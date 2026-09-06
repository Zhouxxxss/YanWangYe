package com.ywy.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ywy.common.exceptions.BizException;
import com.ywy.user.domain.po.FriendShip;
import com.ywy.user.mapper.FriendShipMapper;
import com.ywy.user.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 好友列表服务实现：申请/同意/列表/删除（二期聊天在此之上构建）。
 */
@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final FriendShipMapper friendMapper;

    private FriendShip findRelation(Long a, Long b) {
        return friendMapper.selectOne(new LambdaQueryWrapper<FriendShip>()
                .eq(FriendShip::getUserId, a).eq(FriendShip::getFriendId, b));
    }

    @Override
    @Transactional
    public void apply(Long userId, Long friendId) {
        if (userId.equals(friendId)) throw new BizException("不能添加自己为好友");
        FriendShip exist = findRelation(userId, friendId);
        if (exist != null) throw new BizException("已存在好友关系");
        FriendShip fs = new FriendShip();
        fs.setUserId(userId);
        fs.setFriendId(friendId);
        fs.setStatus("PENDING");
        friendMapper.insert(fs);
    }

    @Override
    @Transactional
    public void accept(Long userId, Long friendId) {
        FriendShip pending = findRelation(friendId, userId);
        if (pending == null || !"PENDING".equals(pending.getStatus())) {
            throw new BizException("没有待处理的好友申请");
        }
        pending.setStatus("ACCEPTED");
        friendMapper.updateById(pending);
        FriendShip mine = findRelation(userId, friendId);
        if (mine == null) {
            mine = new FriendShip();
            mine.setUserId(userId);
            mine.setFriendId(friendId);
            mine.setStatus("ACCEPTED");
            friendMapper.insert(mine);
        } else {
            mine.setStatus("ACCEPTED");
            friendMapper.updateById(mine);
        }
    }

    @Override
    public List<Long> acceptedFriendIds(Long userId) {
        return friendMapper.selectList(new LambdaQueryWrapper<FriendShip>()
                        .eq(FriendShip::getUserId, userId)
                        .eq(FriendShip::getStatus, "ACCEPTED"))
                .stream().map(FriendShip::getFriendId).toList();
    }

    @Override
    public List<FriendShip> list(Long userId) {
        return friendMapper.selectList(new LambdaQueryWrapper<FriendShip>()
                .eq(FriendShip::getUserId, userId)
                .eq(FriendShip::getStatus, "ACCEPTED")
                .orderByDesc(FriendShip::getUpdateTime));
    }

    @Override
    @Transactional
    public void remove(Long userId, Long friendId) {
        for (Long self : List.of(userId, friendId)) {
            for (Long other : List.of(userId, friendId)) {
                if (self.equals(other)) continue;
                friendMapper.delete(new LambdaQueryWrapper<FriendShip>()
                        .eq(FriendShip::getUserId, self).eq(FriendShip::getFriendId, other));
            }
        }
    }
}