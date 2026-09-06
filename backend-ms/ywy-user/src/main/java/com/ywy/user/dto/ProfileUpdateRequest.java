package com.ywy.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 档案信息修改请求。仅提交需要变更的字段。
 */
@Data
public class ProfileUpdateRequest {

    @Size(max = 30, message = "昵称过长")
    private String nickname;

    private String avatar;

    private String email;

    @Size(max = 200, message = "简介过长")
    private String bio;

    private String styleToken;
}