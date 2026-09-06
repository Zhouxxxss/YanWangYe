package com.ywy.study.anki;

/**
 * SM-2 间隔重复算法（Anki 核心，错题遗忘曲线与背诵卡片共用）。
 * 纯函数，便于单测；调度与存储由 {@link AnkiService} 负责。
 */
public final class Sm2 {

    private Sm2() {}

    public static final double DEFAULT_EASE = 2.5;
    public static final int MIN_INTERVAL_DAYS = 1;

    public static Result review(int quality, int repetition, int previousInterval, double previousEase) {
        if (quality < 3) {
            return new Result(1, MIN_INTERVAL_DAYS, previousEase, true);
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
        double newEase = previousEase
                + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02));
        newEase = Math.max(newEase, 1.3);
        return new Result(newRep, interval, newEase, false);
    }

    public static boolean isDue(java.time.LocalDate nextReviewDate) {
        return nextReviewDate != null && !nextReviewDate.isAfter(java.time.LocalDate.now());
    }

    public record Result(int repetition, int intervalDays, double ease, boolean needRelearn) {}
}