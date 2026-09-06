package com.ywy.api.client.user;

import com.ywy.api.dto.user.UserInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * 用户服务降级：下游不可用时返回占位数据，避免级联失败拖垮调用方。
 */
@Slf4j
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {

    @Override
    public UserClient create(Throwable cause) {
        log.warn("调用 ywy-user 失败，返回降级数据：{}", cause.getMessage());
        return id -> {
            UserInfoDTO dto = new UserInfoDTO();
            dto.setId(id);
            dto.setUsername("未知用户");
            return dto;
        };
    }
}