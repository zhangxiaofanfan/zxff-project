package com.zhangxiaofanfan.cloud.module.sso.service.sms.impl;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.annotations.VisibleForTesting;
import com.zhangxiaofanfan.cloud.module.common.enums.CommonStatusEnum;
import com.zhangxiaofanfan.cloud.module.common.pojo.PageResult;
import com.zhangxiaofanfan.cloud.module.common.util.object.BeanUtils;
import com.zhangxiaofanfan.cloud.module.sso.controller.sms.vo.template.SmsTemplatePageReqVO;
import com.zhangxiaofanfan.cloud.module.sso.controller.sms.vo.template.SmsTemplateSaveReqVO;
import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.sms.SmsChannelDO;
import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.sms.SmsTemplateDO;
import com.zhangxiaofanfan.cloud.module.sso.dao.mysql.sms.SmsTemplateMapper;
import com.zhangxiaofanfan.cloud.module.sso.dao.redis.RedisKeyConstants;
import com.zhangxiaofanfan.cloud.module.sso.framework.sms.core.client.SmsClient;
import com.zhangxiaofanfan.cloud.module.sso.framework.sms.core.client.dto.response.SmsTemplateRespDTO;
import com.zhangxiaofanfan.cloud.module.sso.framework.sms.core.enums.SmsTemplateAuditStatusEnum;
import com.zhangxiaofanfan.cloud.module.sso.service.sms.SmsChannelService;
import com.zhangxiaofanfan.cloud.module.sso.service.sms.SmsTemplateService;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.zhangxiaofanfan.cloud.module.common.exception.util.ServiceExceptionUtil.exception;
import static com.zhangxiaofanfan.cloud.module.sso.enums.ErrorCodeConstants.SMS_CHANNEL_DISABLE;
import static com.zhangxiaofanfan.cloud.module.sso.enums.ErrorCodeConstants.SMS_CHANNEL_NOT_EXISTS;
import static com.zhangxiaofanfan.cloud.module.sso.enums.ErrorCodeConstants.SMS_TEMPLATE_API_AUDIT_CHECKING;
import static com.zhangxiaofanfan.cloud.module.sso.enums.ErrorCodeConstants.SMS_TEMPLATE_API_AUDIT_FAIL;
import static com.zhangxiaofanfan.cloud.module.sso.enums.ErrorCodeConstants.SMS_TEMPLATE_API_ERROR;
import static com.zhangxiaofanfan.cloud.module.sso.enums.ErrorCodeConstants.SMS_TEMPLATE_API_NOT_FOUND;
import static com.zhangxiaofanfan.cloud.module.sso.enums.ErrorCodeConstants.SMS_TEMPLATE_CODE_DUPLICATE;
import static com.zhangxiaofanfan.cloud.module.sso.enums.ErrorCodeConstants.SMS_TEMPLATE_NOT_EXISTS;

/**
 * TODO
 *
 * @author zhangxiaofanfan
 * @date 2024-07-03 11:16:22
 */
@Service
public class SmsTemplateServiceImpl implements SmsTemplateService {

    /**
     * 正则表达式，匹配 {} 中的变量
     */
    private static final Pattern PATTERN_PARAMS = Pattern.compile("\\{(.*?)}");

    @Resource
    private SmsTemplateMapper smsTemplateMapper;

    @Resource
    private SmsChannelService smsChannelService;

    @Override
    public Long createSmsTemplate(SmsTemplateSaveReqVO createReqVO) {
        // 校验短信渠道
        SmsChannelDO channelDO = validateSmsChannel(createReqVO.getChannelId());
        // 校验短信编码是否重复
        validateSmsTemplateCodeDuplicate(null, createReqVO.getCode());
        // 校验短信模板
        validateApiTemplate(createReqVO.getChannelId(), createReqVO.getApiTemplateId());

        // 插入
        SmsTemplateDO template = BeanUtils.toBean(createReqVO, SmsTemplateDO.class);
        template.setParams(parseTemplateContentParams(template.getContent()));
        template.setChannelCode(channelDO.getCode());
        smsTemplateMapper.insert(template);
        // 返回
        return template.getId();
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.SMS_TEMPLATE,
            allEntries = true) // allEntries 清空所有缓存，因为可能修改到 code 字段，不好清理
    public void updateSmsTemplate(SmsTemplateSaveReqVO updateReqVO) {
        // 校验存在
        validateSmsTemplateExists(updateReqVO.getId());
        // 校验短信渠道
        SmsChannelDO channelDO = validateSmsChannel(updateReqVO.getChannelId());
        // 校验短信编码是否重复
        validateSmsTemplateCodeDuplicate(updateReqVO.getId(), updateReqVO.getCode());
        // 校验短信模板
        validateApiTemplate(updateReqVO.getChannelId(), updateReqVO.getApiTemplateId());

        // 更新
        SmsTemplateDO updateObj = BeanUtils.toBean(updateReqVO, SmsTemplateDO.class);
        updateObj.setParams(parseTemplateContentParams(updateObj.getContent()));
        updateObj.setChannelCode(channelDO.getCode());
        smsTemplateMapper.updateById(updateObj);
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.SMS_TEMPLATE,
            allEntries = true) // allEntries 清空所有缓存，因为 id 不是直接的缓存 code，不好清理
    public void deleteSmsTemplate(Long id) {
        // 校验存在
        validateSmsTemplateExists(id);
        // 更新
        smsTemplateMapper.deleteById(id);
    }

