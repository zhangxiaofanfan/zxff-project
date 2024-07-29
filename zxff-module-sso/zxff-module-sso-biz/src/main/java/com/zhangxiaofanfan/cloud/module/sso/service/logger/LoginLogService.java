package com.zhangxiaofanfan.cloud.module.sso.service.logger;

import com.zhangxiaofanfan.cloud.module.common.pojo.PageResult;
import com.zhangxiaofanfan.cloud.module.sso.api.logger.dto.LoginLogCreateReqDTO;
import com.zhangxiaofanfan.cloud.module.sso.controller.logger.vo.loginlog.LoginLogPageReqVO;
import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.logger.LoginLogDO;
import jakarta.validation.Valid;

/**
 * 登录日志 Service 接口
 */
public interface LoginLogService {

    /**
     * 获得登录日志分页
     *
     * @param pageReqVO 分页条件
     * @return 登录日志分页
     */
    PageResult<LoginLogDO> getLoginLogPage(LoginLogPageReqVO pageReqVO);

    /**
     * 创建登录日志
     *
     * @param reqDTO 日志信息
     */
    void createLoginLog(@Valid LoginLogCreateReqDTO reqDTO);

}
