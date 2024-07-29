package com.zhangxiaofanfan.cloud.module.common.util.spring;

import cn.hutool.extra.spring.SpringUtil;

import java.util.Objects;

/**
 * TODO
 *
 * @author zhangxiaofanfan
 * @date 2024-07-03 17:42:50
 */
public class SpringUtils extends SpringUtil {

    /**
     * 是否为生产环境
     *
     * @return 是否生产环境
     */
    public static boolean isProd() {
        String activeProfile = getActiveProfile();
        return Objects.equals("prod", activeProfile);
    }

}
