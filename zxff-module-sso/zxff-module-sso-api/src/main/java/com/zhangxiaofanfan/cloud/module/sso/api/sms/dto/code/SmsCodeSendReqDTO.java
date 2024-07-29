package com.zhangxiaofanfan.cloud.module.sso.api.sms.dto.code;

import com.zhangxiaofanfan.cloud.module.common.validation.InEnum;
import com.zhangxiaofanfan.cloud.module.common.validation.Mobile;
import com.zhangxiaofanfan.cloud.module.sso.enums.sms.SmsSceneEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "RPC 服务 - 短信验证码的发送 Request DTO")
@Data
public class SmsCodeSendReqDTO {

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "15601691300")
    @Mobile
    @NotEmpty(message = "手机号不能为空")
    private String mobile;

    @Schema(description = "发送场景", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "发送场景不能为空")
    @InEnum(SmsSceneEnum.class)

    private Integer scene;
    @Schema(description = "发送 IP", requiredMode = Schema.RequiredMode.REQUIRED, example = "10.20.30.40")
    @NotEmpty(message = "发送 IP 不能为空")
    private String createIp;

}
