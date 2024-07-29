package com.zhangxiaofanfan.cloud.module.crontab.service01.handler;

import com.xxl.job.core.handler.IJobHandler;
import com.zhangxiaofanfan.cloud.module.crontab.service01.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 这是一个 GLUE 模式的测试 demo
 *
 * @author zhangxiaofanfan
 * @date 2023-10-10 18:52:42
 */
public class DemoGlueJobHandler extends IJobHandler {

    @Autowired
    private HelloService helloService;

    @Override
    public void execute() {
        helloService.methodA();
    }
}
