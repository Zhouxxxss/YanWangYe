package com.ywy.study.plan.impl;

import com.ywy.common.exceptions.BizException;
import com.ywy.common.utils.UserContext;
import com.ywy.study.plan.PlanTemplateService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 学习计划模板导入实现（一期）：数学/英语/政治/专业课 × 基础/强化/冲刺/模考。
 */
@Service
public class PlanTemplateServiceImpl implements PlanTemplateService {

    private static final List<String> TEMPLATES =
            List.of("math-basic", "math-intensive", "english-basic", "politics-strong", "major-sprint");

    private static final Map<String, String> TEMPLATE_NAMES = Map.of(
            "math-basic", "数学 · 基础巩固",
            "math-intensive", "数学 · 强化提升",
            "english-basic", "英语 · 词汇长难句",
            "politics-strong", "政治 · 冲刺背诵",
            "major-sprint", "专业课 · 冲刺模考");

    @Override
    public List<Map<String, String>> listTemplates() {
        return TEMPLATES.stream()
                .map(id -> Map.of("id", id, "name", TEMPLATE_NAMES.getOrDefault(id, id)))
                .toList();
    }

    @Override
    public Map<String, Object> importTemplate(String templateId) {
        if (!TEMPLATES.contains(templateId)) {
            throw new BizException("模板不存在");
        }
        Long userId = UserContext.uid();
        // TODO: 解析模板 → 生成 schedule_task（每日/每周复习任务）
        return Map.of("templateId", templateId, "name", TEMPLATE_NAMES.get(templateId),
                "userId", userId, "generated", 0, "todo", "接入 schedule_task 批生成");
    }
}