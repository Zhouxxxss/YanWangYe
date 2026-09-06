package com.ywy.study.session;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ywy.common.domain.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 学习计时会话记录。结束一次计时即落库，供数据大盘聚合。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("study_session")
public class StudySession extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    /** 科目：math/english/politics/major/other */
    private String subject;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer durationSeconds;

    /** 来源：TIMER（番茄钟）/ MANUAL */
    private String source;
}