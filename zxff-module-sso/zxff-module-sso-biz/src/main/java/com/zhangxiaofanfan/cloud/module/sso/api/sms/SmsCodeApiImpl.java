package com.zhangxiaofanfan.cloud.module.sso.api.sms;

import com.zhangxiaofanfan.cloud.module.common.pojo.CommonResult;
import com.zhangxiaofanfan.cloud.module.sso.api.sms.dto.code.SmsCodeSendReqDTO;
import com.zhangxiaofanfan.cloud.module.sso.api.sms.dto.code.SmsCodeUseReqDTO;
import com.zhangxiaofanfan.cloud.module.sso.api.sms.dto.code.SmsCodeValidateReqDTO;
import com.zhangxiaofanfan.cloud.module.sso.service.sms.SmsCodeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import static com.zhangxiaofanfan.cloud.module.common.pojo.CommonResult.success;

@Service
public class SmsCodeApiImpl implements SmsCodeApi {
    @Resource
    private SmsCodeService smsCodeService;

    @Override
    public CommonResult<Boolean> sendSmsCode(SmsCodeSendReqDTO reqDTO) {
        smsCodeService.sendSmsCode(reqDTO);
        return success(true);
    }

    @Override
    public CommonResult<Boolean> useSmsCode(SmsCodeUseReqDTO reqDTO) {
        smsCodeService.useSmsCode(reqDTO);
        return success(true);
    }

    @Override
    public CommonResult<Boolean> validateSmsCode(SmsCodeValidateReqDTO reqDTO) {
        smsCodeService.validateSmsCode(reqDTO);
        return success(true);
    }
}
