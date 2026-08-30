package com.yanyan.module.anki;

/**
 * SM-2 间隔重复算法（Anki 核心，错题与背诵卡片共用）。
 *
 * <p>按 4 个质量评分（0-3 重新计、4-5 掌握）计算下次复习间隔。
 * 本类为纯函数，便于单测，调度与存储由上层 AnkiService / Redis 负责。</p>
 */
public final class Sm2 {

    private Sm2() {}

    public static final double DEFAULT_EASE = 2.5;
    public static final int MIN_INTERVAL_DAYS = 1;

    /**
     * 计算一次复习后的新状态。
     *
     * @param quality 用户自评质量 0-5（0 完全忘记 ~ 5 完全掌握）
     * @param repetition 已是第几次成功复习
     * @param previousInterval 上次间隔（天）
     * @param previousEase 上次难度系数（如 2.5）
     */
    public static Result review(int quality, int repetition, int previousInterval, double previousEase) {
        // 标准 SM-2：<3 视为忘记，重置
        if (quality < 3) {
            return new Result(1 /*repetition重置*/, MIN_INTERVAL_DAYS, previousEase, true /*needRelearn*/);
        }
        int newRep = repetition + 1;
        int interval;
        if (newRep == 1) {
            interval = 1;
        } else if (newRep == 2) {
            interval = 6;
        } else {
            interval = (int) Math.round(previousInterval * previousEase);
        }
        // EF' = EF + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))，下限 1.3
        double newEase = previousEase
                + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02));
        newEase = Math.max(newEase, 1.3);
        return new Result(newRep, interval, newEase, false);
    }

    /** 当日到期判断（复习周期字段保存的下次复习日）。 */
    public static boolean isDue(java.time.LocalDate nextReviewDate) {
        return nextReviewDate != null && !nextReviewDate.isAfter(java.time.LocalDate.now());
    }

    public record Result(int repetition, int intervalDays, double ease, boolean needRelearn) {}
}