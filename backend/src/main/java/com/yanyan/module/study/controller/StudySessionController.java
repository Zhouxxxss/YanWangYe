package com.yanyan.module.study.controller;

import com.yanyan.common.result.R;
import com.yanyan.module.study.service.StudySessionService;
import com.yanyan.security.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 学习计时/签到入口（一期）。
 * 前端番茄钟倒计时由 rAF 驱动，结束仅调导这个接口落库。
 */
@RestController
@RequestMapping("/api/v1/study")
@RequiredArgsConstructor
public class StudySessionController {

    private final StudySessionService service;

    @PostMapping("/session/start")
    public R<Map<String, Long>> start(@RequestParam String subject) {
        return R.ok(Map.of("sessionId", service.start(LoginUser.uid(), subject)));
    }

    @PostMapping("/session/{id}/end")
    public R<Void> end(@PathVariable Long id) {
        service.end(LoginUser.uid(), id);
        return R.ok();
    }

    @PostMapping("/checkin")
    public R<Void> checkin() {
        service.checkin(LoginUser.uid());
        return R.ok();
    }
}