    private void validateSmsTemplateExists(Long id) {
        if (smsTemplateMapper.selectById(id) == null) {
            throw exception(SMS_TEMPLATE_NOT_EXISTS);
        }
    }

    @Override
    public SmsTemplateDO getSmsTemplate(Long id) {
        return smsTemplateMapper.selectById(id);
    }

    @Override
    @Cacheable(cacheNames = RedisKeyConstants.SMS_TEMPLATE, key = "#code",
            unless = "#result == null")
    public SmsTemplateDO getSmsTemplateByCodeFromCache(String code) {
        return smsTemplateMapper.selectByCode(code);
    }

    @Override
    public PageResult<SmsTemplateDO> getSmsTemplatePage(SmsTemplatePageReqVO pageReqVO) {
        return smsTemplateMapper.selectPage(pageReqVO);
    }

    @Override
    public Long getSmsTemplateCountByChannelId(Long channelId) {
        return smsTemplateMapper.selectCountByChannelId(channelId);
    }

    @VisibleForTesting
    public SmsChannelDO validateSmsChannel(Long channelId) {
        SmsChannelDO channelDO = smsChannelService.getSmsChannel(channelId);
        if (channelDO == null) {
            throw exception(SMS_CHANNEL_NOT_EXISTS);
        }
        if (CommonStatusEnum.isDisable(channelDO.getStatus())) {
            throw exception(SMS_CHANNEL_DISABLE);
        }
        return channelDO;
    }

    @VisibleForTesting
    public void validateSmsTemplateCodeDuplicate(Long id, String code) {
        SmsTemplateDO template = smsTemplateMapper.selectByCode(code);
        if (template == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的字典类型
        if (id == null) {
            throw exception(SMS_TEMPLATE_CODE_DUPLICATE, code);
        }
        if (!template.getId().equals(id)) {
            throw exception(SMS_TEMPLATE_CODE_DUPLICATE, code);
        }
    }

    /**
     * 校验 API 短信平台的模板是否有效
     *
     * @param channelId 渠道编号
     * @param apiTemplateId API 模板编号
     */
    @VisibleForTesting
    void validateApiTemplate(Long channelId, String apiTemplateId) {
        // 获得短信模板
        SmsClient smsClient = smsChannelService.getSmsClient(channelId);
        Assert.notNull(smsClient, String.format("短信客户端(%d) 不存在", channelId));
        SmsTemplateRespDTO template;
        try {
            template = smsClient.getSmsTemplate(apiTemplateId);
        } catch (Throwable ex) {
            throw exception(SMS_TEMPLATE_API_ERROR, ExceptionUtil.getRootCauseMessage(ex));
        }
        // 校验短信模版
        if (template == null) {
            throw exception(SMS_TEMPLATE_API_NOT_FOUND);
        }
        if (Objects.equals(template.getAuditStatus(), SmsTemplateAuditStatusEnum.CHECKING.getStatus())) {
            throw exception(SMS_TEMPLATE_API_AUDIT_CHECKING);
        }
        if (Objects.equals(template.getAuditStatus(), SmsTemplateAuditStatusEnum.FAIL.getStatus())) {
            throw exception(SMS_TEMPLATE_API_AUDIT_FAIL, template.getAuditReason());
        }
        Assert.equals(template.getAuditStatus(), SmsTemplateAuditStatusEnum.SUCCESS.getStatus(),
                String.format("短信模板(%s) 审核状态(%d) 不正确", apiTemplateId, template.getAuditStatus()));
    }

    @Override
    public String formatSmsTemplateContent(String content, Map<String, Object> params) {
        return StrUtil.format(content, params);
    }

    @VisibleForTesting
    List<String> parseTemplateContentParams(String content) {
        return ReUtil.findAllGroup1(PATTERN_PARAMS, content);
    }

}
