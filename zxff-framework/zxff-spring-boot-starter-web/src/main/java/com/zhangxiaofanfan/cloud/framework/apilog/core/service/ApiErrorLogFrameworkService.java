package com.zhangxiaofanfan.cloud.framework.apilog.core.service;

import com.zhangxiaofanfan.cloud.module.common.dto.logger.request.ApiErrorLogCreateReqDTO;

/**
 * API 错误日志 Framework Service 接口
 *
 * @author 芋道源码
 */
public interface ApiErrorLogFrameworkService {

    /**
     * 创建 API 错误日志
     *
     * @param reqDTO API 错误日志
     */
    void createApiErrorLog(ApiErrorLogCreateReqDTO reqDTO);

}