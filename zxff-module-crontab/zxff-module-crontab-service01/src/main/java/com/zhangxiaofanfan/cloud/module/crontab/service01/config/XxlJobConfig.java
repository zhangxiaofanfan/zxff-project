package com.zhangxiaofanfan.cloud.module.crontab.service01.config;

import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties(XxlJobProperties.class)
public class XxlJobConfig {

    @Bean
    public XxlJobExecutor xxlJobExecutor(XxlJobProperties properties) {
        log.info("[xxlJobExecutor][初始化 XXL-Job 执行器的配置]");
        XxlJobProperties.AdminProperties admin = properties.getAdmin();
        XxlJobProperties.ExecutorProperties executor = properties.getExecutor();

        // 初始化执行器
        XxlJobExecutor xxlJobExecutor = new XxlJobSpringExecutor();
        xxlJobExecutor.setIp(executor.getIp());
        xxlJobExecutor.setPort(executor.getPort());
        xxlJobExecutor.setAppname(executor.getAppName());
        xxlJobExecutor.setLogPath(executor.getLogPath());
        xxlJobExecutor.setLogRetentionDays(executor.getLogRetentionDays());
        xxlJobExecutor.setAdminAddresses(admin.getAddresses());
        xxlJobExecutor.setAccessToken(properties.getAccessToken());
        return xxlJobExecutor;
    }
}
