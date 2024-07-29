package com.zhangxiaofanfan.cloud.module.sso.api.sms;

import com.zhangxiaofanfan.cloud.module.common.constant.http.SsoApiConstants;
import com.zhangxiaofanfan.cloud.module.common.pojo.CommonResult;
import com.zhangxiaofanfan.cloud.module.sso.api.sms.dto.code.SmsCodeSendReqDTO;
import com.zhangxiaofanfan.cloud.module.sso.api.sms.dto.code.SmsCodeUseReqDTO;
import com.zhangxiaofanfan.cloud.module.sso.api.sms.dto.code.SmsCodeValidateReqDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

@FeignClient(name = SsoApiConstants.NAME) // TODO 芋艿：fallbackFactory =
@Tag(name = "RPC 服务 - 短信验证码")
public interface SmsCodeApi {

    String PREFIX = SsoApiConstants.PREFIX + "/oauth2/sms/code";

    @PostMapping(PREFIX + "/send")
    @Operation(summary = "创建短信验证码，并进行发送")
    CommonResult<Boolean> sendSmsCode(@Valid @RequestBody SmsCodeSendReqDTO reqDTO);

    @PutMapping(PREFIX + "/use")
    @Operation(summary = "验证短信验证码，并进行使用")
    CommonResult<Boolean> useSmsCode(@Valid @RequestBody SmsCodeUseReqDTO reqDTO);

    @GetMapping(PREFIX + "/validate")
    @Operation(summary = "检查验证码是否有效")
    CommonResult<Boolean> validateSmsCode(@Valid @RequestBody SmsCodeValidateReqDTO reqDTO);

}
