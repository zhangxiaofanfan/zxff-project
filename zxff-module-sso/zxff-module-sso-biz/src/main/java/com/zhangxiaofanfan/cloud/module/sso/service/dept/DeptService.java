package com.zhangxiaofanfan.cloud.module.sso.service.dept;

import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.dept.DeptDO;

public interface DeptService {
    /**
     * 获得部门信息
     *
     * @param id 部门编号
     * @return 部门信息
     */
    DeptDO getDept(Long id);

}
