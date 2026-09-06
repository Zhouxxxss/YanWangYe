package com.ywy.study.recite;

import com.ywy.study.common.PageVO;

/**
 * 背诵打卡服务接口。实现见 {@code recite.impl.ReciteServiceImpl}。
 */
public interface ReciteService {

    PageVO<ReciteCard> page(Long userId, int page, int size, String subject);

    Long add(Long userId, ReciteCard card);

    void recite(Long userId, Long id, int quality);

    long todayDue(Long userId);
}