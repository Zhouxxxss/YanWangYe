package com.ywy.auth.service;

import com.ywy.auth.dto.AuthResponse;
import com.ywy.auth.dto.LoginRequest;
import com.ywy.auth.dto.RegisterRequest;

/**
 * 认证服务接口：注册/登录/刷新/退出/当前用户。实现见 {@code service.impl.AuthServiceImpl}。
 */
public interface AuthService {

    void register(RegisterRequest req);

    AuthResponse login(LoginRequest req);

    AuthResponse refresh(String refreshToken);

    void logout();

    AuthResponse me();
}