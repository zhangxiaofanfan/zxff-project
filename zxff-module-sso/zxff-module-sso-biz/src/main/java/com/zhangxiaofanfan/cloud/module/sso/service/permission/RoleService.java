package com.zhangxiaofanfan.cloud.module.sso.service.permission;

import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.permission.RoleDO;

import java.util.Collection;
import java.util.List;

public interface RoleService {
    /**
     * 获得角色，从缓存中
     *
     * @param id 角色编号
     * @return 角色
     */
    RoleDO getRoleFromCache(Long id);

    /**
     * 判断角色编号数组中，是否有管理员
     *
     * @param ids 角色编号数组
     * @return 是否有管理员
     */
    boolean hasAnySuperAdmin(Collection<Long> ids);

    /**
     * 获得角色列表
     *
     * @param ids 角色编号数组
     * @return 角色列表
     */
    List<RoleDO> getRoleList(Collection<Long> ids);


}
