package com.zhangxiaofanfan.cloud.framework.xss.core.clean;

/**
 * TODO
 *
 * @author zhangxiaofanfan
 * @date 2024-07-04 09:32:29
 */
public interface XssCleaner {

    /**
     * 清理有 Xss 风险的文本
     *
     * @param html 原 html
     * @return 清理后的 html
     */
    String clean(String html);

}
