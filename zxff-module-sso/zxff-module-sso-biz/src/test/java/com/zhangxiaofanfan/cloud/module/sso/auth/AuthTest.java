package com.zhangxiaofanfan.cloud.module.sso.auth;

import com.zhangxiaofanfan.cloud.module.common.dto.logger.request.ApiErrorLogCreateReqDTO;
import com.zhangxiaofanfan.cloud.module.common.pojo.CommonResult;
import com.zhangxiaofanfan.cloud.module.sso.SsoModuleApplication;
import com.zhangxiaofanfan.cloud.module.sso.api.logger.ApiErrorLogApi;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 认证 controller 测试类
 *
 * @author zhangxiaofanfan
 * @date 2024-07-11 10:58:38
 */
@Slf4j
@SpringBootTest(classes = SsoModuleApplication.class)
public class AuthTest {
    @Resource
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ApiErrorLogApi apiErrorLogApi;

    @Test
    public void encodePassword() {
        String password = "zhangxiaofanfan";
        log.info("encode pwd is {}", passwordEncoder.encode(password));
    }

    @Test
    public void apiErrorLogApiTest() {
        CommonResult<Boolean> apiErrorLog = apiErrorLogApi.createApiErrorLog(
                ApiErrorLogCreateReqDTO
                        .builder()
                        .traceId("traceId")
                        .userId(10086L)
                        .userType(1)
                        .applicationName("zxff-sso-server")
                        .requestMethod("GET")
                        .requestUrl("127.0.0.1")
                        .requestParams("")
                        .userIp("127.0.0.1")
                        .userAgent("userAgent")
                        .exceptionTime(LocalDateTime.now())
                        .exceptionName("exceptionName")
                        .exceptionClassName("exceptionClassName")
                        .exceptionFileName("exceptionFileName")
                        .exceptionMethodName("exceptionMethodName")
                        .exceptionLineNumber(10)
                        .exceptionStackTrace("exceptionStackTrace")
                        .exceptionRootCauseMessage("exceptionRootCauseMessage")
                        .exceptionMessage("exceptionMessage")
                        .build()
        );
        log.info("result is {}", apiErrorLog.getMsg());
    }
}
