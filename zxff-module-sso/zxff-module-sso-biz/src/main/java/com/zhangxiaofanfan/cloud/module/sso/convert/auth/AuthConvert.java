package com.zhangxiaofanfan.cloud.module.sso.convert.auth;

import cn.hutool.core.collection.CollUtil;
import com.zhangxiaofanfan.cloud.module.common.util.object.BeanUtils;
import com.zhangxiaofanfan.cloud.module.sso.api.sms.dto.code.SmsCodeSendReqDTO;
import com.zhangxiaofanfan.cloud.module.sso.api.sms.dto.code.SmsCodeUseReqDTO;
import com.zhangxiaofanfan.cloud.module.sso.api.social.dto.SocialUserBindReqDTO;
import com.zhangxiaofanfan.cloud.module.sso.controller.auth.vo.request.AuthSmsLoginReqVO;
import com.zhangxiaofanfan.cloud.module.sso.controller.auth.vo.request.AuthSmsSendReqVO;
import com.zhangxiaofanfan.cloud.module.sso.controller.auth.vo.request.AuthSocialLoginReqVO;
import com.zhangxiaofanfan.cloud.module.sso.controller.auth.vo.response.AuthPermissionInfoRespVO;
import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.permission.MenuDO;
import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.permission.RoleDO;
import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.user.UserDO;
import com.zhangxiaofanfan.cloud.module.sso.enums.permission.MenuTypeEnum;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.zhangxiaofanfan.cloud.module.sso.controller.auth.vo.response.AuthLoginRespVO;
import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.oauth2.OAuth2AccessTokenDO;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.zhangxiaofanfan.cloud.module.common.util.collection.CollectionUtils.convertSet;
import static com.zhangxiaofanfan.cloud.module.common.util.collection.CollectionUtils.filterList;
import static com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.permission.MenuDO.ID_ROOT;

@Mapper
public interface AuthConvert {

    AuthConvert INSTANCE = Mappers.getMapper(AuthConvert.class);

    AuthLoginRespVO convert(OAuth2AccessTokenDO bean);

    default AuthPermissionInfoRespVO convert(UserDO user, List<RoleDO> roleList, List<MenuDO> menuList) {
        return AuthPermissionInfoRespVO.builder()
                .user(BeanUtils.toBean(user, AuthPermissionInfoRespVO.UserVO.class))
                .roles(convertSet(roleList, RoleDO::getCode))
                // 权限标识信息
                .permissions(convertSet(menuList, MenuDO::getPermission))
                // 菜单树
                .menus(buildMenuTree(menuList))
                .build();
    }

    SocialUserBindReqDTO convert(Long userId, Integer userType, AuthSocialLoginReqVO reqVO);

    SmsCodeSendReqDTO convert(AuthSmsSendReqVO reqVO);

    SmsCodeUseReqDTO convert(AuthSmsLoginReqVO reqVO, Integer scene, String usedIp);


    AuthPermissionInfoRespVO.MenuVO convertTreeNode(MenuDO menu);


    /**
     * 将菜单列表，构建成菜单树
     *
     * @param menuList 菜单列表
     * @return 菜单树
     */
    default List<AuthPermissionInfoRespVO.MenuVO> buildMenuTree(List<MenuDO> menuList) {
        if (CollUtil.isEmpty(menuList)) {
            return Collections.emptyList();
        }
        // 移除按钮
        menuList.removeIf(menu -> menu.getType().equals(MenuTypeEnum.BUTTON.getType()));
        // 排序，保证菜单的有序性
        menuList.sort(Comparator.comparing(MenuDO::getSort));

        // 构建菜单树
        // 使用 LinkedHashMap 的原因，是为了排序 。实际也可以用 Stream API ，就是太丑了。
        Map<Long, AuthPermissionInfoRespVO.MenuVO> treeNodeMap = new LinkedHashMap<>();
        menuList.forEach(menu -> treeNodeMap.put(menu.getId(), AuthConvert.INSTANCE.convertTreeNode(menu)));
        // 处理父子关系
        treeNodeMap.values().stream().filter(node -> !node.getParentId().equals(ID_ROOT)).forEach(childNode -> {
            // 获得父节点
            AuthPermissionInfoRespVO.MenuVO parentNode = treeNodeMap.get(childNode.getParentId());
            if (parentNode == null) {
                LoggerFactory.getLogger(getClass()).error("[buildRouterTree][resource({}) 找不到父资源({})]",
                        childNode.getId(), childNode.getParentId());
                return;
            }
            // 将自己添加到父节点中
            if (parentNode.getChildren() == null) {
                parentNode.setChildren(new ArrayList<>());
            }
            parentNode.getChildren().add(childNode);
        });
        // 获得到所有的根节点
        return filterList(treeNodeMap.values(), node -> ID_ROOT.equals(node.getParentId()));
    }
}
