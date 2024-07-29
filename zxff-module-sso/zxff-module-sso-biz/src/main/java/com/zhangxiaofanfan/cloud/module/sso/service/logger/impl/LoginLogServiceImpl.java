package com.zhangxiaofanfan.cloud.module.sso.service.logger.impl;

import com.zhangxiaofanfan.cloud.module.common.pojo.PageResult;
import com.zhangxiaofanfan.cloud.module.common.util.object.BeanUtils;
import com.zhangxiaofanfan.cloud.module.sso.api.logger.dto.LoginLogCreateReqDTO;
import com.zhangxiaofanfan.cloud.module.sso.controller.logger.vo.loginlog.LoginLogPageReqVO;
import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.logger.LoginLogDO;
import com.zhangxiaofanfan.cloud.module.sso.dao.mysql.logger.LoginLogMapper;
import com.zhangxiaofanfan.cloud.module.sso.service.logger.LoginLogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class LoginLogServiceImpl implements LoginLogService {
    @Resource
    private LoginLogMapper loginLogMapper;

    @Override
    public PageResult<LoginLogDO> getLoginLogPage(LoginLogPageReqVO pageReqVO) {
        return loginLogMapper.selectPage(pageReqVO);
    }

    @Override
    public void createLoginLog(LoginLogCreateReqDTO reqDTO) {
        LoginLogDO loginLog = BeanUtils.toBean(reqDTO, LoginLogDO.class);
        loginLogMapper.insert(loginLog);
    }

}
