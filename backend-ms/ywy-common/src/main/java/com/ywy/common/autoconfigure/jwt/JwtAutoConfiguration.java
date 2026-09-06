package com.ywy.common.autoconfigure.jwt;

import com.ywy.common.utils.JwtUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JWT 自动装配：配置了 ywy.jwt.secret 的模块（网关/auth）启用 JwtUtils Bean。
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(JwtProperties.class)
@ConditionalOnProperty(prefix = "ywy.jwt", name = "secret")
public class JwtAutoConfiguration {

    @Bean
    public JwtUtils jwtUtils(JwtProperties props) {
        return new JwtUtils(props);
    }
}