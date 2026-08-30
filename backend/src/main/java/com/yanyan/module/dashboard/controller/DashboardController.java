package com.yanyan.module.dashboard.controller;

import com.yanyan.common.result.R;
import com.yanyan.module.dashboard.service.DashboardService;
import com.yanyan.security.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;

    @GetMapping("/summary")
    public R<Map<String, Object>> summary(
            @RequestParam(defaultValue = "week") String period) {
        return R.ok(service.summary(LoginUser.uid(), period));
    }
}