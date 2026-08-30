package com.yanyan.task;

import com.yanyan.module.anki.AnkiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务（一期）：
 * 每日推送"今日待复习错题/背诵卡片"提醒（Redis 到期集合 → 站内信通知）。
 * TODO: 接入 notification 表做推送；旺季等频率策略在此调整 cron 即可。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AnkiReminderTask {

    private final AnkiService ankiService;

    /** 每天 08:00 扫描到期集合并推送提醒（占位，按需开启 cron 表达式）。 */
    @Scheduled(cron = "0 0 8 * * ?")
    public void pushDueReminders() {
        // TODO: 遍历活跃用户，取 anki.due<today> 集合触发站内信
        log.info("[task] anki due reminder scan started");
    }
}