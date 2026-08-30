package com.yanyan.module.anki;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 复习调度服务：把 SM-2 结果落到 Redis 待复习集合，并在定时任务中拉取"今日待复习"。
 * 错题与背诵卡片都通过 {@link Reviewable} 注入。
 */
@Service
@RequiredArgsConstructor
public class AnkiService {

    private final StringRedisTemplate redis;

    @Value("${yanyan.redis.key-prefix}")
    private String prefix;

    /** 待复习集合 key：anki:due:yyyyMMdd:uid */
    private String dueKey(LocalDate date, Long userId) {
        return prefix + "anki:due:" + date.format(DateTimeFormatter.BASIC_ISO_DATE) + ":" + userId;
    }

    /**
     * 复习完成：更新调度并登记到新到期日。
     * 使用 Redis Set 去重，天然适配多实例（配合后续分布式锁防并发重复）。
     */
    public void scheduleNext(Reviewable item, Long userId, LocalDate today) {
        LocalDate next = item.getNextReviewDate();
        if (next != null) {
            redis.opsForSet().add(dueKey(next, userId), item.reviewKey());
        }
    }

    /** 取今天到期任务 key 列表（供当日拉取渲染用）。 */
    public java.util.Set<String> dueItems(Long userId, LocalDate date) {
        return redis.opsForSet().members(dueKey(date, userId));
    }

    /** 维护会话/限流的示例时钟（业务模块可复用）。 */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}