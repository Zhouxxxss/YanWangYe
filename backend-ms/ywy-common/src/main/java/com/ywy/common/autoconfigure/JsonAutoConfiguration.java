package com.ywy.common.autoconfigure;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;
import java.util.TimeZone;

/**
 * Jackson 全局配置：统一日期格式与时区。
 */
@Configuration(proxyBeanMethods = false)
public class JsonAutoConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder
                .dateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
                .timeZone(TimeZone.getTimeZone(ZoneId.of("Asia/Shanghai")));
    }
}