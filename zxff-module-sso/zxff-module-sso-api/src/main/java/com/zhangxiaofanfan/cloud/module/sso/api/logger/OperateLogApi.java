package com.zhangxiaofanfan.cloud.module.sso.api.logger;

import com.zhangxiaofanfan.cloud.module.common.constant.http.SsoApiConstants;
import com.zhangxiaofanfan.cloud.module.common.pojo.CommonResult;
import com.zhangxiaofanfan.cloud.module.common.pojo.PageResult;
import com.zhangxiaofanfan.cloud.module.sso.api.logger.dto.OperateLogCreateReqDTO;
import com.zhangxiaofanfan.cloud.module.sso.api.logger.dto.OperateLogPageReqDTO;
import com.zhangxiaofanfan.cloud.module.sso.api.logger.dto.OperateLogRespDTO;
import feign.QueryMap;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = SsoApiConstants.NAME) // TODO 芋艿：fallbackFactory =
@Tag(name = "RPC 服务 - 操作日志")
public interface OperateLogApi {

    String PREFIX = SsoApiConstants.PREFIX + "/operate-log";

    @PostMapping(PREFIX + "/create")
    @Operation(summary = "创建操作日志")
    CommonResult<Boolean> createOperateLog(@Valid @RequestBody OperateLogCreateReqDTO createReqDTO);

    @PostMapping(PREFIX + "/page")
    @Operation(summary = "获取指定模块的指定数据的操作日志分页")
    CommonResult<PageResult<OperateLogRespDTO>> getOperateLogPage(@QueryMap OperateLogPageReqDTO pageReqVO);

}
