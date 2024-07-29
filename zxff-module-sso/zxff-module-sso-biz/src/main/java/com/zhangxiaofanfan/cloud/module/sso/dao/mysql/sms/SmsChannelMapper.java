package com.zhangxiaofanfan.cloud.module.sso.dao.mysql.sms;

import com.zhangxiaofanfan.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.zhangxiaofanfan.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.zhangxiaofanfan.cloud.module.common.pojo.PageResult;
import com.zhangxiaofanfan.cloud.module.sso.controller.sms.vo.channel.SmsChannelPageReqVO;
import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.sms.SmsChannelDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * TODO
 *
 * @author zhangxiaofanfan
 * @date 2024-07-03 11:13:08
 */
@Mapper
public interface SmsChannelMapper extends BaseMapperX<SmsChannelDO> {

    default PageResult<SmsChannelDO> selectPage(SmsChannelPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SmsChannelDO>()
                .likeIfPresent(SmsChannelDO::getSignature, reqVO.getSignature())
                .eqIfPresent(SmsChannelDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(SmsChannelDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(SmsChannelDO::getId));
    }

    default SmsChannelDO selectByCode(String code) {
        return selectOne(SmsChannelDO::getCode, code);
    }

}
