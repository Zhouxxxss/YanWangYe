package com.ywy.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywy.auth.wx.MockWxMpClient;
import com.ywy.auth.wx.RealWxMpClient;
import com.ywy.auth.wx.WxMpClient;
import com.ywy.auth.wx.WxMpProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

/**
 * 认证配置：BCrypt 加密、公众号适配器（配置了 appId/secret 走真实接口，否则走 Mock 以便本地联调）。
 */
@Configuration
@EnableConfigurationProperties(WxMpProperties.class)
public class AuthConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public WxMpClient wxMpClient(WxMpProperties props, RestTemplate restTemplate, ObjectMapper objectMapper) {
        if (props.configured()) {
            return new RealWxMpClient(props, restTemplate, objectMapper);
        }
        return new MockWxMpClient(props);
    }
}