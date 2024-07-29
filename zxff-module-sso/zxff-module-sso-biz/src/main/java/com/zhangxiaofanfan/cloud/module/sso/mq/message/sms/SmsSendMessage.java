package com.zhangxiaofanfan.cloud.module.sso.mq.message.sms;

import com.zhangxiaofanfan.cloud.module.common.core.KeyValue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 短信发送消息
 *
 * @author zhangxiaofanfan
 * @date 2024-07-03 11:27:49
 */
@Data
public class SmsSendMessage {

    /**
     * 短信日志编号
     */
    @NotNull(message = "短信日志编号不能为空")
    private Long logId;
    /**
     * 手机号
     */
    @NotNull(message = "手机号不能为空")
    private String mobile;
    /**
     * 短信渠道编号
     */
    @NotNull(message = "短信渠道编号不能为空")
    private Long channelId;
    /**
     * 短信 API 的模板编号
     */
    @NotNull(message = "短信 API 的模板编号不能为空")
    private String apiTemplateId;
    /**
     * 短信模板参数
     */
    private List<KeyValue<String, Object>> templateParams;

}
