package com.yanyan.module.auth.controller;

import com.yanyan.common.result.R;
import com.yanyan.module.auth.dto.AuthResponse;
import com.yanyan.module.auth.dto.LoginRequest;
import com.yanyan.module.auth.dto.RegisterRequest;
import com.yanyan.module.auth.service.AuthService;
import com.yanyan.security.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public R<Void> register(@Valid @RequestBody RegisterRequest req) {
        authService.register(req);
        return R.ok();
    }

    @PostMapping("/login")
    public R<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return R.ok(authService.login(req));
    }

    @PostMapping("/logout")
    public R<Void> logout() {
        authService.logout(LoginUser.uid());
        return R.ok();
    }

    @GetMapping("/me")
    public R<AuthResponse> me() {
        LoginUser u = LoginUser.get();
        return R.ok(new AuthResponse(null, null, u.getUserId(), u.getUsername(), u.getRole()));
    }
}