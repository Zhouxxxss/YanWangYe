package com.yanyan.module.wrongbook.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanyan.common.exception.BizException;
import com.yanyan.module.anki.AnkiService;
import com.yanyan.module.anki.Sm2;
import com.yanyan.module.wrongbook.entity.WrongQuestion;
import com.yanyan.module.wrongbook.mapper.WrongQuestionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * 错题服务（一期核心）。展示 Anki 调度接入方式。
 * AI 讲解/错因分析环节一期由 {@code module/rag} 提供，见 RAG 服务占位。
 */
@Service
@RequiredArgsConstructor
public class WrongQuestionService {

    private final WrongQuestionMapper mapper;
    private final AnkiService ankiService;

    public Page<WrongQuestion> page(Long userId, int page, int size, String subject) {
        LambdaQueryWrapper<WrongQuestion> qw = new LambdaQueryWrapper<WrongQuestion>()
                .eq(WrongQuestion::getUserId, userId)
                .eq(subject != null && !subject.isBlank(), WrongQuestion::getSubject, subject)
                .orderByDesc(WrongQuestion::getCreateTime);
        return mapper.selectPage(new Page<>(page, size), qw);
    }

    public WrongQuestion get(Long userId, Long id) {
        WrongQuestion wq = mapper.selectById(id);
        if (wq == null || !wq.getUserId().equals(userId)) {
            throw new BizException("错题不存在或无权限");
        }
        return wq;
    }

    public Long add(Long userId, WrongQuestion wq) {
        wq.setId(null);
        wq.setUserId(userId);
        // 新错题按 SM-2 首评间隔初始化为 1 天
        wq.setRepetition(0);
        wq.setIntervalDays(0);
        wq.setEase(Sm2.DEFAULT_EASE);
        mapper.insert(wq);
        return wq.getId();
    }

    /** 提交复习评分（0-5），更新调度并登记到 Redis 待复习集合。 */
    public void review(Long userId, Long id, int quality) {
        WrongQuestion wq = get(userId, id);
        LocalDate next = wq.applyReview(quality);
        mapper.updateById(wq);
        // 联动计时器记录复盘时长的逻辑由学习模块通过领域事件消费
        ankiService.scheduleNext(wq, userId, LocalDate.now());
    }

    public void delete(Long userId, Long id) {
        WrongQuestion wq = get(userId, id);
        mapper.deleteById(wq.getId());
    }
}