package com.zhangxiaofanfan.cloud.framework.apilog.core.service.impl;

import com.zhangxiaofanfan.cloud.framework.apilog.core.service.ApiAccessLogFrameworkService;
import com.zhangxiaofanfan.cloud.module.sso.api.logger.ApiAccessLogApi;
import com.zhangxiaofanfan.cloud.module.sso.api.logger.dto.ApiAccessLogCreateReqDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;

/**
 * TODO
 *
 * @author zhangxiaofanfan
 * @date 2024-07-03 17:09:50
 */
@RequiredArgsConstructor
public class ApiAccessLogFrameworkServiceImpl implements ApiAccessLogFrameworkService {

    private final ApiAccessLogApi apiAccessLogApi;

    @Override
    @Async
    public void createApiAccessLog(ApiAccessLogCreateReqDTO reqDTO) {
        apiAccessLogApi.createApiAccessLog(reqDTO);
    }

}