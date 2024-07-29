package com.zhangxiaofanfan.cloud.module.sso.service.dept;

import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.dept.PostDO;

public interface PostService {

    /**
     * 获得岗位信息
     *
     * @param id 岗位编号
     * @return 岗位信息
     */
    PostDO getPost(Long id);

}
