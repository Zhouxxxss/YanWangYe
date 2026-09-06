package com.ywy.user.service;

import com.ywy.user.domain.po.FriendShip;

import java.util.List;

/**
 * 好友列表服务接口。实现见 {@code service.impl.FriendServiceImpl}。
 */
public interface FriendService {

    void apply(Long userId, Long friendId);

    void accept(Long userId, Long friendId);

    List<Long> acceptedFriendIds(Long userId);

    List<FriendShip> list(Long userId);

    void remove(Long userId, Long friendId);
}