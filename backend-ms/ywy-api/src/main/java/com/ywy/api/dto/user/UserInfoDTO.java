package com.ywy.api.dto.user;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户基础信息 DTO（跨服务传递）。
 */
@Data
public class UserInfoDTO implements Serializable {

    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private LocalDateTime createTime;
}