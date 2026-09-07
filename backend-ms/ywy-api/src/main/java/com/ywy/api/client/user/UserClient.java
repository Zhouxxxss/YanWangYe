package com.ywy.api.client.user;

import com.ywy.api.config.DefaultFeignConfig;
import com.ywy.api.dto.user.UserInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户服务 Feign 客户端。降级逻辑见 {@link UserClientFallbackFactory}。
 */
@FeignClient(value = "ywy-user", contextId = "userClient",
        configuration = DefaultFeignConfig.class,
        fallbackFactory = UserClientFallbackFactory.class)
public interface UserClient {

    @GetMapping("/users/{id}")
    UserInfoDTO getUserById(@PathVariable("id") Long id);
}