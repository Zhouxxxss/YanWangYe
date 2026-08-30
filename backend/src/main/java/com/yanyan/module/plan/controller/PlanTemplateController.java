package com.yanyan.module.plan.controller;

import com.yanyan.common.result.R;
import com.yanyan.module.plan.service.PlanTemplateService;
import com.yanyan.security.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/plan")
@RequiredArgsConstructor
public class PlanTemplateController {

    private final PlanTemplateService service;

    @GetMapping("/templates")
    public R<List<String>> templates() {
        return R.ok(service.listTemplates());
    }

    @PostMapping("/import")
    public R<Map<String, Object>> importTemplate(@RequestParam String templateId) {
        return R.ok(service.importTemplate(LoginUser.uid(), templateId));
    }
}