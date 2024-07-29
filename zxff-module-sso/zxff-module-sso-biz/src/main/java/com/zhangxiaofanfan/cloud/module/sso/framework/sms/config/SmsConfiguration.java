package com.zhangxiaofanfan.cloud.module.sso.framework.sms.config;

import com.zhangxiaofanfan.cloud.module.sso.framework.sms.core.factory.SmsClientFactory;
import com.zhangxiaofanfan.cloud.module.sso.framework.sms.core.factory.impl.SmsClientFactoryImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 短信配置类，包括短信客户端、短信验证码两部分
 *
 * @author zhangxiaofanfan
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SmsCodeProperties.class)
public class SmsConfiguration {

    @Bean
    public SmsClientFactory smsClientFactory() {
        return new SmsClientFactoryImpl();
    }

}
