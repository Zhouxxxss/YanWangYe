package com.ywy.auth.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ywy.common.domain.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 账号表（auth 模块主权）。承载登录凭据与基础身份；
 * 用户档案（昵称/头像/简介等）由用户模块的 user_profile 维护。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("auth_account")
public class User extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    /** 状态：NORMAL / DISABLED */
    private String status;

    /** 角色（占位，二期接入 RBAC 权限点后按 user_role 关联读取） */
    private String role;

    /** 头像样式 token，供前端生成强交互头像动效 */
    @TableField("style_token")
    private String styleToken;

    /** 二期预留：目标院校 ID（择校库接入后启用） */
    @TableField("target_school_id")
    private Long targetSchoolId;
}