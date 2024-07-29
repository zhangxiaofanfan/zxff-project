package com.zhangxiaofanfan.cloud.module.sso.api.logger;

import com.zhangxiaofanfan.cloud.module.common.constant.http.SsoApiConstants;
import com.zhangxiaofanfan.cloud.module.common.dto.logger.request.ApiErrorLogCreateReqDTO;
import com.zhangxiaofanfan.cloud.module.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 单点登录异常日志记录方法
 *
 * @author zhangxiaofanfan
 * @date 2024-07-03 16:29:31
 */
@FeignClient(name = SsoApiConstants.NAME) // TODO 芋艿：fallbackFactory =
@Tag(name = "RPC 服务 - API 异常日志")
public interface ApiErrorLogApi {

    String PREFIX = SsoApiConstants.PREFIX + "/api-error-log";

    @PostMapping(PREFIX + "/create")
    @Operation(summary = "创建 API 异常日志")
    CommonResult<Boolean> createApiErrorLog(@Valid @RequestBody ApiErrorLogCreateReqDTO createDTO);

}