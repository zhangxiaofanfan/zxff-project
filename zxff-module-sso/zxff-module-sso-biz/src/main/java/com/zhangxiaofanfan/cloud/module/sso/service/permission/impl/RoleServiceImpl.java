package com.zhangxiaofanfan.cloud.module.sso.service.permission.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.permission.RoleDO;
import com.zhangxiaofanfan.cloud.module.sso.dao.mysql.permission.RoleMapper;
import com.zhangxiaofanfan.cloud.module.sso.dao.redis.RedisKeyConstants;
import com.zhangxiaofanfan.cloud.module.sso.enums.permission.RoleCodeEnum;
import com.zhangxiaofanfan.cloud.module.sso.service.permission.RoleService;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    @Resource
    private RoleMapper roleMapper;

    @Override
    public boolean hasAnySuperAdmin(Collection<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return false;
        }
        RoleServiceImpl self = getSelf();
        return ids.stream().anyMatch(id -> {
            RoleDO role = self.getRoleFromCache(id);
            return role != null && RoleCodeEnum.isSuperAdmin(role.getCode());
        });
    }

    @Override
    public List<RoleDO> getRoleList(Collection<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return roleMapper.selectBatchIds(ids);
    }

    @Override
    @Cacheable(
            value = RedisKeyConstants.ROLE,
            key = "#id",
            unless = "#result == null"
    )
    public RoleDO getRoleFromCache(Long id) {
        return roleMapper.selectById(id);
    }


    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private RoleServiceImpl getSelf() {
        return SpringUtil.getBean(getClass());
    }

}
