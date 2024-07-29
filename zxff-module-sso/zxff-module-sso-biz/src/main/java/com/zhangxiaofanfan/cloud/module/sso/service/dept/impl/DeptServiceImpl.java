package com.zhangxiaofanfan.cloud.module.sso.service.dept.impl;

import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.dept.DeptDO;
import com.zhangxiaofanfan.cloud.module.sso.dao.mysql.dept.DeptMapper;
import com.zhangxiaofanfan.cloud.module.sso.service.dept.DeptService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class DeptServiceImpl implements DeptService {
    @Resource
    private DeptMapper deptMapper;

    @Override
    public DeptDO getDept(Long id) {
        return deptMapper.selectById(id);
    }
}
