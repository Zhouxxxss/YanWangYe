package com.yanyan.module.recite.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yanyan.common.entity.BaseEntity;
import com.yanyan.module.anki.Reviewable;
import com.yanyan.module.anki.Sm2;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 背诵卡片（政治/专业课）。同样实现 Reviewable，与错题共用 Anki 调度。
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

    /** 背诵状态：LEARNING / MASTERED */
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
        var r = Sm2.review(quality, getRepetition(), getIntervalDays(), getEase());
        this.repetition = r.repetition();
        this.intervalDays = r.intervalDays();
        this.ease = r.ease();
        this.nextReviewDate = LocalDate.now().plusDays(r.intervalDays());
        return nextReviewDate;
    }
}