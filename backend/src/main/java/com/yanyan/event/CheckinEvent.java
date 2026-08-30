package com.yanyan.event;

import lombok.Getter;

/** 打卡事件（背诵打卡/每日签到）。二期：连续打卡、社区动态流消费。 */
@Getter
public class CheckinEvent extends DomainEvent {

    private final String bizType; // CHECKIN / RECITE

    public CheckinEvent(Long userId, String bizType) {
        super(userId);
        this.bizType = bizType;
    }
}