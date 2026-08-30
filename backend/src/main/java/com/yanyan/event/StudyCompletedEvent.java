package com.yanyan.event;

import lombok.Getter;

/** 一次学习时长完成事件（计时/番茄钟联动）。二期：动流/进度统计消费。 */
@Getter
public class StudyCompletedEvent extends DomainEvent {

    /** 学习时长（秒） */
    private final int seconds;
    private final String subject;

    public StudyCompletedEvent(Long userId, int seconds, String subject) {
        super(userId);
        this.seconds = seconds;
        this.subject = subject;
    }
}