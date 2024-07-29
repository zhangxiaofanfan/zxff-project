package com.zhangxiaofanfan.cloud.framework.banner.config;

import com.zhangxiaofanfan.cloud.framework.banner.core.BannerApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * TODO
 *
 * @author zhangxiaofanfan
 * @date 2024-07-03 18:06:52
 */
@AutoConfiguration
public class ZxffBannerAutoConfiguration {

    @Bean
    public BannerApplicationRunner bannerApplicationRunner() {
        return new BannerApplicationRunner();
    }

}
