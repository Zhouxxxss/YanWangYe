package com.ywy.common.autoconfigure.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 自动装配：分页插件 + 审计字段填充。
 * 仅当 mybatis-plus 与分析相关类在 classpath 时生效（业务模块引入 starter 后启用）。
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ MybatisPlusInterceptor.class, PaginationInnerInterceptor.class })
public class MybatisAutoConfiguration {

    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new BaseMetaObjectHandler();
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }
}