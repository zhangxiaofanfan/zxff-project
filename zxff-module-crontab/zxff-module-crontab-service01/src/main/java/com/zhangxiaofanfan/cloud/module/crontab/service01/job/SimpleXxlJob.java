package com.zhangxiaofanfan.cloud.module.crontab.service01.job;

import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 向容器中注入一个任务
 *
 * @author zhangxiaofanfan
 * @date 2023-10-09 19:56:56
 */
@Slf4j
@Component
public class SimpleXxlJob {

    @XxlJob("zhangxiaofanfanJobHandler")
    public void jobHandler() {
        log.info("定时任务被执行, 时间为: {}", new Date());
    }

    @XxlJob("zxff-job")
    public void zxffJobHandler() {
        log.info("zxff定时任务被执行, 时间为: {}", new Date());
    }

    @XxlJob("yxmm-job")
    public void yxmmJobHandler() {
        log.info("yxmm定时任务被执行, 时间为: {}", new Date());
    }
}
