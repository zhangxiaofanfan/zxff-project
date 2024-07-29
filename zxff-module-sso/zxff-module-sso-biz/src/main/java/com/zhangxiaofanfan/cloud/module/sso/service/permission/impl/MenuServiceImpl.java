package com.zhangxiaofanfan.cloud.module.sso.service.permission.impl;

import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.permission.MenuDO;
import com.zhangxiaofanfan.cloud.module.sso.dao.mysql.permission.MenuMapper;
import com.zhangxiaofanfan.cloud.module.sso.service.permission.MenuService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {
    @Resource
    private MenuMapper menuMapper;

    @Override
    public List<MenuDO> getMenuList() {
        return menuMapper.selectList();
    }

    @Override
    public List<MenuDO> getMenuList(Collection<Long> ids) {
        return menuMapper.selectBatchIds(ids);
    }
}
