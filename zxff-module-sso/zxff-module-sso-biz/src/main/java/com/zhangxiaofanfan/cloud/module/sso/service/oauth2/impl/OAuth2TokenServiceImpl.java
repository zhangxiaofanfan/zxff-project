package com.zhangxiaofanfan.cloud.module.sso.service.oauth2.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.zhangxiaofanfan.cloud.module.common.enums.UserTypeEnum;
import com.zhangxiaofanfan.cloud.module.common.exception.enums.GlobalErrorCodeConstants;
import com.zhangxiaofanfan.cloud.module.common.pojo.user.LoginUser;
import com.zhangxiaofanfan.cloud.module.common.util.date.DateUtils;
import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.oauth2.OAuth2AccessTokenDO;
import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.oauth2.OAuth2ClientDO;
import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.oauth2.OAuth2RefreshTokenDO;
import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.user.UserDO;
import com.zhangxiaofanfan.cloud.module.sso.dao.mysql.oauth2.OAuth2AccessTokenMapper;
import com.zhangxiaofanfan.cloud.module.sso.dao.mysql.oauth2.OAuth2RefreshTokenMapper;
import com.zhangxiaofanfan.cloud.module.sso.dao.redis.oauth2.OAuth2AccessTokenRedisDAO;
import com.zhangxiaofanfan.cloud.module.sso.service.oauth2.OAuth2ClientService;
import com.zhangxiaofanfan.cloud.module.sso.service.oauth2.OAuth2TokenService;
import com.zhangxiaofanfan.cloud.module.sso.service.user.UserService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.zhangxiaofanfan.cloud.module.common.exception.util.ServiceExceptionUtil.exception0;
import static com.zhangxiaofanfan.cloud.module.common.util.collection.CollectionUtils.convertSet;

@Service
public class OAuth2TokenServiceImpl implements OAuth2TokenService {

    @Resource
    private OAuth2ClientService oauth2ClientService;
    @Resource
    @Lazy // 懒加载，避免循环依赖
    private UserService userService;

    @Resource
    private OAuth2AccessTokenMapper oauth2AccessTokenMapper;
    @Resource
    private OAuth2RefreshTokenMapper oauth2RefreshTokenMapper;

    @Resource
    private OAuth2AccessTokenRedisDAO oauth2AccessTokenRedisDAO;


    @Override
    @Transactional
    public OAuth2AccessTokenDO createAccessToken(Long userId, Integer userType, String clientId, List<String> scopes) {
        OAuth2ClientDO clientDO = oauth2ClientService.validOAuthClientFromCache(clientId);
        // 创建刷新令牌
        OAuth2RefreshTokenDO refreshTokenDO = createOAuth2RefreshToken(userId, userType, clientDO, scopes);
        // 创建访问令牌
        return createOAuth2AccessToken(refreshTokenDO, clientDO);
    }

    @Override
    public OAuth2AccessTokenDO removeAccessToken(String accessToken) {
        // 删除访问令牌
        OAuth2AccessTokenDO accessTokenDO = oauth2AccessTokenMapper.selectByAccessToken(accessToken);
        if (accessTokenDO == null) {
            return null;
        }
        oauth2AccessTokenMapper.deleteById(accessTokenDO.getId());
        oauth2AccessTokenRedisDAO.delete(accessToken);
        // 删除刷新令牌
        oauth2RefreshTokenMapper.deleteByRefreshToken(accessTokenDO.getRefreshToken());
        return accessTokenDO;
    }

    @Override
    public OAuth2AccessTokenDO refreshAccessToken(String refreshToken, String clientId) {
        // 查询访问令牌
        OAuth2RefreshTokenDO refreshTokenDO = oauth2RefreshTokenMapper.selectByRefreshToken(refreshToken);
        if (refreshTokenDO == null) {
            throw exception0(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), "无效的刷新令牌");
        }

