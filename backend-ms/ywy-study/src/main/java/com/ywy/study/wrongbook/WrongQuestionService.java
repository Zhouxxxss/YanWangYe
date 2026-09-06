package com.ywy.study.wrongbook;

import com.ywy.study.common.PageVO;

import java.util.Map;

/**
 * 错题服务接口。实现见 {@code wrongbook.impl.WrongQuestionServiceImpl}。
 */
public interface WrongQuestionService {

    PageVO<WrongQuestion> page(Long userId, int page, int size, String subject);

    WrongQuestion get(Long userId, Long id);

    Long add(Long userId, WrongQuestion wq);

    void review(Long userId, Long id, int quality);

    void delete(Long userId, Long id);

    /** 遗忘曲线查看（错题复习计划图）。 */
    Map<String, Object> forgetCurve();
}