package com.yanyan.module.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yanyan.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 学习时段记录（计时器/番茄钟落库）。study_stat_daily 预聚合服务据此产出大盘数据。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("study_session")
public class StudySession extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String subject;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    /** 秒 */
    private Integer durationSeconds;

    /** 来源：TIMER / POMODORO / REVIEW / RECITE */
    private String source;
}