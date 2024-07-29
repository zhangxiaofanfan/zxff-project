package com.zhangxiaofanfan.cloud.module.crontab.service01.service.impl;

import com.zhangxiaofanfan.cloud.module.crontab.service01.service.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 服务方法实现, 主要是打印方法执行了, 并且打印当前时间
 *
 * @date 2024-05-14 10:26:37
 * @author zhangxiaofanfan
 */
@Slf4j
@Service
public class HelloServiceImpl implements HelloService {
    @Override
    public void methodA() {
        log.info("执行了 MethodA 方法..., 时间为: {}", new Date());
    }

    @Override
    public void methodB() {
        log.info("执行了 MethodB 方法..., 时间为: {}", new Date());
    }
}
