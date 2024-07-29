package com.zhangxiaofanfan.cloud.module.sso.service.oauth2.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.zhangxiaofanfan.cloud.module.common.enums.CommonStatusEnum;
import com.zhangxiaofanfan.cloud.module.common.util.string.StrUtils;
import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.oauth2.OAuth2ClientDO;
import com.zhangxiaofanfan.cloud.module.sso.dao.mysql.oauth2.OAuth2ClientMapper;
import com.zhangxiaofanfan.cloud.module.sso.dao.redis.RedisKeyConstants;
import com.zhangxiaofanfan.cloud.module.sso.service.oauth2.OAuth2ClientService;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static com.zhangxiaofanfan.cloud.module.common.exception.util.ServiceExceptionUtil.exception;
import static com.zhangxiaofanfan.cloud.module.sso.enums.ErrorCodeConstants.OAUTH2_CLIENT_AUTHORIZED_GRANT_TYPE_NOT_EXISTS;
import static com.zhangxiaofanfan.cloud.module.sso.enums.ErrorCodeConstants.OAUTH2_CLIENT_CLIENT_SECRET_ERROR;
import static com.zhangxiaofanfan.cloud.module.sso.enums.ErrorCodeConstants.OAUTH2_CLIENT_DISABLE;
import static com.zhangxiaofanfan.cloud.module.sso.enums.ErrorCodeConstants.OAUTH2_CLIENT_NOT_EXISTS;
import static com.zhangxiaofanfan.cloud.module.sso.enums.ErrorCodeConstants.OAUTH2_CLIENT_REDIRECT_URI_NOT_MATCH;
import static com.zhangxiaofanfan.cloud.module.sso.enums.ErrorCodeConstants.OAUTH2_CLIENT_SCOPE_OVER;

@Service
public class OAuth2ClientServiceImpl implements OAuth2ClientService {

    @Resource
    private OAuth2ClientMapper oauth2ClientMapper;

    @Override
    public OAuth2ClientDO validOAuthClientFromCache(String clientId, String clientSecret, String authorizedGrantType,
                                                    Collection<String> scopes, String redirectUri) {
        // 校验客户端存在、且开启
        OAuth2ClientDO client = getSelf().getOAuth2ClientFromCache(clientId);
        if (client == null) {
            throw exception(OAUTH2_CLIENT_NOT_EXISTS);
        }
        if (CommonStatusEnum.isDisable(client.getStatus())) {
            throw exception(OAUTH2_CLIENT_DISABLE);
        }

        // 校验客户端密钥
        if (StrUtil.isNotEmpty(clientSecret) && ObjectUtil.notEqual(client.getSecret(), clientSecret)) {
            throw exception(OAUTH2_CLIENT_CLIENT_SECRET_ERROR);
        }
        // 校验授权方式
        if (StrUtil.isNotEmpty(authorizedGrantType) &&
                !CollUtil.contains(client.getAuthorizedGrantTypes(), authorizedGrantType)) {
            throw exception(OAUTH2_CLIENT_AUTHORIZED_GRANT_TYPE_NOT_EXISTS);
        }
        // 校验授权范围
        if (CollUtil.isNotEmpty(scopes) && !CollUtil.containsAll(client.getScopes(), scopes)) {
            throw exception(OAUTH2_CLIENT_SCOPE_OVER);
        }
        // 校验回调地址
        if (StrUtil.isNotEmpty(redirectUri) && !StrUtils.startWithAny(redirectUri, client.getRedirectUris())) {
            throw exception(OAUTH2_CLIENT_REDIRECT_URI_NOT_MATCH, redirectUri);
        }
        return client;
    }

    @Override
    @Cacheable(
            cacheNames = RedisKeyConstants.OAUTH_CLIENT,
            key = "#clientId",
            unless = "#result == null"
    )
    public OAuth2ClientDO getOAuth2ClientFromCache(String clientId) {
        return oauth2ClientMapper.selectByClientId(clientId);
    }

    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private OAuth2ClientServiceImpl getSelf() {
        return SpringUtil.getBean(getClass());
    }

}
