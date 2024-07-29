package com.zhangxiaofanfan.cloud.module.sso.enums.sms;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * TODO
 *
 * @author zhangxiaofanfan
 * @date 2024-07-04 11:16:57
 */
@Getter
@AllArgsConstructor
public enum SmsSendStatusEnum {

    INIT(0), // 初始化
    SUCCESS(10), // 发送成功
    FAILURE(20), // 发送失败
    IGNORE(30), // 忽略，即不发送
    ;

    private final int status;

}