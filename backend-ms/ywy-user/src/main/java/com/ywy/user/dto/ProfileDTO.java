package com.ywy.user.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 档案信息响应。
 */
@Data
public class ProfileDTO implements Serializable {

    private Long userId;
    private String nickname;
    private String avatar;
    private String email;
    private String bio;
    private String styleToken;
    private Long targetSchoolId;
    private LocalDateTime createTime;
}