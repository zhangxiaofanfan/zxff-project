package com.zhangxiaofanfan.cloud.module.sso.service.permission.impl;

import cn.hutool.core.collection.CollUtil;
import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.permission.MenuDO;
import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.permission.RoleMenuDO;
import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.permission.UserRoleDO;
import com.zhangxiaofanfan.cloud.module.sso.dao.mysql.permission.RoleMenuMapper;
import com.zhangxiaofanfan.cloud.module.sso.dao.mysql.permission.UserRoleMapper;
import com.zhangxiaofanfan.cloud.module.sso.dao.redis.RedisKeyConstants;
import com.zhangxiaofanfan.cloud.module.sso.service.permission.MenuService;
import com.zhangxiaofanfan.cloud.module.sso.service.permission.PermissionService;
import com.zhangxiaofanfan.cloud.module.sso.service.permission.RoleService;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static com.zhangxiaofanfan.cloud.module.common.util.collection.CollectionUtils.convertSet;

@Service
public class PermissionServiceImpl implements PermissionService {
    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    private RoleMenuMapper roleMenuMapper;
    @Resource
    private RoleService roleService;
    @Resource
    private MenuService menuService;

    @Override
    @CacheEvict(value = RedisKeyConstants.USER_ROLE_ID_LIST, key = "#userId")
    public void processUserDeleted(Long userId) {
        userRoleMapper.deleteListByUserId(userId);
    }

    @Override
    public Set<Long> getUserRoleIdListByUserId(Long userId) {
        return convertSet(userRoleMapper.selectListByUserId(userId), UserRoleDO::getRoleId);
    }

    @Override
    public Set<Long> getRoleMenuListByRoleId(Collection<Long> roleIds) {
        if (CollUtil.isEmpty(roleIds)) {
            return Collections.emptySet();
        }

        // 如果是管理员的情况下，获取全部菜单编号
        if (roleService.hasAnySuperAdmin(roleIds)) {
            return convertSet(menuService.getMenuList(), MenuDO::getId);
        }
        // 如果是非管理员的情况下，获得拥有的菜单编号
        return convertSet(roleMenuMapper.selectListByRoleId(roleIds), RoleMenuDO::getMenuId);
    }
}
