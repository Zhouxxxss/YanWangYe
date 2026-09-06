package com.ywy.study.recite;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ywy.common.domain.entity.BaseEntity;
import com.ywy.study.anki.Reviewable;
import com.ywy.study.anki.Sm2;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 背诵卡片（政治/专业课）。与错题共用遗忘曲线调度。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("recite_card")
public class ReciteCard extends BaseEntity implements Reviewable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String subject;

    private String chapter;

    private String content;

    /** LEARNING / MASTERED */
    private String status;

    private Integer repetition;
    private Integer intervalDays;
    private Double ease;
    private LocalDate nextReviewDate;

    @Override
    public String reviewKey() {
        return "recite_card:" + id;
    }

    @Override
    public LocalDate applyReview(int quality) {
        var r = Sm2.review(quality, repetition, intervalDays, ease);
        this.repetition = r.repetition();
        this.intervalDays = r.intervalDays();
        this.ease = r.ease();
        this.nextReviewDate = LocalDate.now().plusDays(r.intervalDays());
        return nextReviewDate;
    }
}