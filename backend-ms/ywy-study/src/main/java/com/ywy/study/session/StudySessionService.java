package com.ywy.study.session;

/**
 * 学习会话服务接口：开始/结束计时、每日签到。实现见 {@code session.impl.StudySessionServiceImpl}。
 */
public interface StudySessionService {

    Long start(Long userId, String subject);

    void end(Long userId, Long sessionId);

    /** 返回是否今日首次签到。 */
    boolean checkin(Long userId);
}