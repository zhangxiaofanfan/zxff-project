package com.zhangxiaofanfan.cloud.framework.tracer.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO
 *
 * @author zhangxiaofanfan
 * @date 2024-07-04 16:44:02
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface BizTrace {

    /**
     * 业务编号 tag 名
     */
    String ID_TAG = "biz.id";
    /**
     * 业务类型 tag 名
     */
    String TYPE_TAG = "biz.type";

    /**
     * @return 操作名
     */
    String operationName() default "";

    /**
     * @return 业务编号
     */
    String id();

    /**
     * @return 业务类型
     */
    String type();

}
