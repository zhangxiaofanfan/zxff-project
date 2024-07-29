package com.zhangxiaofanfan.cloud.module.sso.service.sms.impl;

import cn.hutool.core.util.StrUtil;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.zhangxiaofanfan.cloud.module.common.pojo.PageResult;
import com.zhangxiaofanfan.cloud.module.common.util.object.BeanUtils;
import com.zhangxiaofanfan.cloud.module.sso.controller.sms.vo.channel.SmsChannelPageReqVO;
import com.zhangxiaofanfan.cloud.module.sso.controller.sms.vo.channel.SmsChannelSaveReqVO;
import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.sms.SmsChannelDO;
import com.zhangxiaofanfan.cloud.module.sso.dao.mysql.sms.SmsChannelMapper;
import com.zhangxiaofanfan.cloud.module.sso.framework.sms.core.client.SmsClient;
import com.zhangxiaofanfan.cloud.module.sso.framework.sms.core.factory.SmsClientFactory;
import com.zhangxiaofanfan.cloud.module.sso.framework.sms.core.property.SmsChannelProperties;
import com.zhangxiaofanfan.cloud.module.sso.service.sms.SmsChannelService;
import com.zhangxiaofanfan.cloud.module.sso.service.sms.SmsTemplateService;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

import static com.zhangxiaofanfan.cloud.module.common.exception.util.ServiceExceptionUtil.exception;
import static com.zhangxiaofanfan.cloud.module.common.util.cache.CacheUtils.buildAsyncReloadingCache;
import static com.zhangxiaofanfan.cloud.module.sso.enums.ErrorCodeConstants.SMS_CHANNEL_HAS_CHILDREN;
import static com.zhangxiaofanfan.cloud.module.sso.enums.ErrorCodeConstants.SMS_CHANNEL_NOT_EXISTS;

/**
 * TODO
 *
 * @author zhangxiaofanfan
 * @date 2024-07-03 11:08:31
 */
@Service
public class SmsChannelServiceImpl implements SmsChannelService {

    /**
     * {@link SmsClient} 缓存，通过它异步刷新 smsClientFactory
     */
    @Getter
    private final LoadingCache<Long, SmsClient> idClientCache = buildAsyncReloadingCache(Duration.ofSeconds(10L),
            new CacheLoader<Long, SmsClient>() {

                @Override
                public SmsClient load(Long id) {
                    // 查询，然后尝试刷新
                    SmsChannelDO channel = smsChannelMapper.selectById(id);
                    if (channel != null) {
                        SmsChannelProperties properties = BeanUtils.toBean(channel, SmsChannelProperties.class);
                        smsClientFactory.createOrUpdateSmsClient(properties);
                    }
                    return smsClientFactory.getSmsClient(id);
                }

            });

    /**
     * {@link SmsClient} 缓存，通过它异步刷新 smsClientFactory
     */
    @Getter
    private final LoadingCache<String, SmsClient> codeClientCache = buildAsyncReloadingCache(Duration.ofSeconds(60L),
            new CacheLoader<String, SmsClient>() {

                @Override
                public SmsClient load(String code) {
                    // 查询，然后尝试刷新
                    SmsChannelDO channel = smsChannelMapper.selectByCode(code);
                    if (channel != null) {
                        SmsChannelProperties properties = BeanUtils.toBean(channel, SmsChannelProperties.class);
                        smsClientFactory.createOrUpdateSmsClient(properties);
                    }
                    return smsClientFactory.getSmsClient(code);
                }

            });

    @Resource
    private SmsClientFactory smsClientFactory;

    @Resource
    private SmsChannelMapper smsChannelMapper;

    @Resource
    private SmsTemplateService smsTemplateService;

    @Override
    public Long createSmsChannel(SmsChannelSaveReqVO createReqVO) {
        SmsChannelDO channel = BeanUtils.toBean(createReqVO, SmsChannelDO.class);
        smsChannelMapper.insert(channel);
        return channel.getId();
    }

    @Override
    public void updateSmsChannel(SmsChannelSaveReqVO updateReqVO) {
        // 校验存在
        SmsChannelDO channel = validateSmsChannelExists(updateReqVO.getId());
        // 更新
        SmsChannelDO updateObj = BeanUtils.toBean(updateReqVO, SmsChannelDO.class);
        smsChannelMapper.updateById(updateObj);

        // 清空缓存
        clearCache(updateReqVO.getId(), channel.getCode());
    }

    @Override
    public void deleteSmsChannel(Long id) {
        // 校验存在
        SmsChannelDO channel = validateSmsChannelExists(id);
        // 校验是否有在使用该账号的模版
        if (smsTemplateService.getSmsTemplateCountByChannelId(id) > 0) {
            throw exception(SMS_CHANNEL_HAS_CHILDREN);
        }
        // 删除
        smsChannelMapper.deleteById(id);

        // 清空缓存
        clearCache(id, channel.getCode());
    }

    /**
     * 清空指定渠道编号的缓存
     *
     * @param id 渠道编号
     * @param code 渠道编码
     */
    private void clearCache(Long id, String code) {
        idClientCache.invalidate(id);
        if (StrUtil.isNotEmpty(code)) {
            codeClientCache.invalidate(code);
        }
    }

    private SmsChannelDO validateSmsChannelExists(Long id) {
        SmsChannelDO channel = smsChannelMapper.selectById(id);
        if (channel == null) {
            throw exception(SMS_CHANNEL_NOT_EXISTS);
        }
        return channel;
    }

    @Override
    public SmsChannelDO getSmsChannel(Long id) {
        return smsChannelMapper.selectById(id);
    }

    @Override
    public List<SmsChannelDO> getSmsChannelList() {
        return smsChannelMapper.selectList();
    }

    @Override
    public PageResult<SmsChannelDO> getSmsChannelPage(SmsChannelPageReqVO pageReqVO) {
        return smsChannelMapper.selectPage(pageReqVO);
    }

    @Override
    public SmsClient getSmsClient(Long id) {
        return idClientCache.getUnchecked(id);
    }

    @Override
    public SmsClient getSmsClient(String code) {
        return codeClientCache.getUnchecked(code);
    }

}
