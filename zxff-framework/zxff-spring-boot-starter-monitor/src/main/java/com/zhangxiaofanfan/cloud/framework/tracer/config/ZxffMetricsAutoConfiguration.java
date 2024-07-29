package com.zhangxiaofanfan.cloud.framework.tracer.config;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * TODO
 *
 * @author zhangxiaofanfan
 * @date 2024-07-04 16:42:33
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass({MeterRegistryCustomizer.class})
@ConditionalOnProperty(prefix = "zxff.metrics", value = "enable", matchIfMissing = true) // 允许使用 yudao.metrics.enable=false 禁用 Metrics
public class ZxffMetricsAutoConfiguration {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags(
            @Value("${spring.application.name}") String applicationName
    ) {
        log.info("注入对象: {}", "测试");
        return registry -> registry.config().commonTags("application", applicationName);
    }
}
