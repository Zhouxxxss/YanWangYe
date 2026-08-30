package com.yanyan.module.dashboard.service;

import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 数据大盘服务（一期）。
 * 数据源聚合：签到 / 学习时长 / 任务完成率 / 错题复习 / 背诵打卡。
 * 性能策略：读取 study_stat_daily 预聚合表 + Redis 缓存，避免实时全表扫描；
 *        热力图与科目占比支持周/月切换。
 * TODO: 预聚合定时任务 + 查询实现。
 */
@Service
public class DashboardService {

    public Map<String, Object> summary(Long userId, String period) {
        // TODO: study_stat_daily 聚合
        return Map.of(
                "userId", userId,
                "period", period,
                "studySeconds", 0,
                "checkinDays", 0,
                "taskDoneRate", 0.0,
                "wrongReviewDone", 0,
                "reciteDone", 0,
                "heatmap", java.util.Map.of(),
                "subjectShare", java.util.Map.of());
    }
}