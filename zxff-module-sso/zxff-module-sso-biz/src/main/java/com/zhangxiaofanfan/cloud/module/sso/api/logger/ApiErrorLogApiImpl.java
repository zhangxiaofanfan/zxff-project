package com.zhangxiaofanfan.cloud.module.sso.api.logger;

import com.zhangxiaofanfan.cloud.module.common.dto.logger.request.ApiErrorLogCreateReqDTO;
import com.zhangxiaofanfan.cloud.module.common.pojo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import static com.zhangxiaofanfan.cloud.module.common.pojo.CommonResult.success;

/**
 * TODO
 *
 * @author zhangxiaofanfan
 * @date 2024-07-11 15:15:15
 */
@Slf4j
// @Validated
@RestController
public class ApiErrorLogApiImpl implements ApiErrorLogApi {
    @Override
    public CommonResult<Boolean> createApiErrorLog(ApiErrorLogCreateReqDTO createDTO) {
        log.info("create error log method running, dto is {}", createDTO);
        return success(true);
    }
}
