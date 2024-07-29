package com.zhangxiaofanfan.cloud.module.sso.framework.security.config;

import com.zhangxiaofanfan.cloud.framework.security.config.customize.AuthorizeRequestsCustomizer;
import com.zhangxiaofanfan.cloud.module.common.constant.http.SsoApiConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

/**
 * zxff-module-sso 服务 spring-boot-security 配置 允许访问 的白名单路径
 *
 * @author zhangxiaofanfan
 * @date 2024-07-06 11:34:16
 */
@Configuration(proxyBeanMethods = false, value = "systemSecurityConfiguration")
public class SecurityConfiguration {
    @Value("${management.endpoints.web.base-path}")
    private String basePath;

    @Bean("systemAuthorizeRequestsCustomizer")
    public AuthorizeRequestsCustomizer authorizeRequestsCustomizer() {
        return new AuthorizeRequestsCustomizer() {

            @Override
            public void customize(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
                // Swagger 接口文档
                registry.requestMatchers("/v3/api-docs/**").permitAll() // 元数据
                        .requestMatchers("/swagger-ui.html").permitAll() // Swagger UI
                        .requestMatchers("/swagger-ui/**").permitAll(); // Swagger UI
                // Druid 监控
                registry.requestMatchers("/druid/**").permitAll();
                // Spring Boot Actuator 的安全配置
                registry.requestMatchers(basePath).permitAll()
                        .requestMatchers(String.format("%s/**", basePath)).permitAll();
                // RPC 服务的安全配置
                registry.requestMatchers(SsoApiConstants.PREFIX + "/**").permitAll();
            }
        };
    }

}
