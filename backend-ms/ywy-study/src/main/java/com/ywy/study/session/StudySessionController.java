package com.ywy.study.session;

import com.ywy.common.result.R;
import com.ywy.common.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/study")
@RequiredArgsConstructor
public class StudySessionController {

    private final StudySessionService service;

    @PostMapping("/session/start")
    public R<Map<String, Long>> start(@RequestParam String subject) {
        return R.ok(Map.of("sessionId", service.start(UserContext.uid(), subject)));
    }

    @PostMapping("/session/{id}/end")
    public R<Void> end(@PathVariable Long id) {
        service.end(UserContext.uid(), id);
        return R.ok();
    }

    @PostMapping("/checkin")
    public R<Boolean> checkin() {
        return R.ok(service.checkin(UserContext.uid()));
    }
}