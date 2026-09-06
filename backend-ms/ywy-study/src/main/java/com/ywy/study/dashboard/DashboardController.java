package com.ywy.study.dashboard;

import com.ywy.common.result.R;
import com.ywy.common.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;

    @GetMapping("/summary")
    public R<Map<String, Object>> summary(@RequestParam(defaultValue = "week") String period) {
        return R.ok(service.summary(UserContext.uid(), period));
    }
}