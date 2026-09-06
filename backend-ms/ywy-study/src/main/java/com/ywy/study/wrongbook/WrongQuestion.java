package com.ywy.study.wrongbook;

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
 * 错题。实现 Reviewable 接入遗忘曲线调度（SM-2）。
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

    /** 错题内容 */
    private String content;

    private String myAnswer;

    private String correctAnswer;

    /** 错因：careless/knowledge/method/other */
    private String errorReason;

    /** 截图/附件 URL（对象存储） */
    private String imageUrl;

    private Integer repetition;
    private Integer intervalDays;
    private Double ease;
    private LocalDate nextReviewDate;

    @Override
    public String reviewKey() {
        return "wrong_question:" + id;
    }

    /** SM-2 上报 + 持久化复习字段。 */
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