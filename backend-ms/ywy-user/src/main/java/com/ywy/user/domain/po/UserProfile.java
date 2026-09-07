package com.ywy.user.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ywy.common.domain.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 单表 user 的档案侧映射（user 模块主权列：nickname/avatar/email/bio 等）。
 * 主键 id 即全局用户 id，与 auth 模块共用同一行，无需再引入独立 user_id。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class UserProfile extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String nickname;

    private String avatar;

    private String email;

    /** 一句话简介 */
    private String bio;

    /** 头像样式 token，供前端生成强交互头像动效 */
    @TableField("style_token")
    private String styleToken;

    /** 目标院校（二期择校库接入后启用） */
    @TableField("target_school_id")
    private Long targetSchoolId;
}