package com.zhangxiaofanfan.cloud.module.sso.service.permission;

import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.permission.MenuDO;

import java.util.Collection;
import java.util.List;

public interface MenuService {
    /**
     * 获得所有菜单列表
     *
     * @return 菜单列表
     */
    List<MenuDO> getMenuList();

    /**
     * 获得菜单数组
     *
     * @param ids 菜单编号数组
     * @return 菜单数组
     */
    List<MenuDO> getMenuList(Collection<Long> ids);

}
