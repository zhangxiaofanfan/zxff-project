package com.zhangxiaofanfan.cloud.framework.operatelog.core.service;

import com.mzt.logapi.beans.LogRecord;
import com.mzt.logapi.service.ILogRecordService;
import com.zhangxiaofanfan.cloud.framework.security.core.util.SecurityFrameworkUtils;
import com.zhangxiaofanfan.cloud.module.common.pojo.user.LoginUser;
import com.zhangxiaofanfan.cloud.module.common.util.monitor.TracerUtils;
import com.zhangxiaofanfan.cloud.module.common.util.servlet.ServletUtils;
import com.zhangxiaofanfan.cloud.module.sso.api.logger.OperateLogApi;
import com.zhangxiaofanfan.cloud.module.sso.api.logger.dto.OperateLogCreateReqDTO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * TODO
 *
 * @author zhangxiaofanfan
 * @date 2024-07-03 15:11:35
 */
@Slf4j
public class LogRecordServiceImpl implements ILogRecordService {

    @Resource
    private OperateLogApi operateLogApi;

    @Override
    public void record(LogRecord logRecord) {
        // 1. 补全通用字段
        OperateLogCreateReqDTO reqDTO = new OperateLogCreateReqDTO();
        reqDTO.setTraceId(TracerUtils.getTraceId());
        // 补充用户信息
        fillUserFields(reqDTO);
        // 补全模块信息
        fillModuleFields(reqDTO, logRecord);
        // 补全请求信息
        fillRequestFields(reqDTO);

        // 2. 异步记录日志
        operateLogApi.createOperateLog(reqDTO);
    }

    private static void fillUserFields(OperateLogCreateReqDTO reqDTO) {
        // 使用 SecurityFrameworkUtils。因为要考虑，rpc、mq、job，它其实不是 web；
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            return;
        }
        reqDTO.setUserId(loginUser.getId());
        reqDTO.setUserType(loginUser.getUserType());
    }

    public static void fillModuleFields(OperateLogCreateReqDTO reqDTO, LogRecord logRecord) {
        reqDTO.setType(logRecord.getType()); // 大模块类型，例如：CRM 客户
        reqDTO.setSubType(logRecord.getSubType());// 操作名称，例如：转移客户
        reqDTO.setBizId(Long.parseLong(logRecord.getBizNo())); // 业务编号，例如：客户编号
        reqDTO.setAction(logRecord.getAction());// 操作内容，例如：修改编号为 1 的用户信息，将性别从男改成女，将姓名从芋道改成源码。
        reqDTO.setExtra(logRecord.getExtra()); // 拓展字段，有些复杂的业务，需要记录一些字段 ( JSON 格式 )，例如说，记录订单编号，{ orderId: "1"}
    }

    private static void fillRequestFields(OperateLogCreateReqDTO reqDTO) {
        // 获得 Request 对象
        HttpServletRequest request = ServletUtils.getRequest();
        if (request == null) {
            return;
        }
        // 补全请求信息
        reqDTO.setRequestMethod(request.getMethod());
        reqDTO.setRequestUrl(request.getRequestURI());
        reqDTO.setUserIp(ServletUtils.getClientIP(request));
        reqDTO.setUserAgent(ServletUtils.getUserAgent(request));
    }

    @Override
    public List<LogRecord> queryLog(String bizNo, String type) {
        throw new UnsupportedOperationException("使用 OperateLogApi 进行操作日志的查询");
    }

    @Override
    public List<LogRecord> queryLogByBizNo(String bizNo, String type, String subType) {
        throw new UnsupportedOperationException("使用 OperateLogApi 进行操作日志的查询");
    }
}
