package com.zhangxiaofanfan.cloud.module.sso.service.social.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReflectUtil;
import com.google.common.annotations.VisibleForTesting;
import com.xingyuv.jushauth.config.AuthConfig;
import com.xingyuv.jushauth.model.AuthCallback;
import com.xingyuv.jushauth.model.AuthResponse;
import com.xingyuv.jushauth.model.AuthUser;
import com.xingyuv.jushauth.request.AuthRequest;
import com.xingyuv.jushauth.utils.AuthStateUtils;
import com.xingyuv.justauth.AuthRequestFactory;
import com.zhangxiaofanfan.cloud.module.common.enums.CommonStatusEnum;
import com.zhangxiaofanfan.cloud.module.common.util.http.HttpUtils;
import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.social.SocialClientDO;
import com.zhangxiaofanfan.cloud.module.sso.dao.mysql.social.SocialClientMapper;
import com.zhangxiaofanfan.cloud.module.sso.enums.social.SocialTypeEnum;
import com.zhangxiaofanfan.cloud.module.sso.service.social.SocialClientService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.zhangxiaofanfan.cloud.module.common.exception.util.ServiceExceptionUtil.exception;
import static com.zhangxiaofanfan.cloud.module.common.util.json.JsonUtils.toJsonString;
import static com.zhangxiaofanfan.cloud.module.sso.enums.ErrorCodeConstants.SOCIAL_USER_AUTH_FAILURE;

/**
 * 社交应用 Service 实现类
 *
 * @date 2024-06-05 08:31:55
 * @author zhangxiaofanfan
 */
@Slf4j
@Service
public class SocialClientServiceImpl implements SocialClientService {
    @Resource
    private SocialClientMapper socialClientMapper;
    @Resource
    private AuthRequestFactory authRequestFactory;

    @Override
    public AuthUser getAuthUser(Integer socialType, Integer userType, String code, String state) {
        // 构建请求
        AuthRequest authRequest = buildAuthRequest(socialType, userType);
        AuthCallback authCallback = AuthCallback.builder().code(code).state(state).build();
        // 执行请求
        AuthResponse<?> authResponse = authRequest.login(authCallback);
        log.info("[getAuthUser][请求社交平台 type({}) request({}) response({})]", socialType,
                toJsonString(authCallback), toJsonString(authResponse));
        if (!authResponse.ok()) {
            throw exception(SOCIAL_USER_AUTH_FAILURE, authResponse.getMsg());
        }
        return (AuthUser) authResponse.getData();
    }

    @Override
    public String getAuthorizeUrl(Integer socialType, Integer userType, String redirectUri) {
        // 获得对应的 AuthRequest 实现
        AuthRequest authRequest = buildAuthRequest(socialType, userType);
        // 生成跳转地址
        String authorizeUri = authRequest.authorize(AuthStateUtils.createState());
        return HttpUtils.replaceUrlQuery(authorizeUri, "redirect_uri", redirectUri);
    }

    /**
     * 构建 AuthRequest 对象，支持多租户配置
     *
     * @param socialType 社交类型
     * @param userType 用户类型
     * @return AuthRequest 对象
     */
    @VisibleForTesting
    AuthRequest buildAuthRequest(Integer socialType, Integer userType) {
        // 1. 先查找默认的配置项，从 application-*.yaml 中读取
        AuthRequest request = authRequestFactory.get(SocialTypeEnum.valueOfType(socialType).getSource());
        Assert.notNull(request, String.format("社交平台(%d) 不存在", socialType));
        // 2. 查询 DB 的配置项，如果存在则进行覆盖
        SocialClientDO client = socialClientMapper.selectBySocialTypeAndUserType(socialType, userType);
        if (client != null && Objects.equals(client.getStatus(), CommonStatusEnum.ENABLE.getStatus())) {
            // 2.1 构造新的 AuthConfig 对象
            AuthConfig authConfig = (AuthConfig) ReflectUtil.getFieldValue(request, "config");
            AuthConfig newAuthConfig = ReflectUtil.newInstance(authConfig.getClass());
            BeanUtil.copyProperties(authConfig, newAuthConfig);
            // 2.2 修改对应的 clientId + clientSecret 密钥
            newAuthConfig.setClientId(client.getClientId());
            newAuthConfig.setClientSecret(client.getClientSecret());
            if (client.getAgentId() != null) { // 如果有 agentId 则修改 agentId
                newAuthConfig.setAgentId(client.getAgentId());
            }
            // 2.3 设置会 request 里，进行后续使用
            ReflectUtil.setFieldValue(request, "config", newAuthConfig);
        }
        return request;
    }
}
