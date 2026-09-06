package com.ywy.study.anki;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 复习调度服务：打分后把条目登记到「到期日」Redis Set，供定时任务拉取今日待复习。
 */
@Service
@RequiredArgsConstructor
public class AnkiService {

    private final StringRedisTemplate redis;

    @Value("${ywy.study.anki-prefix:anki:due:}")
    private String prefix;

    private String dueKey(LocalDate date, Long userId) {
        return prefix + date + ":" + userId;
    }

    /** 打分完成后登记到新到期日。Redis Set 天然去重，多实例配合分布式锁防并发重复。 */
    public void scheduleNext(Reviewable item, Long userId, LocalDate today) {
        LocalDate next = item.getNextReviewDate();
        if (next != null) {
            redis.opsForSet().add(dueKey(next, userId), item.reviewKey());
        }
    }

    public Set<String> dueItems(Long userId, LocalDate date) {
        return redis.opsForSet().members(dueKey(date, userId));
    }

    /** 今日到期标识数量（供数据大盘/卡片角标展示）。 */
    public long todayDueCount(Long userId) {
        Set<String> set = redis.opsForSet().members(dueKey(LocalDate.now(), userId));
        return set == null ? 0 : set.size();
    }

    /** 到期条目带生存期，避免无限累积。 */
    public void expireIfNeeded(Long userId, LocalDate date) {
        redis.expire(dueKey(date, userId), 7, TimeUnit.DAYS);
    }
}