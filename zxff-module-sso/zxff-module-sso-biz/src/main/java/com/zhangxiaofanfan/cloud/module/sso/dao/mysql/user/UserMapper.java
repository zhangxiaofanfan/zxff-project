package com.zhangxiaofanfan.cloud.module.sso.dao.mysql.user;

import com.zhangxiaofanfan.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.zhangxiaofanfan.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.zhangxiaofanfan.cloud.module.common.pojo.PageResult;
import com.zhangxiaofanfan.cloud.module.sso.controller.user.vo.user.request.UserPageReqVO;
import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.user.UserDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface UserMapper extends BaseMapperX<UserDO> {
    default UserDO selectByUsername(String username) {
        return selectOne(UserDO::getUsername, username);
    }

    default UserDO selectByEmail(String email) {
        return selectOne(UserDO::getEmail, email);
    }

    default UserDO selectByMobile(String mobile) {
        return selectOne(UserDO::getMobile, mobile);
    }

    default PageResult<UserDO> selectPage(UserPageReqVO reqVO, Collection<Long> deptIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<UserDO>()
                .likeIfPresent(UserDO::getUsername, reqVO.getUsername())
                .likeIfPresent(UserDO::getMobile, reqVO.getMobile())
                .eqIfPresent(UserDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(UserDO::getCreateTime, reqVO.getCreateTime())
                .inIfPresent(UserDO::getDeptId, deptIds)
                .orderByDesc(UserDO::getId));
    }

    default List<UserDO> selectListByNickname(String nickname) {
        return selectList(new LambdaQueryWrapperX<UserDO>().like(UserDO::getNickname, nickname));
    }

    default List<UserDO> selectListByStatus(Integer status) {
        return selectList(UserDO::getStatus, status);
    }

    default List<UserDO> selectListByDeptIds(Collection<Long> deptIds) {
        return selectList(UserDO::getDeptId, deptIds);
    }

}
