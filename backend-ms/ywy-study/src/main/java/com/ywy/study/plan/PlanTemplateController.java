package com.ywy.study.plan;

import com.ywy.common.result.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/plan")
@RequiredArgsConstructor
public class PlanTemplateController {

    private final PlanTemplateService service;

    @GetMapping("/templates")
    public R<List<Map<String, String>>> templates() {
        return R.ok(service.listTemplates());
    }

    @PostMapping("/import")
    public R<Map<String, Object>> importTemplate(@RequestParam String templateId) {
        return R.ok(service.importTemplate(templateId));
    }
}