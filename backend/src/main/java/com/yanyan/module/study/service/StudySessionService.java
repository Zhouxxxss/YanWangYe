package com.yanyan.module.study.service;

import com.yanyan.event.StudyCompletedEvent;
import com.yanyan.event.CheckinEvent;
import com.yanyan.module.study.entity.StudySession;
import com.yanyan.module.study.mapper.StudySessionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 学习会话服务：结束一次计时即入库并发布领域事件。
 * 打卡成功联动 CheckinEvent；时长同步到大盘由预聚合任务消费。
 */
@Service
@RequiredArgsConstructor
public class StudySessionService {

    private final StudySessionMapper mapper;
    private final ApplicationEventPublisher publisher;

    public Long start(Long userId, String subject) {
        StudySession s = new StudySession();
        s.setUserId(userId);
        s.setSubject(subject);
        s.setStartTime(LocalDateTime.now());
        s.setDurationSeconds(0);
        s.setSource("TIMER");
        mapper.insert(s);
        return s.getId();
    }

    public void end(Long userId, Long sessionId) {
        StudySession s = mapper.selectById(sessionId);
        if (s == null || !s.getUserId().equals(userId)) {
            return;
        }
        LocalDateTime end = LocalDateTime.now();
        s.setEndTime(end);
        s.setDurationSeconds((int) Duration.between(s.getStartTime(), end).getSeconds());
        mapper.updateById(s);
        publisher.publishEvent(new StudyCompletedEvent(userId, s.getDurationSeconds(), s.getSubject()));
    }

    /** 每日签到占位：由签到接口调用，联动 CheckinEvent。 */
    public void checkin(Long userId) {
        publisher.publishEvent(new CheckinEvent(userId, "CHECKIN"));
    }
}