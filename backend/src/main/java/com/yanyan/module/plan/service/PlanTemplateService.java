package com.yanyan.module.plan.service;

import com.yanyan.common.exception.BizException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 计划模板导入服务（一期）。
 * 模板库：数学/英语/政治/专业课 × 基础/强化/冲刺/模考（内置数量有限）。
 * 核心：一键导入 ⇒ 批量生成周/日复习日程（复用既有 schedule_task 能力）。
 * TODO: 接入既有 schedule_task 表后，在此完成模板→日程的批生成与可自定义修改。
 */
@Service
public class PlanTemplateService {

    private static final List<String> TEMPLATES =
            List.of("math-basic", "math-intensive", "english-basic", "politics-strong", "major-sprint");

    public List<String> listTemplates() {
        return TEMPLATES;
    }

    public Map<String, Object> importTemplate(Long userId, String templateId) {
        if (!TEMPLATES.contains(templateId)) {
            throw new BizException("模板不存在");
        }
        // TODO: 解析模板 → 生成 schedule_task（每日/每周复习任务）
        // duration 联动 study_stat_daily 统计，形成"规划-执行-统计"闭环
        return Map.of("templateId", templateId, "userId", userId, "generated", 0, "todo", "接入 schedule_task");
    }
}