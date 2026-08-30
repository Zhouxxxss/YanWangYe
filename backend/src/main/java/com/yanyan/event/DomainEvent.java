package com.yanyan.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * 领域事件一期即发布（仅日志消费），供二期社区动态流/小组排行榜零侵入扩展。
 * EventA 封装 userId + 发生时间；业务负载放具体子类。
 */
@Getter
@RequiredArgsConstructor
public abstract class DomainEvent {

    private final Long userId;
    private final LocalDateTime occurredAt = LocalDateTime.now();
}