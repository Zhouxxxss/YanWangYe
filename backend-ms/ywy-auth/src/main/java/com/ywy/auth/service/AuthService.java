package com.ywy.auth.service;

import com.ywy.auth.domain.dto.AuthResponse;
import com.ywy.auth.domain.dto.CredentialUpdateRequest;
import com.ywy.auth.domain.dto.LoginRequest;
import com.ywy.auth.domain.dto.RegisterRequest;

/**
 * 认证服务接口：注册/登录/刷新/退出/当前用户。实现见 {@code service.impl.AuthServiceImpl}。
 * 认证链路为 JWT + Redis 双 token 会话，不依赖额外认证表。
 */
public interface AuthService {

    void register(RegisterRequest req);

    AuthResponse login(LoginRequest req);

    /** 公众号关注自动注册 + 登录：用 code 换 openid，命中 account 则登录，否则建默认账号后登录 */
    AuthResponse wxLogin(String code);

    AuthResponse refresh(String refreshToken);

    /** 「我的」页设/改用户名与密码，成功后轮换会话令牌 */
    AuthResponse updateCredential(Long userId, CredentialUpdateRequest req);

    void logout();

    AuthResponse me();
}