package com.zhangxiaofanfan.cloud.framework.web.core.filter;

import cn.hutool.core.util.StrUtil;
import com.zhangxiaofanfan.cloud.framework.web.config.WebProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * TODO
 *
 * @author zhangxiaofanfan
 * @date 2024-07-03 17:40:09
 */
@RequiredArgsConstructor
public abstract class ApiRequestFilter extends OncePerRequestFilter {

    protected final WebProperties webProperties;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 只过滤 API 请求的地址
        return !StrUtil.startWithAny(request.getRequestURI(), webProperties.getAdminApi().getPrefix(),
                webProperties.getAppApi().getPrefix());
    }

}