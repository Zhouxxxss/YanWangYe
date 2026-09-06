package com.ywy.user.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ywy.common.domain.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 好友关系（user 模块）。双向一条记录，维护申请/已通过等状态。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_friend")
public class FriendShip extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 发起方用户 id */
    private Long userId;

    /** 好友用户 id */
    private Long friendId;

    /** 状态：PENDING / ACCEPTED / REJECTED / BLOCKED */
    private String status;
}