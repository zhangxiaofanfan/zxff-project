package com.zhangxiaofanfan.cloud.framework.banner.core;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @author zhangxiaofanfan
 * @date 2024-07-03 18:07:11
 */
@Slf4j
public class BannerApplicationRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        ThreadUtil.execute(() -> {
            ThreadUtil.sleep(1, TimeUnit.SECONDS); // 延迟 1 秒，保证输出到结尾
            log.info("\n----------------------------------------------------------\n\t" +
                            "项目启动成功！\n\t" +
                            "接口文档: \t{} \n\t" +
                            "开发文档: \t{} \n\t" +
                            "视频教程: \t{} \n" +
                            "----------------------------------------------------------",
                    "https://cloud.iocoder.cn/api-doc/",
                    "https://cloud.iocoder.cn",
                    "https://t.zsxq.com/02Yf6M7Qn");
        });
    }

}