package com.zhangxiaofanfan.cloud.framework.apilog.core.service;

import com.zhangxiaofanfan.cloud.module.sso.api.logger.dto.ApiAccessLogCreateReqDTO;

/**
 * TODO
 *
 * @author zhangxiaofanfan
 * @date 2024-07-03 17:08:59
 */
public interface ApiAccessLogFrameworkService {

    /**
     * 创建 API 访问日志
     *
     * @param reqDTO API 访问日志
     */
    void createApiAccessLog(ApiAccessLogCreateReqDTO reqDTO);

}