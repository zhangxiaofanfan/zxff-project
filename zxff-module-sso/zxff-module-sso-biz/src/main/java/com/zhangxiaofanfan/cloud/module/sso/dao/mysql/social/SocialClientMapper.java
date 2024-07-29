package com.zhangxiaofanfan.cloud.module.sso.dao.mysql.social;


import com.zhangxiaofanfan.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.zhangxiaofanfan.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.zhangxiaofanfan.cloud.module.common.pojo.PageResult;
import com.zhangxiaofanfan.cloud.module.sso.controller.social.vo.client.SocialClientPageReqVO;
import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.social.SocialClientDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SocialClientMapper extends BaseMapperX<SocialClientDO> {

    default SocialClientDO selectBySocialTypeAndUserType(Integer socialType, Integer userType) {
        return selectOne(SocialClientDO::getSocialType, socialType,
                SocialClientDO::getUserType, userType);
    }

    default PageResult<SocialClientDO> selectPage(SocialClientPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SocialClientDO>()
                .likeIfPresent(SocialClientDO::getName, reqVO.getName())
                .eqIfPresent(SocialClientDO::getSocialType, reqVO.getSocialType())
                .eqIfPresent(SocialClientDO::getUserType, reqVO.getUserType())
                .likeIfPresent(SocialClientDO::getClientId, reqVO.getClientId())
                .eqIfPresent(SocialClientDO::getStatus, reqVO.getStatus())
                .orderByDesc(SocialClientDO::getId));
    }

}
