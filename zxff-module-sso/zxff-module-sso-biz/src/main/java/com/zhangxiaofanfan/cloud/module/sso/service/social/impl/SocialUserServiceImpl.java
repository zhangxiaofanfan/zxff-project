package com.zhangxiaofanfan.cloud.module.sso.service.social.impl;

import cn.hutool.core.lang.Assert;
import com.xingyuv.jushauth.model.AuthUser;
import com.zhangxiaofanfan.cloud.module.common.exception.ServiceException;
import com.zhangxiaofanfan.cloud.module.sso.api.social.dto.SocialUserBindReqDTO;
import com.zhangxiaofanfan.cloud.module.sso.api.social.dto.SocialUserRespDTO;
import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.social.SocialUserBindDO;
import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.social.SocialUserDO;
import com.zhangxiaofanfan.cloud.module.sso.dao.mysql.social.SocialUserBindMapper;
import com.zhangxiaofanfan.cloud.module.sso.dao.mysql.social.SocialUserMapper;
import com.zhangxiaofanfan.cloud.module.sso.enums.social.SocialTypeEnum;
import com.zhangxiaofanfan.cloud.module.sso.service.social.SocialClientService;
import com.zhangxiaofanfan.cloud.module.sso.service.social.SocialUserService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.zhangxiaofanfan.cloud.module.common.util.json.JsonUtils.toJsonString;

@Service
public class SocialUserServiceImpl implements SocialUserService {
    @Resource
    private SocialUserBindMapper socialUserBindMapper;
    @Resource
    private SocialUserMapper socialUserMapper;
    @Resource
    private SocialClientService socialClientService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public String bindSocialUser(SocialUserBindReqDTO reqDTO) {
        // 获得社交用户
        SocialUserDO socialUser = authSocialUser(
                reqDTO.getSocialType(),
                reqDTO.getUserType(),
                reqDTO.getCode(),
                reqDTO.getState()
        );
        Assert.notNull(socialUser, "社交用户不能为空");

        // 社交用户可能之前绑定过别的用户，需要进行解绑
        socialUserBindMapper.deleteByUserTypeAndSocialUserId(reqDTO.getUserType(), socialUser.getId());

        // 用户可能之前已经绑定过该社交类型，需要进行解绑
        socialUserBindMapper.deleteByUserTypeAndUserIdAndSocialType(reqDTO.getUserType(), reqDTO.getUserId(),
                socialUser.getType());

        // 绑定当前登录的社交用户
        SocialUserBindDO socialUserBind = SocialUserBindDO.builder()
                .userId(reqDTO.getUserId()).userType(reqDTO.getUserType())
                .socialUserId(socialUser.getId()).socialType(socialUser.getType()).build();
        socialUserBindMapper.insert(socialUserBind);
        return socialUser.getOpenid();
    }

    @Override
    public SocialUserRespDTO getSocialUserByCode(Integer userType, Integer socialType, String code, String state) {
        // 获得社交用户
        SocialUserDO socialUser = authSocialUser(socialType, userType, code, state);
        Assert.notNull(socialUser, "社交用户不能为空");

        // 获得绑定用户
        SocialUserBindDO socialUserBind = socialUserBindMapper.selectByUserTypeAndSocialUserId(
                userType, socialUser.getId()
        );
        return new SocialUserRespDTO(socialUser.getOpenid(), socialUser.getNickname(), socialUser.getAvatar(),
                socialUserBind != null ? socialUserBind.getUserId() : null);
    }

    /**
     * 授权获得对应的社交用户
     * 如果授权失败，则会抛出 {@link ServiceException} 异常
     *
     * @param socialType 社交平台的类型 {@link SocialTypeEnum}
     * @param userType 用户类型
     * @param code     授权码
     * @param state    state
     * @return 授权用户
     */
    @NotNull
    public SocialUserDO authSocialUser(Integer socialType, Integer userType, String code, String state) {
        // 优先从 DB 中获取，因为 code 有且可以使用一次。
        // 在社交登录时，当未绑定 User 时，需要绑定登录，此时需要 code 使用两次
        SocialUserDO socialUser = socialUserMapper.selectByTypeAndCodeAnState(socialType, code, state);
        if (socialUser != null) {
            return socialUser;
        }

        // 请求获取
        AuthUser authUser = socialClientService.getAuthUser(socialType, userType, code, state);
        Assert.notNull(authUser, "三方用户不能为空");

        // 保存到 DB 中
        socialUser = socialUserMapper.selectByTypeAndOpenid(socialType, authUser.getUuid());
        if (socialUser == null) {
            socialUser = new SocialUserDO();
        }
        socialUser
                .setType(socialType).setCode(code).setState(state) // 需要保存 code + state 字段，保证后续可查询
                .setOpenid(authUser.getUuid())
                .setToken(authUser.getToken().getAccessToken())
                .setRawTokenInfo((toJsonString(authUser.getToken())))
                .setNickname(authUser.getNickname())
                .setAvatar(authUser.getAvatar()).setRawUserInfo(toJsonString(authUser.getRawUserInfo()));
        if (socialUser.getId() == null) {
            socialUserMapper.insert(socialUser);
        } else {
            socialUserMapper.updateById(socialUser);
        }
        return socialUser;
    }
}
