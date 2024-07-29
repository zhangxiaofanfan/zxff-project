package com.zhangxiaofanfan.cloud.module.sso.service.social;

import com.xingyuv.jushauth.model.AuthUser;
import com.zhangxiaofanfan.cloud.module.sso.enums.social.SocialTypeEnum;

public interface SocialClientService {

    /**
     * 请求社交平台，获得授权的用户
     *
     * @param socialType 社交平台的类型
     * @param userType 用户类型
     * @param code 授权码
     * @param state 授权 state
     * @return 授权的用户
     */
    AuthUser getAuthUser(Integer socialType, Integer userType, String code, String state);

    /**
     * 获得社交平台的授权 URL
     *
     * @param socialType 社交平台的类型 {@link SocialTypeEnum}
     * @param userType 用户类型
     * @param redirectUri 重定向 URL
     * @return 社交平台的授权 URL
     */
    String getAuthorizeUrl(Integer socialType, Integer userType, String redirectUri);

}
