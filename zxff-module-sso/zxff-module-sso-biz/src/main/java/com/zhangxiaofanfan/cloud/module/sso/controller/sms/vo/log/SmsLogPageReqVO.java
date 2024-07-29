package com.zhangxiaofanfan.cloud.module.sso.controller.sms.vo.log;

import com.zhangxiaofanfan.cloud.module.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.zhangxiaofanfan.cloud.module.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * TODO
 *
 * @author zhangxiaofanfan
 * @date 2024-07-03 11:30:59
 */
@Schema(description = "管理后台 - 短信日志分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SmsLogPageReqVO extends PageParam {

    @Schema(description = "短信渠道编号", example = "10")
    private Long channelId;

    @Schema(description = "模板编号", example = "20")
    private Long templateId;

    @Schema(description = "手机号", example = "15601691300")
    private String mobile;

    @Schema(description = "发送状态，参见 SmsSendStatusEnum 枚举类", example = "1")
    private Integer sendStatus;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "发送时间")
    private LocalDateTime[] sendTime;

    @Schema(description = "接收状态，参见 SmsReceiveStatusEnum 枚举类", example = "0")
    private Integer receiveStatus;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "接收时间")
    private LocalDateTime[] receiveTime;

}