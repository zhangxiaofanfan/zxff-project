package com.zhangxiaofanfan.cloud.module.sso.service.sms.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import com.zhangxiaofanfan.cloud.module.sso.api.sms.dto.code.SmsCodeSendReqDTO;
import com.zhangxiaofanfan.cloud.module.sso.api.sms.dto.code.SmsCodeUseReqDTO;
import com.zhangxiaofanfan.cloud.module.sso.api.sms.dto.code.SmsCodeValidateReqDTO;
import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.sms.SmsCodeDO;
import com.zhangxiaofanfan.cloud.module.sso.dao.mysql.sms.SmsCodeMapper;
import com.zhangxiaofanfan.cloud.module.sso.enums.sms.SmsSceneEnum;
import com.zhangxiaofanfan.cloud.module.sso.framework.sms.config.SmsCodeProperties;
import com.zhangxiaofanfan.cloud.module.sso.service.sms.SmsCodeService;
import com.zhangxiaofanfan.cloud.module.sso.service.sms.SmsSendService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static cn.hutool.core.util.RandomUtil.randomInt;
import static com.zhangxiaofanfan.cloud.module.common.exception.util.ServiceExceptionUtil.exception;
import static com.zhangxiaofanfan.cloud.module.common.util.date.DateUtils.isToday;
import static com.zhangxiaofanfan.cloud.module.sso.enums.ErrorCodeConstants.SMS_CODE_EXCEED_SEND_MAXIMUM_QUANTITY_PER_DAY;
import static com.zhangxiaofanfan.cloud.module.sso.enums.ErrorCodeConstants.SMS_CODE_SEND_TOO_FAST;

/**
 * 短信验证码服务实现类
 *
 * @date 2024-06-14 03:14:32
 * @author zhangxiaofanfan
 */
@Service
public class SmsCodeServiceImpl implements SmsCodeService {

    @Resource
    private SmsCodeProperties smsCodeProperties;
    @Resource
    private SmsCodeMapper smsCodeMapper;
    @Resource
    private SmsSendService smsSendService;

    @Override
    public void sendSmsCode(SmsCodeSendReqDTO reqDTO) {
        SmsSceneEnum sceneEnum = SmsSceneEnum.getCodeByScene(reqDTO.getScene());
        Assert.notNull(sceneEnum, "验证码场景({}) 查找不到配置", reqDTO.getScene());
        // 创建验证码
        String code = createSmsCode(reqDTO.getMobile(), reqDTO.getScene(), reqDTO.getCreateIp());
        // 发送验证码
        smsSendService.sendSingleSms(reqDTO.getMobile(), null, null,
                sceneEnum.getTemplateCode(), MapUtil.of("code", code));
    }

    private String createSmsCode(String mobile, Integer scene, String ip) {
        // 校验是否可以发送验证码，不用筛选场景
        SmsCodeDO lastSmsCode = smsCodeMapper.selectLastByMobile(mobile, null, null);
        if (lastSmsCode != null) {
            if (LocalDateTimeUtil.between(lastSmsCode.getCreateTime(), LocalDateTime.now()).toMillis()
                    < smsCodeProperties.getSendFrequency().toMillis()) { // 发送过于频繁
                throw exception(SMS_CODE_SEND_TOO_FAST);
            }
            if (isToday(lastSmsCode.getCreateTime()) && // 必须是今天，才能计算超过当天的上限
                    lastSmsCode.getTodayIndex() >= smsCodeProperties.getSendMaximumQuantityPerDay()) { // 超过当天发送的上限。
                throw exception(SMS_CODE_EXCEED_SEND_MAXIMUM_QUANTITY_PER_DAY);
            }
            // TODO zxff：提升，每个 IP 每天可发送数量
            // TODO zxff：提升，每个 IP 每小时可发送数量
        }

        // 创建验证码记录
        String code = String.valueOf(randomInt(smsCodeProperties.getBeginCode(), smsCodeProperties.getEndCode() + 1));
        SmsCodeDO newSmsCode = SmsCodeDO.builder().mobile(mobile).code(code).scene(scene)
                .todayIndex(lastSmsCode != null && isToday(lastSmsCode.getCreateTime()) ? lastSmsCode.getTodayIndex() + 1 : 1)
                .createIp(ip).used(false).build();
        smsCodeMapper.insert(newSmsCode);
        return code;
    }

    @Override
    public void useSmsCode(SmsCodeUseReqDTO reqDTO) {

    }

    @Override
    public void validateSmsCode(SmsCodeValidateReqDTO reqDTO) {

    }
}
