package com.zhangxiaofanfan.cloud.module.sso.service.dept.impl;

import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.dept.PostDO;
import com.zhangxiaofanfan.cloud.module.sso.dao.mysql.dept.PostMapper;
import com.zhangxiaofanfan.cloud.module.sso.service.dept.PostService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {

    @Resource
    private PostMapper postMapper;

    @Override
    public PostDO getPost(Long id) {
        return postMapper.selectById(id);
    }
}
