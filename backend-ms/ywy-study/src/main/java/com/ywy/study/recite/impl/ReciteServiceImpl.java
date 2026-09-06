package com.ywy.study.recite.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ywy.common.exceptions.BizException;
import com.ywy.common.utils.UserContext;
import com.ywy.study.anki.AnkiService;
import com.ywy.study.anki.Sm2;
import com.ywy.study.common.PageVO;
import com.ywy.study.recite.ReciteCard;
import com.ywy.study.recite.ReciteCardMapper;
import com.ywy.study.recite.ReciteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * 背诵打卡服务实现。AI 抽背问题池一期由 RAG 提供（延后通联），此处以手动建卡为主。
 */
@Service
@RequiredArgsConstructor
public class ReciteServiceImpl implements ReciteService {

    private final ReciteCardMapper mapper;
    private final AnkiService ankiService;

    @Override
    public PageVO<ReciteCard> page(Long userId, int page, int size, String subject) {
        return PageVO.from(mapper.selectPage(new Page<>(page, size), new LambdaQueryWrapper<ReciteCard>()
                .eq(ReciteCard::getUserId, userId)
                .eq(subject != null && !subject.isBlank(), ReciteCard::getSubject, subject)
                .orderByDesc(ReciteCard::getCreateTime)));
    }

    @Override
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

    @Override
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
    }

    @Override
    public long todayDue(Long userId) {
        return ankiService.todayDueCount(userId);
    }
}