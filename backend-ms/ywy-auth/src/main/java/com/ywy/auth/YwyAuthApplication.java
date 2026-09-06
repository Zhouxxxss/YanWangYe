package com.ywy.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 认证授权服务。仅负责账号凭据与令牌签发；鉴权过滤收敛在网关。
 */
@EnableConfigurationProperties
@SpringBootApplication
public class YwyAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(YwyAuthApplication.class, args);
    }
}