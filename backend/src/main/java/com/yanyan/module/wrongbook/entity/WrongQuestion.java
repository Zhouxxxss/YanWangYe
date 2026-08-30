package com.yanyan.module.wrongbook.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yanyan.common.entity.BaseEntity;
import com.yanyan.module.anki.Reviewable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 错题。实现 Reviewable 接入统一复习调度（Anki）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wrong_question")
public class WrongQuestion extends BaseEntity implements Reviewable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    /** 科目：math/english/politics/major */
    private String subject;

    /** 章节/知识点 */
    private String chapter;

    /** 错题内容（文本/富文本） */
    private String content;

    /** 我的答案 */
    private String myAnswer;

    /** 正确答案 */
    private String correctAnswer;

    /** 错因：careless/knowledge/method/other */
    private String errorReason;

    /** 截图/附件 URL（对象存储） */
    private String imageUrl;

    // ---- SM-2 复习字段 ----
    private Integer repetition;
    private Integer intervalDays;
    private Double ease;
    private LocalDate nextReviewDate;

    @Override
    public String reviewKey() {
        return "wrong_question:" + id;
    }

    /** 上报复习结果：按 SM-2 计算并持久化复习字段。 */
    @Override
    public LocalDate applyReview(int quality) {
        var r = com.yanyan.module.anki.Sm2.review(quality, getRepetition(), getIntervalDays(), getEase());
        this.repetition = r.repetition();
        this.intervalDays = r.intervalDays();
        this.ease = r.ease();
        this.nextReviewDate = LocalDate.now().plusDays(r.intervalDays());
        return nextReviewDate;
    }
}