package com.zhangxiaofanfan.cloud.framework.operatelog.config;

import com.zhangxiaofanfan.cloud.module.sso.api.logger.OperateLogApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * TODO
 *
 * @author zhangxiaofanfan
 * @date 2024-07-03 15:10:17
 */
@AutoConfiguration
@EnableFeignClients(clients = {OperateLogApi.class}) // 主要是引入相关的 API 服务
public class ZxffOperateLogRpcAutoConfiguration {
}
