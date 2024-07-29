package com.zhangxiaofanfan.cloud.module.sso.service.oauth2;

import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.oauth2.OAuth2ClientDO;

import java.util.Collection;

public interface OAuth2ClientService {

    /**
     * 获得 OAuth2 客户端，从缓存中
     *
     * @param clientId 客户端编号
     * @return OAuth2 客户端
     */
    OAuth2ClientDO getOAuth2ClientFromCache(String clientId);

    /**
     * 从缓存中，校验客户端是否合法
     *
     * @return 客户端
     */
    default OAuth2ClientDO validOAuthClientFromCache(String clientId) {
        return validOAuthClientFromCache(clientId, null, null, null, null);
    }

    /**
     * 从缓存中，校验客户端是否合法, 非空时，进行校验
     *
     * @param clientId 客户端编号
     * @param clientSecret 客户端密钥
     * @param authorizedGrantType 授权方式
     * @param scopes 授权范围
     * @param redirectUri 重定向地址
     * @return 客户端
     */
    OAuth2ClientDO validOAuthClientFromCache(
            String clientId,
            String clientSecret,
            String authorizedGrantType,
            Collection<String> scopes, String redirectUri
    );
}
