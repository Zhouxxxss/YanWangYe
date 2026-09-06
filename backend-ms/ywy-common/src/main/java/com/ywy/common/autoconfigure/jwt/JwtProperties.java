package com.ywy.common.autoconfigure.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT 配置。前缀 ywy.jwt，由网关（签发校验）与 auth（签发）共用。
 */
@Data
@ConfigurationProperties(prefix = "ywy.jwt")
public class JwtProperties {

    /** 签名密钥，生产用环境变量注入 */
    private String secret = "ywy-dev-secret-change-me-in-prod-0123456789012345";

    /** 访问令牌有效期（毫秒），默认 2 小时 */
    private Long expireMs = 7_200_000L;

    /** 刷新令牌有效期（毫秒），默认 7 天 */
    private Long refreshExpireMs = 604_800_000L;

    private String header = "Authorization";
    private String prefix = "Bearer ";
}