package com.ywy.study.wrongbook.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ywy.common.exceptions.BizException;
import com.ywy.common.utils.UserContext;
import com.ywy.study.anki.AnkiService;
import com.ywy.study.anki.Sm2;
import com.ywy.study.common.PageVO;
import com.ywy.study.wrongbook.WrongQuestion;
import com.ywy.study.wrongbook.WrongQuestionMapper;
import com.ywy.study.wrongbook.WrongQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

/**
 * 错题服务实现：维护 + SM-2 复习评分。讲解/错因分析由 RAG 模块兜底（二期通联）。
 */
@Service
@RequiredArgsConstructor
public class WrongQuestionServiceImpl implements WrongQuestionService {

    private final WrongQuestionMapper mapper;
    private final AnkiService ankiService;

    @Override
    public PageVO<WrongQuestion> page(Long userId, int page, int size, String subject) {
        return PageVO.from(mapper.selectPage(new Page<>(page, size), new LambdaQueryWrapper<WrongQuestion>()
                .eq(WrongQuestion::getUserId, userId)
                .eq(subject != null && !subject.isBlank(), WrongQuestion::getSubject, subject)
                .orderByDesc(WrongQuestion::getCreateTime)));
    }

    @Override
    public WrongQuestion get(Long userId, Long id) {
        WrongQuestion wq = mapper.selectById(id);
        if (wq == null || !wq.getUserId().equals(userId)) {
            throw new BizException("错题不存在或无权限");
        }
        return wq;
    }

    @Override
    public Long add(Long userId, WrongQuestion wq) {
        wq.setId(null);
        wq.setUserId(userId);
        wq.setRepetition(0);
        wq.setIntervalDays(0);
        wq.setEase(Sm2.DEFAULT_EASE);
        mapper.insert(wq);
        ankiService.scheduleNext(wq, userId, LocalDate.now());
        return wq.getId();
    }

    @Override
    public void review(Long userId, Long id, int quality) {
        WrongQuestion wq = get(userId, id);
        wq.applyReview(quality);
        mapper.updateById(wq);
        ankiService.scheduleNext(wq, userId, LocalDate.now());
    }

    @Override
    public void delete(Long userId, Long id) {
        WrongQuestion wq = get(userId, id);
        mapper.deleteById(wq.getId());
    }

    @Override
    public Map<String, Object> forgetCurve() {
        Long userId = UserContext.uid();
        return Map.of(
                "userId", userId,
                "todayDue", ankiService.todayDueCount(userId),
                "curve", java.util.List.of());
    }
}