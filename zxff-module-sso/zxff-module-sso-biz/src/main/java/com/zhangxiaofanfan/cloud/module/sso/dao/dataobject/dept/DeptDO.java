package com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.dept;


import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhangxiaofanfan.cloud.framework.mybatis.core.dataobject.BaseDO;
import com.zhangxiaofanfan.cloud.module.common.enums.CommonStatusEnum;
import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.user.UserDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门表
 *
 * @date 2024-06-07 10:30:22
 * @author zhangxiaofanfan
 */
@TableName("system_dept")
@KeySequence("system_dept_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
public class DeptDO extends BaseDO {

    public static final Long PARENT_ID_ROOT = 0L;

    /**
     * 部门ID
     */
    @TableId
    private Long id;
    /**
     * 部门名称
     */
    private String name;
    /**
     * 父部门ID
     *
     * 关联 {@link #id}
     */
    private Long parentId;
    /**
     * 显示顺序
     */
    private Integer sort;
    /**
     * 负责人
     *
     * 关联 {@link UserDO#getId()}
     */
    private Long leaderUserId;
    /**
     * 联系电话
     */
    private String phone;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 部门状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;

}
