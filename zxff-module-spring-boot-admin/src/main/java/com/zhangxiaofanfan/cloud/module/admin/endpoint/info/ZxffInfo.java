package com.zhangxiaofanfan.cloud.module.admin.endpoint.info;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @date 2024-06-04 08:54:45
 * @author zhangxiaofanfan
 */
@Component
public class ZxffInfo implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        builder
                .withDetail("name", "zxff")
                .withDetail("age", 20)
                .withDetail("sex", "男")
                .withDetail("version", "V1.0.0");
    }
}
