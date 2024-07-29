package com.zhangxiaofanfan.cloud.framework.operatelog.config;

import com.mzt.logapi.service.ILogRecordService;
import com.mzt.logapi.starter.annotation.EnableLogRecord;
import com.zhangxiaofanfan.cloud.framework.operatelog.core.service.LogRecordServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * TODO
 *
 * @author zhangxiaofanfan
 * @date 2024-07-03 15:10:07
 */
@EnableLogRecord(tenant = "") // 貌似用不上 tenant 这玩意给个空好啦
@AutoConfiguration
@Slf4j
public class ZxffOperateLogConfiguration {

    @Bean
    @Primary
    public ILogRecordService iLogRecordServiceImpl() {
        return new LogRecordServiceImpl();
    }

}