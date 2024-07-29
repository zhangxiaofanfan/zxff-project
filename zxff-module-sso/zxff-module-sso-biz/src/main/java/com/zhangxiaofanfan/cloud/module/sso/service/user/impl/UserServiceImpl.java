package com.zhangxiaofanfan.cloud.module.sso.service.user.impl;

import com.google.common.annotations.VisibleForTesting;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.starter.annotation.LogRecord;
import com.zhangxiaofanfan.cloud.module.sso.controller.user.vo.prifile.request.UserProfileUpdatePasswordReqVO;
import com.zhangxiaofanfan.cloud.module.sso.controller.user.vo.user.request.UserSaveReqVO;
import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.user.UserDO;
import com.zhangxiaofanfan.cloud.module.sso.dao.mysql.dept.UserPostMapper;
import com.zhangxiaofanfan.cloud.module.sso.dao.mysql.user.UserMapper;
import com.zhangxiaofanfan.cloud.module.sso.service.permission.PermissionService;
import com.zhangxiaofanfan.cloud.module.sso.service.user.UserService;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.zhangxiaofanfan.cloud.module.common.exception.util.ServiceExceptionUtil.exception;
import static com.zhangxiaofanfan.cloud.module.sso.enums.ErrorCodeConstants.USER_NOT_EXISTS;
import static com.zhangxiaofanfan.cloud.module.sso.enums.ErrorCodeConstants.USER_PASSWORD_FAILED;
import static com.zhangxiaofanfan.cloud.module.sso.enums.LogRecordConstants.SYSTEM_USER_DELETE_SUB_TYPE;
import static com.zhangxiaofanfan.cloud.module.sso.enums.LogRecordConstants.SYSTEM_USER_DELETE_SUCCESS;
import static com.zhangxiaofanfan.cloud.module.sso.enums.LogRecordConstants.SYSTEM_USER_TYPE;
import static com.zhangxiaofanfan.cloud.module.sso.enums.LogRecordConstants.SYSTEM_USER_UPDATE_PASSWORD_SUB_TYPE;
import static com.zhangxiaofanfan.cloud.module.sso.enums.LogRecordConstants.SYSTEM_USER_UPDATE_PASSWORD_SUCCESS;

/**
 * TODO
 *
 * @date 2024-06-05 05:00:57
 * @author zhangxiaofanfan
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserPostMapper userPostMapper;
    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private PermissionService permissionService;


    @Override
    public UserDO getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public boolean isPasswordMatch(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public void updateUserLogin(Long id, String loginIp) {
        userMapper.updateById(new UserDO().setId(id).setLoginIp(loginIp).setLoginDate(LocalDateTime.now()));
    }

    @Override
    public UserDO getUser(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public Long createUser(UserSaveReqVO createReqVO) {
        return null;
    }

    @Override
    public void updateUser(UserSaveReqVO updateReqVO) {

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(
            type = SYSTEM_USER_TYPE,
            subType = SYSTEM_USER_DELETE_SUB_TYPE,
            bizNo = "{{#id}}",
            success = SYSTEM_USER_DELETE_SUCCESS)
    public void deleteUser(Long id) {
        // 1. 校验用户存在
        UserDO user = validateUserExists(id);

        // 2.1 删除用户
        userMapper.deleteById(id);
        // 2.2 删除用户关联数据
        permissionService.processUserDeleted(id);
        // 2.2 删除用户岗位
        userPostMapper.deleteByUserId(id);

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable("user", user);
    }

    @Override
    @LogRecord(
            type = SYSTEM_USER_TYPE,
            subType = SYSTEM_USER_UPDATE_PASSWORD_SUB_TYPE,
            bizNo = "{{#id}}",
            success = SYSTEM_USER_UPDATE_PASSWORD_SUCCESS
    )
    public void updateUserPassword(Long id, String password) {
        // 1. 校验用户存在
        UserDO user = validateUserExists(id);

        // 2. 更新密码
        UserDO updateObj = new UserDO();
        updateObj.setId(id);
        updateObj.setPassword(encodePassword(password)); // 加密密码
        userMapper.updateById(updateObj);

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable("user", user);
        LogRecordContext.putVariable("newPassword", updateObj.getPassword());
    }

    @Override
    public void updateUserPassword(Long id, UserProfileUpdatePasswordReqVO reqVO) {
        // 校验旧密码密码
        validateOldPassword(id, reqVO.getOldPassword());
        // 执行更新
        UserDO updateObj = new UserDO().setId(id);
        updateObj.setPassword(encodePassword(reqVO.getNewPassword())); // 加密密码
        userMapper.updateById(updateObj);
    }

    /**
     * 校验旧密码
     * @param id          用户 id
     * @param oldPassword 旧密码
     */
    @VisibleForTesting
    void validateOldPassword(Long id, String oldPassword) {
        UserDO user = userMapper.selectById(id);
        if (user == null) {
            throw exception(USER_NOT_EXISTS);
        }
        if (!isPasswordMatch(oldPassword, user.getPassword())) {
            throw exception(USER_PASSWORD_FAILED);
        }
    }

    @Override
    public void updateUserStatus(Long id, Integer status) {
        // 校验用户存在
        validateUserExists(id);
        // 更新状态
        UserDO updateObj = new UserDO();
        updateObj.setId(id);
        updateObj.setStatus(status);
        userMapper.updateById(updateObj);
    }

    @Override
    public UserDO getUserByMobile(String mobile) {
        return userMapper.selectByMobile(mobile);
    }

    @VisibleForTesting
    UserDO validateUserExists(Long id) {
        if (id == null) {
            return null;
        }
        UserDO user = userMapper.selectById(id);
        if (user == null) {
            throw exception(USER_NOT_EXISTS);
        }
        return user;
    }

    /**
     * 对密码进行加密
     *
     * @param password 密码
     * @return 加密后的密码
     */
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
