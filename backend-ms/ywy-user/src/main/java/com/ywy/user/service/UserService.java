package com.ywy.user.service;

import com.ywy.api.dto.user.UserInfoDTO;
import com.ywy.user.dto.ProfileDTO;
import com.ywy.user.dto.ProfileUpdateRequest;

/**
 * 用户档案服务接口：读取/修改当前用户档案，并暴露跨服务读取用户基础信息。实现见 {@code service.impl.UserServiceImpl}。
 */
public interface UserService {

    ProfileDTO getProfile(Long userId);

    ProfileDTO updateProfile(Long userId, ProfileUpdateRequest req);

    /** Feign 接口实现：跨服务读取用户基础信息。 */
    UserInfoDTO getUserById(Long id);
}