        // 校验 Client 匹配
        OAuth2ClientDO clientDO = oauth2ClientService.validOAuthClientFromCache(clientId);
        if (ObjectUtil.notEqual(clientId, refreshTokenDO.getClientId())) {
            throw exception0(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), "刷新令牌的客户端编号不正确");
        }

        // 移除相关的访问令牌
        List<OAuth2AccessTokenDO> accessTokenDOs = oauth2AccessTokenMapper.selectListByRefreshToken(refreshToken);
        if (CollUtil.isNotEmpty(accessTokenDOs)) {
            oauth2AccessTokenMapper.deleteBatchIds(convertSet(accessTokenDOs, OAuth2AccessTokenDO::getId));
            oauth2AccessTokenRedisDAO.deleteList(convertSet(accessTokenDOs, OAuth2AccessTokenDO::getAccessToken));
        }

        // 已过期的情况下，删除刷新令牌
        if (DateUtils.isExpired(refreshTokenDO.getExpiresTime())) {
            oauth2RefreshTokenMapper.deleteById(refreshTokenDO.getId());
            throw exception0(GlobalErrorCodeConstants.UNAUTHORIZED.getCode(), "刷新令牌已过期");
        }

        // 创建访问令牌
        return createOAuth2AccessToken(refreshTokenDO, clientDO);
    }

    private OAuth2AccessTokenDO createOAuth2AccessToken(OAuth2RefreshTokenDO refreshTokenDO, OAuth2ClientDO clientDO) {
        OAuth2AccessTokenDO accessTokenDO = new OAuth2AccessTokenDO().setAccessToken(generateAccessToken())
                .setUserId(refreshTokenDO.getUserId()).setUserType(refreshTokenDO.getUserType())
                .setUserInfo(buildUserInfo(refreshTokenDO.getUserId(), refreshTokenDO.getUserType()))
                .setClientId(clientDO.getClientId()).setScopes(refreshTokenDO.getScopes())
                .setRefreshToken(refreshTokenDO.getRefreshToken())
                .setExpiresTime(LocalDateTime.now().plusSeconds(clientDO.getAccessTokenValiditySeconds()));
        oauth2AccessTokenMapper.insert(accessTokenDO);
        // 记录到 Redis 中
        oauth2AccessTokenRedisDAO.set(accessTokenDO);
        return accessTokenDO;
    }

    private OAuth2RefreshTokenDO createOAuth2RefreshToken(
            Long userId,
            Integer userType,
            OAuth2ClientDO clientDO,
            List<String> scopes
    ) {
        OAuth2RefreshTokenDO refreshToken = new OAuth2RefreshTokenDO()
                .setRefreshToken(generateRefreshToken())
                .setUserId(userId)
                .setUserType(userType)
                .setClientId(clientDO.getClientId())
                .setScopes(scopes)
                .setExpiresTime(LocalDateTime.now().plusSeconds(clientDO.getRefreshTokenValiditySeconds()));
        oauth2RefreshTokenMapper.insert(refreshToken);
        return refreshToken;
    }

    /**
     * 加载用户信息，方便获取到昵称、部门等信息
     *
     * @param userId 用户编号
     * @param userType 用户类型
     * @return 用户信息
     */
    private Map<String, String> buildUserInfo(Long userId, Integer userType) {
        if (userType.equals(UserTypeEnum.ADMIN.getValue())) {
            UserDO user = userService.getUser(userId);
            return MapUtil.builder(LoginUser.INFO_KEY_NICKNAME, user.getNickname())
                    .put(LoginUser.INFO_KEY_DEPT_ID, StrUtil.toStringOrNull(user.getDeptId())).build();
        } else if (userType.equals(UserTypeEnum.MEMBER.getValue())) {
            // 注意：目前 Member 暂时不读取，可以按需实现
            return Collections.emptyMap();
        }
        return null;
    }

    /**
     * 生成访问令牌
     *
     * @return 访问令牌字符串
     */
    private static String generateAccessToken() {
        return IdUtil.fastSimpleUUID();
    }

    /**
     * 生成刷新令牌
     *
     * @return 刷新令牌字符串
     */
    private static String generateRefreshToken() {
        return IdUtil.fastSimpleUUID();
    }

}
