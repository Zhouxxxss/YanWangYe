package com.yanyan.module.recite.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanyan.common.exception.BizException;
import com.yanyan.event.CheckinEvent;
import com.yanyan.module.anki.AnkiService;
import com.yanyan.module.anki.Sm2;
import com.yanyan.module.recite.entity.ReciteCard;
import com.yanyan.module.recite.mapper.ReciteCardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * 背诵打卡服务。
 * AI 抽背问题池（一期占位）：由 RAG 的 chat 生成并缓存，避免每次实时调用。
 */
@Service
@RequiredArgsConstructor
public class ReciteService {

    private final ReciteCardMapper mapper;
    private final AnkiService ankiService;
    private final ApplicationEventPublisher publisher;

    public Page<ReciteCard> page(Long userId, int page, int size, String subject) {
        return mapper.selectPage(new Page<>(page, size), new LambdaQueryWrapper<ReciteCard>()
                .eq(ReciteCard::getUserId, userId)
                .eq(subject != null && !subject.isBlank(), ReciteCard::getSubject, subject)
                .orderByDesc(ReciteCard::getCreateTime));
    }

    public Long add(Long userId, ReciteCard card) {
        card.setId(null);
        card.setUserId(userId);
        card.setStatus("LEARNING");
        card.setRepetition(0);
        card.setIntervalDays(0);
        card.setEase(Sm2.DEFAULT_EASE);
        mapper.insert(card);
        return card.getId();
    }

    /** 背诵打卡：打分并调度，发布 CheckinEvent（时长计入学习统计由预聚合消费）。 */
    public void recite(Long userId, Long id, int quality) {
        ReciteCard card = mapper.selectById(id);
        if (card == null || !card.getUserId().equals(userId)) {
            throw new BizException("卡片不存在");
        }
        card.applyReview(quality);
        if (card.getRepetition() >= 3) {
            card.setStatus("MASTERED");
        }
        mapper.updateById(card);
        ankiService.scheduleNext(card, userId, LocalDate.now());
        publisher.publishEvent(new CheckinEvent(userId, "RECITE"));
    }
}