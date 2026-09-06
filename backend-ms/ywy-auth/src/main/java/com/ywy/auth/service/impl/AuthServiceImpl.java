package com.ywy.auth.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ywy.auth.domain.po.User;
import com.ywy.auth.dto.AuthResponse;
import com.ywy.auth.dto.LoginRequest;
import com.ywy.auth.dto.RegisterRequest;
import com.ywy.auth.mapper.UserMapper;
import com.ywy.auth.service.AuthService;
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
 * 认证服务实现：注册/登录/退出/当前用户。Redis 记录会话，支撑单点会话与主动下线。
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final StringRedisTemplate redis;

    @Value("${ywy.auth.session-prefix:login:token:}")
    private String sessionPrefix;

    private String loginKey(Long userId) {
        return sessionPrefix + userId;
    }

    @Override
    public void register(RegisterRequest req) {
        Long cnt = userMapper.selectCount(
                Wrappers.<User>lambdaQuery().eq(User::getUsername, req.getUsername()));
        if (cnt != null && cnt > 0) {
            throw new BizException("用户名已存在");
        }
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setStatus("NORMAL");
        user.setRole("USER");
        userMapper.insert(user);
    }

    @Override
    public AuthResponse login(LoginRequest req) {
        User user = userMapper.selectOne(
                Wrappers.<User>lambdaQuery().eq(User::getUsername, req.getUsername()));
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BizException("用户名或密码错误");
        }
        if (!"NORMAL".equals(user.getStatus())) {
            throw new BizException("账号已被禁用");
        }
        String role = user.getRole() == null ? "USER" : user.getRole();
        String access = jwtUtils.createToken(user.getId(), user.getUsername(), role);
        String refresh = jwtUtils.createRefreshToken(user.getId());
        redis.opsForValue().set(loginKey(user.getId()), access, jwtUtils.getExpireMs(), TimeUnit.MILLISECONDS);
        return new AuthResponse(access, refresh, user.getId(), user.getUsername(), role);
    }

    @Override
    public AuthResponse refresh(String refreshToken) {
        var claims = jwtUtils.parse(refreshToken);
        if (claims == null || jwtUtils.getUserId(claims) == null) {
            throw new BizException("refresh token 无效或已过期");
        }
        Long userId = jwtUtils.getUserId(claims);
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        String role = user.getRole() == null ? "USER" : user.getRole();
        String access = jwtUtils.createToken(user.getId(), user.getUsername(), role);
        redis.opsForValue().set(loginKey(user.getId()), access, jwtUtils.getExpireMs(), TimeUnit.MILLISECONDS);
        return new AuthResponse(access, null, user.getId(), user.getUsername(), role);
    }

    @Override
    public void logout() {
        Long userId = UserContext.uid();
        if (userId != null) {
            redis.delete(loginKey(userId));
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