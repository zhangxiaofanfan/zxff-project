package com.zhangxiaofanfan.cloud.module.sso.service.social;

import com.zhangxiaofanfan.cloud.module.common.exception.ServiceException;
import com.zhangxiaofanfan.cloud.module.sso.api.social.dto.SocialUserBindReqDTO;
import com.zhangxiaofanfan.cloud.module.sso.api.social.dto.SocialUserRespDTO;
import jakarta.validation.Valid;

public interface SocialUserService {
    /**
     * 绑定社交用户
     *
     * @param reqDTO 绑定信息
     * @return 社交用户 openid
     */
    String bindSocialUser(@Valid SocialUserBindReqDTO reqDTO);

    /**
     * 获得社交用户
     *
     * 在认证信息不正确的情况下，也会抛出 {@link ServiceException} 业务异常
     *
     * @param userType 用户类型
     * @param socialType 社交平台的类型
     * @param code 授权码
     * @param state state
     * @return 社交用户
     */
    SocialUserRespDTO getSocialUserByCode(Integer userType, Integer socialType, String code, String state);

}
