package com.zhangxiaofanfan.cloud.framework.security.config;

import com.zhangxiaofanfan.cloud.framework.security.core.rpc.LoginUserRequestInterceptor;
import com.zhangxiaofanfan.cloud.module.sso.api.oauth2.OAuth2TokenApi;
import com.zhangxiaofanfan.cloud.module.sso.api.permission.PermissionApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

/**
 * Security 使用到 Feign 的配置项
 *
 * @author 芋道源码
 */
@AutoConfiguration
@EnableFeignClients(
        clients = {
                OAuth2TokenApi.class, // 主要是引入相关的 API 服务
                PermissionApi.class
        })
public class ZxffSecurityRpcAutoConfiguration {

    @Bean
    public LoginUserRequestInterceptor loginUserRequestInterceptor() {
        return new LoginUserRequestInterceptor();
    }

}
