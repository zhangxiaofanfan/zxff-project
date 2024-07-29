package com.zhangxiaofanfan.cloud.module.sso.service.user;

import com.zhangxiaofanfan.cloud.module.sso.controller.user.vo.prifile.request.UserProfileUpdatePasswordReqVO;
import com.zhangxiaofanfan.cloud.module.sso.controller.user.vo.user.request.UserSaveReqVO;
import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.user.UserDO;
import jakarta.validation.Valid;

/**
 * 后台用户 Service 接口
 *
 * @author zhangxiaofanfan
 */
public interface UserService {
    /**
     * 通过用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象信息
     */
    UserDO getUserByUsername(String username);

    /**
     * 判断密码是否匹配
     *
     * @param rawPassword 未加密的密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    boolean isPasswordMatch(String rawPassword, String encodedPassword);

    /**
     * 更新用户的最后登陆信息
     *
     * @param id 用户编号
     * @param loginIp 登陆 IP
     */
    void updateUserLogin(Long id, String loginIp);

    /**
     * 通过用户 ID 查询用户
     *
     * @param id 用户ID
     * @return 用户对象信息
     */
    UserDO getUser(Long id);

    /**
     * 创建用户
     *
     * @param createReqVO 用户信息
     * @return 用户编号
     */
    Long createUser(@Valid UserSaveReqVO createReqVO);

    /**
     * 修改用户
     *
     * @param updateReqVO 用户信息
     */
    void updateUser(@Valid UserSaveReqVO updateReqVO);

    /**
     * 删除用户
     *
     * @param id 用户编号
     */
    void deleteUser(Long id);

    /**
     * 修改密码
     *
     * @param id       用户编号
     * @param password 密码
     */
    void updateUserPassword(Long id, String password);

    /**
     * 修改用户个人密码
     *
     * @param id 用户编号
     * @param reqVO 更新用户个人密码
     */
    void updateUserPassword(Long id, @Valid UserProfileUpdatePasswordReqVO reqVO);

    /**
     * 修改状态
     *
     * @param id     用户编号
     * @param status 状态
     */
    void updateUserStatus(Long id, Integer status);

    /**
     * 通过手机号获取用户
     *
     * @param mobile 手机号
     * @return 用户对象信息
     */
    UserDO getUserByMobile(String mobile);


}
