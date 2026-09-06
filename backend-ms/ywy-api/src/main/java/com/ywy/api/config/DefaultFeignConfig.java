package com.ywy.api.config;

import com.ywy.common.utils.UserContext;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

/**
 * 全局 Feign 配置：服务间调用时透传当前用户 Header，保证下游拿到调用者身份。
 * 引用此配置需配合 @EnableFeignClients(defaultConfiguration = DefaultFeignConfig.class)。
 */
public class DefaultFeignConfig {

    @Bean
    public RequestInterceptor userInfoFeignInterceptor() {
        return template -> {
            UserContext ctx = UserContext.get();
            if (ctx != null && ctx.getUserId() != null) {
                template.header(UserContext.HEADER_USER_ID, String.valueOf(ctx.getUserId()));
                template.header(UserContext.HEADER_USERNAME, ctx.getUsername() == null ? "" : ctx.getUsername());
                template.header(UserContext.HEADER_ROLE, ctx.getRole() == null ? "USER" : ctx.getRole());
            }
        };
    }
}