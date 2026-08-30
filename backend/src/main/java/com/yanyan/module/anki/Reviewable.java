package com.yanyan.module.anki;

import java.time.LocalDate;

/**
 * 复习条目 SPI：错题、背诵卡片等「可复习内容」统一实现本接口，
 * 从而共用 AnkiService 调度器（业务文档"错题 + 背诵共用一套复习"）。
 *
 * <p>访问器采用标准 {@code getX} 命名，与 Lombok {@code @Data} 生成的 getter 对齐。</p>
 */
public interface Reviewable {

    /** 全局唯一业务标识（如 wrong_question:12 / recite_card:7）。 */
    String reviewKey();

    /** 当前复习重复次数。 */
    Integer getRepetition();

    /** 当前间隔（天）。 */
    Integer getIntervalDays();

    /** 当前难度系数。（可空，缺省用 {@link Sm2#DEFAULT_EASE}） */
    Double getEase();

    /** 下次复习日。 */
    LocalDate getNextReviewDate();

    /** 应用 SM-2 结果，返回更新后的下次复习日。 */
    LocalDate applyReview(int quality);
}