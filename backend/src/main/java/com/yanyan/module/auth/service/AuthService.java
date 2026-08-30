package com.yanyan.module.auth.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yanyan.common.exception.BizException;
import com.yanyan.module.auth.dto.AuthResponse;
import com.yanyan.module.auth.dto.LoginRequest;
import com.yanyan.module.auth.dto.RegisterRequest;
import com.yanyan.module.auth.entity.User;
import com.yanyan.module.auth.mapper.UserMapper;
import com.yanyan.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 认证服务：注册 / 登录 / 退出。Redis 记录会话，支撑主动下线与单点会话。
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final StringRedisTemplate stringRedisTemplate;

    @Value("${yanyan.redis.key-prefix}")
    private String keyPrefix;

    @Value("${yanyan.jwt.expire-ms}")
    private long expireMs;

    public void register(RegisterRequest req) {
        Long cnt = userMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getUsername, req.getUsername()));
        if (cnt != null && cnt > 0) {
            throw new BizException("用户名已存在");
        }
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setNickname(req.getNickname() == null ? req.getUsername() : req.getNickname());
        userMapper.insert(user); // 二期：此处可按角色分配权限点
    }

    public AuthResponse login(LoginRequest req) {
        User user = userMapper.selectOne(
                Wrappers.<User>lambdaQuery().eq(User::getUsername, req.getUsername()));
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BizException("用户名或密码错误");
        }
        String role = "USER"; // 二期：按 user_role 关联读取真实角色
        String access = jwtUtils.createAccessToken(user.getId(), user.getUsername(), role);
        String refresh = jwtUtils.createRefreshToken(user.getId());
        // Redis 写入会话，供过滤器二次校验
        stringRedisTemplate.opsForValue().set(
                keyPrefix + "login:token:" + user.getId(), access, expireMs, TimeUnit.MILLISECONDS);
        return new AuthResponse(access, refresh, user.getId(), user.getUsername(), role);
    }

    public void logout(Long userId) {
        if (userId != null) {
            stringRedisTemplate.delete(keyPrefix + "login:token:" + userId);
        }
    }
}