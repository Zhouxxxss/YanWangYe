package com.ywy.study.anki;

import java.time.LocalDate;

/**
 * 复习条目 SPI：错题、背诵卡片等「可复习内容」统一实现本接口，共用 Anki 调度。
 */
public interface Reviewable {

    /** 全局唯一业务标识（如 wrong_question:12 / recite_card:7）。 */
    String reviewKey();

    Integer getRepetition();

    Integer getIntervalDays();

    Double getEase();

    LocalDate getNextReviewDate();

    LocalDate applyReview(int quality);
}