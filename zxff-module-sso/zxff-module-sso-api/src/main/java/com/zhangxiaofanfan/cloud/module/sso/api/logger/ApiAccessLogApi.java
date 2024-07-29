package com.zhangxiaofanfan.cloud.module.sso.api.logger;

import com.zhangxiaofanfan.cloud.module.common.constant.http.SsoApiConstants;
import com.zhangxiaofanfan.cloud.module.common.pojo.CommonResult;
import com.zhangxiaofanfan.cloud.module.sso.api.logger.dto.ApiAccessLogCreateReqDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * TODO
 *
 * @author zhangxiaofanfan
 * @date 2024-07-03 17:02:19
 */
@FeignClient(name = SsoApiConstants.NAME) // TODO 芋艿：fallbackFactory =
@Tag(name = "RPC 服务 - API 访问日志")
public interface ApiAccessLogApi {

    String PREFIX = SsoApiConstants.PREFIX + "/api-access-log";

    @PostMapping(PREFIX + "/create")
    @Operation(summary = "创建 API 访问日志")
    CommonResult<Boolean> createApiAccessLog(@Valid @RequestBody ApiAccessLogCreateReqDTO createDTO);

}
