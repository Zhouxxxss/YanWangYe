package com.ywy.study.plan;

import java.util.List;
import java.util.Map;

/**
 * 学习计划模板服务接口。实现见 {@code plan.impl.PlanTemplateServiceImpl}。
 */
public interface PlanTemplateService {

    List<Map<String, String>> listTemplates();

    Map<String, Object> importTemplate(String templateId);
}