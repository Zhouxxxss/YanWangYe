package com.ywy.study.session.impl;

import com.ywy.study.session.StudySession;
import com.ywy.study.session.StudySessionMapper;
import com.ywy.study.session.StudySessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学习会话服务实现。番茄钟在前端由 rAF 驱动，结束仅调导本接口落库。
 */
@Service
@RequiredArgsConstructor
public class StudySessionServiceImpl implements StudySessionService {

    private final StudySessionMapper mapper;
    private final StringRedisTemplate redis;

    @Value("${ywy.study.checkin-prefix:checkin:}")
    private String checkinPrefix;

    @Override
    public Long start(Long userId, String subject) {
        StudySession s = new StudySession();
        s.setUserId(userId);
        s.setSubject(subject == null || subject.isBlank() ? "other" : subject);
        s.setStartTime(LocalDateTime.now());
        s.setDurationSeconds(0);
        s.setSource("TIMER");
        mapper.insert(s);
        return s.getId();
    }

    @Override
    public void end(Long userId, Long sessionId) {
        StudySession s = mapper.selectById(sessionId);
        if (s == null || !s.getUserId().equals(userId)) {
            return;
        }
        LocalDateTime end = LocalDateTime.now();
        s.setEndTime(end);
        s.setDurationSeconds((int) Duration.between(s.getStartTime(), end).getSeconds());
        mapper.updateById(s);
    }

    @Override
    public boolean checkin(Long userId) {
        String key = checkinPrefix + LocalDate.now() + ":" + userId;
        Long added = redis.opsForSet().add(key, String.valueOf(userId));
        redis.expire(key, Duration.ofDays(2));
        return added != null && added > 0;
    }
}