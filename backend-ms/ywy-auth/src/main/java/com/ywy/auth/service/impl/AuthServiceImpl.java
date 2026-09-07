package com.ywy.auth.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ywy.auth.domain.po.User;
import com.ywy.auth.domain.dto.AuthResponse;
import com.ywy.auth.domain.dto.CredentialUpdateRequest;
import com.ywy.auth.domain.dto.LoginRequest;
import com.ywy.auth.domain.dto.RegisterRequest;
import com.ywy.auth.mapper.UserMapper;
import com.ywy.auth.service.AuthService;
import com.ywy.auth.wx.WxMpClient;
import com.ywy.common.exceptions.BizException;
import com.ywy.common.utils.JwtUtils;
import com.ywy.common.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现。认证链路 = JWT 签名 + Redis 双 token 会话：
 *   请求(earer) → 网关校验签名并透传身份 → 本服务仅在下发/刷新令牌时写 Redis，
 *   不按请求查库。双 token：access(短时，滑动) + refresh(长时，可轮换)。
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final StringRedisTemplate redis;
    private final WxMpClient wxMpClient;

    @Value("${ywy.auth.session-prefix:login:token:}")
    private String sessionPrefix;

    private static final int ACCESS = 0;
    private static final int REFRESH = 1;

    private String sessionKey(Long userId, int kind) {
        return sessionPrefix + userId + (kind == REFRESH ? ":refresh" : ":access");
    }

    @Override
    public void register(RegisterRequest req) {
        Long cnt = userMapper.selectCount(
                Wrappers.<User>lambdaQuery().eq(User::getUsername, req.getUsername()));
        if (cnt != null && cnt > 0) {
            throw new BizException("用户名已存在");
        }
        User user = new User();
        user.setAccount(req.getUsername());
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setStatus("NORMAL");
        user.setRole("USER");
        userMapper.insert(user);
        if (req.getNickname() == null || req.getNickname().isBlank()) {
            userMapper.update(null, Wrappers.<User>update()
                    .eq("id", user.getId())
                    .set("nickname", "研友" + user.getId()));
        } else {
            userMapper.update(null, Wrappers.<User>update()
                    .eq("id", user.getId())
                    .set("nickname", req.getNickname()));
        }
    }

    @Override
    public AuthResponse login(LoginRequest req) {
        // 标识可用「登录名」或「账号(公众号 openid，若已设置密码)」
        User user = userMapper.selectOne(
                Wrappers.<User>lambdaQuery().eq(User::getUsername, req.getUsername()));
        if (user == null) {
            user = userMapper.selectOne(
                    Wrappers.<User>lambdaQuery().eq(User::getAccount, req.getUsername()));
        }
        if (user == null) {
            throw new BizException("用户名或密码错误");
        }
        // 公众号自动注册用户未设置密码，账号密码登录前需在「我的」页补设密码
        if (user.getPassword() == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BizException("用户名或密码错误");
        }
        if (!"NORMAL".equals(user.getStatus())) {
            throw new BizException("账号已被禁用");
        }
        return toAuthResponse(user);
    }

    @Override
    public AuthResponse wxLogin(String code) {
        String openid = wxMpClient.codeToOpenid(code);
        User user = userMapper.selectOne(
                Wrappers.<User>lambdaQuery().eq(User::getAccount, openid));
        if (user == null) {
            // 关注自动注册：默认账号，密码为空，用户名待用户在「我的」页补充
            user = new User();
            user.setAccount(openid);
            user.setUsername(null);
            user.setPassword(null);
            user.setStatus("NORMAL");
            user.setRole("USER");
            userMapper.insert(user);
            userMapper.update(null, Wrappers.<User>update()
                    .eq("id", user.getId())
                    .set("nickname", "研友" + user.getId()));
        }
        if (!"NORMAL".equals(user.getStatus())) {
            throw new BizException("账号已被禁用");
        }
        return toAuthResponse(user);
    }

    @Override
    public AuthResponse refresh(String refreshToken) {
        var claims = jwtUtils.parse(refreshToken);
        if (claims == null || jwtUtils.getUserId(claims) == null) {
            throw new BizException("refresh token 无效或已过期");
        }
        Long userId = jwtUtils.getUserId(claims);
        String stored = redis.opsForValue().get(sessionKey(userId, REFRESH));
        if (stored == null || !stored.equals(refreshToken)) {
            throw new BizException("请重新登录");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        return toAuthResponse(user);
    }

    @Override
    public AuthResponse updateCredential(Long userId, CredentialUpdateRequest req) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        boolean changed = false;
        if (req.getNewUsername() != null && !req.getNewUsername().isBlank()) {
            Long cnt = userMapper.selectCount(Wrappers.<User>lambdaQuery()
                    .eq(User::getUsername, req.getNewUsername())
                    .ne(User::getId, userId));
            if (cnt != null && cnt > 0) {
                throw new BizException("用户名已存在");
            }
            user.setUsername(req.getNewUsername());
            changed = true;
        }
        if (req.getNewPassword() != null && !req.getNewPassword().isBlank()) {
            if (user.getPassword() != null
                    && (req.getCurrentPassword() == null
                    || !passwordEncoder.matches(req.getCurrentPassword(), user.getPassword()))) {
                throw new BizException("当前密码错误");
            }
            user.setPassword(passwordEncoder.encode(req.getNewPassword()));
            changed = true;
        }
        if (!changed) {
            throw new BizException("没有需要修改的内容");
        }
        userMapper.updateById(user);
        // 凭据变更后轮换令牌，令新用户名/新会话立即生效
        return toAuthResponse(user);
    }

    private AuthResponse toAuthResponse(User user) {
        String role = user.getRole() == null ? "USER" : user.getRole();
        String access = jwtUtils.createToken(user.getId(), user.getUsername(), role);
        String refresh = jwtUtils.createRefreshToken(user.getId());
        redis.opsForValue().set(sessionKey(user.getId(), ACCESS), access,
                jwtUtils.getExpireMs(), TimeUnit.MILLISECONDS);
        redis.opsForValue().set(sessionKey(user.getId(), REFRESH), refresh,
                jwtUtils.getRefreshExpireMs(), TimeUnit.MILLISECONDS);
        return new AuthResponse(access, refresh, user.getId(), user.getUsername(), role);
    }

    @Override
    public void logout() {
        Long userId = UserContext.uid();
        if (userId != null) {
            redis.delete(sessionKey(userId, ACCESS));
            redis.delete(sessionKey(userId, REFRESH));
        }
    }

    @Override
    public AuthResponse me() {
        Long userId = UserContext.uid();
        if (userId == null) {
            throw new BizException(401, "未登录");
        }
        User user = userMapper.selectById(userId);
        String role = user == null ? "USER" : (user.getRole() == null ? "USER" : user.getRole());
        return new AuthResponse(null, null, userId,
                user == null ? "" : user.getUsername(), role);
    }
}