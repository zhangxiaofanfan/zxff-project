package com.zhangxiaofanfan.cloud.module.sso.enums.sms;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * TODO
 *
 * @author zhangxiaofanfan
 * @date 2024-07-04 11:17:20
 */
@Getter
@AllArgsConstructor
public enum SmsReceiveStatusEnum {

    INIT(0), // 初始化
    SUCCESS(10), // 接收成功
    FAILURE(20), // 接收失败
    ;

    private final int status;

}
