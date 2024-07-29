package com.zhangxiaofanfan.cloud.module.admin.endpoint.health;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @date 2024-06-04 08:54:45
 * @author zhangxiaofanfan
 */
@Component
public class ZxffHealthIndicator extends AbstractHealthIndicator {

    // 自定义检查逻辑
    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        builder.up();
    }
}
