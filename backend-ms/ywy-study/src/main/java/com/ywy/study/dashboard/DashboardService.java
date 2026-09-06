package com.ywy.study.dashboard;

import java.util.Map;

/**
 * 数据大盘服务接口。实现见 {@code dashboard.impl.DashboardServiceImpl}。
 */
public interface DashboardService {

    Map<String, Object> summary(Long userId, String period);
}