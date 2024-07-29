package com.zhangxiaofanfan.cloud.module.sso.service.sms;

import com.zhangxiaofanfan.cloud.module.common.pojo.PageResult;
import com.zhangxiaofanfan.cloud.module.sso.controller.sms.vo.channel.SmsChannelPageReqVO;
import com.zhangxiaofanfan.cloud.module.sso.controller.sms.vo.channel.SmsChannelSaveReqVO;
import com.zhangxiaofanfan.cloud.module.sso.dao.dataobject.sms.SmsChannelDO;
import com.zhangxiaofanfan.cloud.module.sso.framework.sms.core.client.SmsClient;
import jakarta.validation.Valid;

import java.util.List;

/**
 * TODO
 *
 * @author zhangxiaofanfan
 * @date 2024-07-03 11:07:58
 */
public interface SmsChannelService {

    /**
     * 创建短信渠道
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSmsChannel(@Valid SmsChannelSaveReqVO createReqVO);

    /**
     * 更新短信渠道
     *
     * @param updateReqVO 更新信息
     */
    void updateSmsChannel(@Valid SmsChannelSaveReqVO updateReqVO);

    /**
     * 删除短信渠道
     *
     * @param id 编号
     */
    void deleteSmsChannel(Long id);

    /**
     * 获得短信渠道
     *
     * @param id 编号
     * @return 短信渠道
     */
    SmsChannelDO getSmsChannel(Long id);

    /**
     * 获得所有短信渠道列表
     *
     * @return 短信渠道列表
     */
    List<SmsChannelDO> getSmsChannelList();

    /**
     * 获得短信渠道分页
     *
     * @param pageReqVO 分页查询
     * @return 短信渠道分页
     */
    PageResult<SmsChannelDO> getSmsChannelPage(SmsChannelPageReqVO pageReqVO);

    /**
     * 获得短信客户端
     *
     * @param id 编号
     * @return 短信客户端
     */
    SmsClient getSmsClient(Long id);

    /**
     * 获得短信客户端
     *
     * @param code 编码
     * @return 短信客户端
     */
    SmsClient getSmsClient(String code);

}
