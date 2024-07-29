package com.zhangxiaofanfan.cloud.framework.apilog.core.service.impl;

import com.zhangxiaofanfan.cloud.framework.apilog.core.service.ApiErrorLogFrameworkService;
import com.zhangxiaofanfan.cloud.module.common.dto.logger.request.ApiErrorLogCreateReqDTO;
import com.zhangxiaofanfan.cloud.module.sso.api.logger.ApiErrorLogApi;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;

/**
 * TODO
 *
 * @author zhangxiaofanfan
 * @date 2024-07-03 16:25:32
 */
@RequiredArgsConstructor
public class ApiErrorLogFrameworkServiceImpl implements ApiErrorLogFrameworkService {

    private final ApiErrorLogApi apiErrorLogApi;

    @Override
    @Async
    public void createApiErrorLog(ApiErrorLogCreateReqDTO reqDTO) {
        apiErrorLogApi.createApiErrorLog(reqDTO);
    }

}
