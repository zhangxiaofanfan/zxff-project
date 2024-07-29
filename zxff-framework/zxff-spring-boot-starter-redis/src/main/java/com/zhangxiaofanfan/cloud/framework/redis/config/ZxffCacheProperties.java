package com.zhangxiaofanfan.cloud.framework.redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Cache 配置项
 *
 * @author zhangxiaofanfan
 * @date 2024-07-08 16:47:32
 */
@ConfigurationProperties("zxff.cache")
@Data
@Validated
public class ZxffCacheProperties {
    /**
     * {@link #redisScanBatchSize} 默认值
     */
    private static final Integer REDIS_SCAN_BATCH_SIZE_DEFAULT = 30;

    /**
     * redis scan 一次返回数量
     */
    private Integer redisScanBatchSize = REDIS_SCAN_BATCH_SIZE_DEFAULT;
}
