package com.yanyan.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 一期领域事件日志消费：验证事件链路跑通，为二期社区/小组实时扩展预演。
 * 二期做法：在同包下新增监听器消费同一事件，平滑替换为 MQ 亦不侵入业务。
 */
@Component
public class DomainEventListener {

    @EventListener
    public void onStudy(StudyCompletedEvent e) {
        System.out.println("[event] study completed: user=" + e.getUserId()
                + ", seconds=" + e.getSeconds() + ", subject=" + e.getSubject());
    }

    @EventListener
    public void onCheckin(CheckinEvent e) {
        System.out.println("[event] checkin: user=" + e.getUserId() + ", type=" + e.getBizType());
    }
}