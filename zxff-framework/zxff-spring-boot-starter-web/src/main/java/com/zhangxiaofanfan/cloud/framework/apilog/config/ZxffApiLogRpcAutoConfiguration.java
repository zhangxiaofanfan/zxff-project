package com.zhangxiaofanfan.cloud.framework.apilog.config;

import com.zhangxiaofanfan.cloud.module.sso.api.logger.ApiAccessLogApi;
import com.zhangxiaofanfan.cloud.module.sso.api.logger.ApiErrorLogApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * TODO
 *
 * @author zhangxiaofanfan
 * @date 2024-07-03 16:57:13
 */
@AutoConfiguration
@EnableFeignClients(clients = {
        ApiAccessLogApi.class, // 主要是引入相关的 API 服务
        ApiErrorLogApi.class
})
public class ZxffApiLogRpcAutoConfiguration {
}
