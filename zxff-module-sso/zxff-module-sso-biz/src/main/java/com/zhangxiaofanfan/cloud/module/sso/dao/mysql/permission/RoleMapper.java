package com.zhangxiaofanfan.cloud.module.sso.dao.mysql.permission;

import com.zhangxiaofanfan.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.permission.RoleDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapperX<RoleDO> {

//    default PageResult<RoleDO> selectPage(RolePageReqVO reqVO) {
//        return selectPage(reqVO, new LambdaQueryWrapperX<RoleDO>()
//                .likeIfPresent(RoleDO::getName, reqVO.getName())
//                .likeIfPresent(RoleDO::getCode, reqVO.getCode())
//                .eqIfPresent(RoleDO::getStatus, reqVO.getStatus())
//                .betweenIfPresent(BaseDO::getCreateTime, reqVO.getCreateTime())
//                .orderByAsc(RoleDO::getSort));
//    }

    default RoleDO selectByName(String name) {
        return selectOne(RoleDO::getName, name);
    }

    default RoleDO selectByCode(String code) {
        return selectOne(RoleDO::getCode, code);
    }

    default List<RoleDO> selectListByStatus(@Nullable Collection<Integer> statuses) {
        return selectList(RoleDO::getStatus, statuses);
    }

}
