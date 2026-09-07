package com.ywy.auth.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ywy.common.domain.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 单表 user（auth 与 user 模块共用）。凭据列(auth 主权)：account/username/password/status/role；
 * 档案列(user 主权)：nickname/avatar/email/bio/style_token/target_school_id。
 * 认证链路仅依赖本表主键与 Redis 会话，不做每次请求查库。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class User extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 账号：公众号 openid，或账号密码注册时与 username 同值 */
    private String account;

    /** 登录名（可后置设置，公众号注册为空，用户可在「我的」页补充） */
    private String username;

    /** BCrypt 散列（公众号注册为空，认证走 account） */
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