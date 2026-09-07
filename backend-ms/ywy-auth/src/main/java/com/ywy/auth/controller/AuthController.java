package com.ywy.auth.controller;

import com.ywy.auth.domain.dto.AuthResponse;
import com.ywy.auth.domain.dto.CredentialUpdateRequest;
import com.ywy.auth.domain.dto.LoginRequest;
import com.ywy.auth.domain.dto.RegisterRequest;
import com.ywy.auth.domain.dto.WxLoginRequest;
import com.ywy.auth.service.AuthService;
import com.ywy.common.result.R;
import com.ywy.common.utils.UserContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public R<Void> register(@Valid @RequestBody RegisterRequest req) {
        authService.register(req);
        return R.ok();
    }

//    @PostMapping("/login")
//    public R<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
//        return R.ok(authService.login(req));
//    }

    @PostMapping("/wx/login")
    public R<AuthResponse> wxLogin(@Valid @RequestBody WxLoginRequest req) {
        return R.ok(authService.wxLogin(req.getCode()));
    }

    @PostMapping("/me/credential")
    public R<AuthResponse> updateCredential(@Valid @RequestBody CredentialUpdateRequest req) {
        return R.ok(authService.updateCredential(UserContext.uid(), req));
    }

    @PostMapping("/refresh")
    public R<AuthResponse> refresh(@RequestParam String refreshToken) {
        return R.ok(authService.refresh(refreshToken));
    }

    @PostMapping("/logout")
    public R<Void> logout() {
        authService.logout();
        return R.ok();
    }

    @PostMapping("/me")
    public R<AuthResponse> me() {
        return R.ok(authService.me());
    }
}