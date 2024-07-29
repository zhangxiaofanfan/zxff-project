package com.zhangxiaofanfan.cloud.module.sso.dao.mysql.social;

import com.zhangxiaofanfan.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.zhangxiaofanfan.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.social.SocialUserBindDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SocialUserBindMapper extends BaseMapperX<SocialUserBindDO> {

    default void deleteByUserTypeAndUserIdAndSocialType(Integer userType, Long userId, Integer socialType) {
        delete(new LambdaQueryWrapperX<SocialUserBindDO>()
                .eq(SocialUserBindDO::getUserType, userType)
                .eq(SocialUserBindDO::getUserId, userId)
                .eq(SocialUserBindDO::getSocialType, socialType));
    }

    default void deleteByUserTypeAndSocialUserId(Integer userType, Long socialUserId) {
        delete(new LambdaQueryWrapperX<SocialUserBindDO>()
                .eq(SocialUserBindDO::getUserType, userType)
                .eq(SocialUserBindDO::getSocialUserId, socialUserId));
    }

    default SocialUserBindDO selectByUserTypeAndSocialUserId(Integer userType, Long socialUserId) {
        return selectOne(SocialUserBindDO::getUserType, userType,
                SocialUserBindDO::getSocialUserId, socialUserId);
    }

    default List<SocialUserBindDO> selectListByUserIdAndUserType(Long userId, Integer userType) {
        return selectList(new LambdaQueryWrapperX<SocialUserBindDO>()
                .eq(SocialUserBindDO::getUserId, userId)
                .eq(SocialUserBindDO::getUserType, userType));
    }

    default SocialUserBindDO selectByUserIdAndUserTypeAndSocialType(Long userId, Integer userType, Integer socialType) {
        return selectOne(new LambdaQueryWrapperX<SocialUserBindDO>()
                .eq(SocialUserBindDO::getUserId, userId)
                .eq(SocialUserBindDO::getUserType, userType)
                .eq(SocialUserBindDO::getSocialType, socialType));
    }

}
