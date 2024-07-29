package com.zhangxiaofanfan.cloud.module.admin;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TODO
 *
 * @author zhangxiaofanfan
 * @date 2024-04-30 13:56:07
 */
@EnableAdminServer
@SpringBootApplication
public class SpringBootAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootAdminApplication.class, args);
    }
}
