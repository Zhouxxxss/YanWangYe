package com.ywy.user.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ywy.common.domain.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户档案（user 模块主权）。凭据（用户名/密码/状态）在 auth_account，此处仅存展示信息。
 * 通过 userId 与 auth 模块关联。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_profile")
public class UserProfile extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 对应 auth_account.id */
    private Long userId;

    private String nickname;

    private String avatar;

    private String email;

    /** 一句话简介 */
    private String bio;

    /** 头像样式 token，供前端生成强交互头像动效 */
    private String styleToken;

    /** 目标院校（二期择校库接入后启用） */
    private Long targetSchoolId;
}