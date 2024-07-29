package com.zhangxiaofanfan.cloud.module.common.constant.http;

import com.zhangxiaofanfan.cloud.module.common.constant.ApiConstants;
import com.zhangxiaofanfan.cloud.module.common.constant.rpc.RpcConstants;

public class SsoApiConstants implements ApiConstants {
    /**
     * 服务名
     *
     * 注意，需要保证和 spring.application.name 保持一致
     */
    public static final String NAME = "zxff-sso-server";

    public static final String PREFIX = RpcConstants.RPC_API_PREFIX +  "/system";

}
