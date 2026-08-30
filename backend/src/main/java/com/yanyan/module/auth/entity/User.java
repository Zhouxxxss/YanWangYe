package com.yanyan.module.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户表（RBAC 五表之一）。二期预留 targetSchoolId 字段（择校目标绑定）。
 */
@Data
@TableName("sys_user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String avatar;

    private String email;

    /** 头像样式 token，供前端动态生成强交互头像动效 */
    @TableField("style_token")
    private String styleToken;

    /** 二期预留：目标院校 ID（择校库接入后启用） */
    @TableField("target_school_id")
    private Long targetSchoolId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}