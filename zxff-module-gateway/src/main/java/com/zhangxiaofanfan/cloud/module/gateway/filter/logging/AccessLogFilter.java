package com.zhangxiaofanfan.cloud.module.gateway.filter.logging;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.common.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.apache.skywalking.apm.toolkit.webflux.WebFluxSkyWalkingOperators;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.annotation.Resource;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static cn.hutool.core.date.DatePattern.NORM_DATETIME_MS_FORMATTER;

/**
 * gateway 网关的访问时记录下请求日志
 *
 * @author zhangxiaofanfan
 * @date 2024-05-13 10:33:51
 */
@Slf4j
@Component
public class AccessLogFilter implements GlobalFilter, Ordered {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL); // 忽略 null 值
        OBJECT_MAPPER.registerModules(new JavaTimeModule()); // 解决 LocalDateTime 的序列化
    }

    private static final String LOGIN_USER_ID_ATTR = "login-user-id";
    private static final String LOGIN_USER_TYPE_ATTR = "login-user-type";

    @Resource
    private CodecConfigurer codecConfigurer;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 将 Request 中可以直接获取到的参数，设置到网关日志
        ServerHttpRequest request = exchange.getRequest();
        AccessLog gatewayLog = new AccessLog();

        gatewayLog.setTraceId(WebFluxSkyWalkingOperators.continueTracing(exchange, TraceContext::traceId));
        gatewayLog.setRoute(getGatewayRoute(exchange));
        gatewayLog.setSchema(request.getURI().getScheme());
        gatewayLog.setRequestMethod(request.getMethod().name());
        gatewayLog.setRequestUrl(request.getURI().getRawPath());
        gatewayLog.setQueryParams(request.getQueryParams());
        gatewayLog.setRequestHeaders(request.getHeaders());
        gatewayLog.setStartTime(LocalDateTime.now());
        gatewayLog.setUserIp(getClientIP(exchange));

        // 继续 filter 过滤
        MediaType mediaType = request.getHeaders().getContentType();
        if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(mediaType) ||
                MediaType.APPLICATION_JSON.isCompatibleWith(mediaType)) {
            return filterWithRequestBody(exchange, chain, gatewayLog);
        }
        return filterWithoutRequestBody(exchange, chain, gatewayLog);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    /**
     * 获得请求匹配的 Route 路由
     *
     * @param exchange 请求
     * @return 路由
     */
    public Route getGatewayRoute(ServerWebExchange exchange) {
        return exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
    }

    /**
     * 获得客户端 IP
     *
     * @param exchange         请求
     * @param otherHeaderNames 其它 header 名字的数组
     * @return 客户端 IP
     */
    public String getClientIP(ServerWebExchange exchange, String... otherHeaderNames) {
        String[] headers = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };
        if (ArrayUtil.isNotEmpty(otherHeaderNames)) {
            headers = ArrayUtil.addAll(headers, otherHeaderNames);
        }
        // 方式一，通过 header 获取
        String ip;
        for (String header : headers) {
            ip = exchange.getRequest().getHeaders().getFirst(header);
            if (!NetUtil.isUnknown(ip)) {
                return NetUtil.getMultistageReverseProxyIp(ip);
            }
        }

        // 方式二，通过 remoteAddress 获取
        if (exchange.getRequest().getRemoteAddress() == null) {
            return null;
        }
        ip = exchange.getRequest().getRemoteAddress().getHostString();
        return NetUtil.getMultistageReverseProxyIp(ip);
    }


    private Mono<Void> filterWithoutRequestBody(
            ServerWebExchange exchange,
            GatewayFilterChain chain,
            AccessLog accessLog
    ) {
        // 包装 Response，用于记录 Response Body
        ServerHttpResponseDecorator decoratedResponse = recordResponseLog(exchange, accessLog);
        return chain
                .filter(exchange.mutate().response(decoratedResponse).build())
                .then(Mono.fromRunnable(() -> writeAccessLog(accessLog)));
    }

    /**
     * 记录响应日志
     * 通过 DataBufferFactory 解决响应体分段传输问题。
     */
    private ServerHttpResponseDecorator recordResponseLog(ServerWebExchange exchange, AccessLog gatewayLog) {
        ServerHttpResponse response = exchange.getResponse();
        return new ServerHttpResponseDecorator(response) {

            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    DataBufferFactory bufferFactory = response.bufferFactory();
                    // 计算执行时间
                    gatewayLog.setEndTime(LocalDateTime.now());

                    gatewayLog.setDuration((int) (LocalDateTimeUtil.between(gatewayLog.getStartTime(),
                            gatewayLog.getEndTime()).toMillis()));
                    // 设置其它字段
                    gatewayLog.setUserId(getLoginUserId(exchange));
                    gatewayLog.setUserType(getLoginUserType(exchange));
                    gatewayLog.setResponseHeaders(response.getHeaders());
                    gatewayLog.setHttpStatus((HttpStatus) response.getStatusCode());

                    // 获取响应类型，如果是 json 就打印
                    String originalResponseContentType = exchange.getAttribute(
                            ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR
                    );
                    if (StringUtils.isNotBlank(originalResponseContentType)
                            && originalResponseContentType.contains("application/json")) {
                        Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                        return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                            // 设置 response body 到网关日志
                            byte[] content = readContent(dataBuffers);
                            String responseResult = new String(content, StandardCharsets.UTF_8);
                            gatewayLog.setResponseBody(responseResult);

                            // 响应
                            return bufferFactory.wrap(content);
                        }));
                    }
                }
                // if body is not a flux. never got there.
                return super.writeWith(body);
            }
        };
    }

    /**
     * 打印日志
     *
     * @param gatewayLog 网关日志
     */
    @SneakyThrows
    private void writeAccessLog(AccessLog gatewayLog) {
        // 方式一：打印 Logger 后，通过 ELK 进行收集
//         log.info("[writeAccessLog][日志内容：{}]", JSONUtil.toJsonStr(gatewayLog));

        // 方式二：调用远程服务，记录到数据库中
        // TODO 芋艿：暂未实现

        // 方式三：打印到控制台，方便排查错误
        Map<String, Object> values = MapUtil.newHashMap(16, true); // 手工拼接，保证排序；15 保证不用扩容
        values.put("traceId", gatewayLog.getTraceId());
        values.put("userId", gatewayLog.getUserId());
        values.put("userType", gatewayLog.getUserType());
        values.put("routeId", gatewayLog.getRoute() != null ? gatewayLog.getRoute().getId() : null);
        values.put("schema", gatewayLog.getSchema());
        values.put("requestUrl", gatewayLog.getRequestUrl());
        values.put("queryParams", gatewayLog.getQueryParams().toSingleValueMap());
        values.put("requestBody", JSONUtil.isTypeJSON(gatewayLog.getRequestBody()) ? // 保证 body 的展示好看
                JSONUtil.parse(gatewayLog.getRequestBody()) : gatewayLog.getRequestBody());
        values.put("requestHeaders", OBJECT_MAPPER.writeValueAsString(
                gatewayLog.getRequestHeaders().toSingleValueMap())
        );
        values.put("userIp", gatewayLog.getUserIp());
        values.put("responseBody", JSONUtil.isTypeJSON(gatewayLog.getResponseBody()) ? // 保证 body 的展示好看
                JSONUtil.parse(gatewayLog.getResponseBody()) : gatewayLog.getResponseBody());
        values.put("responseHeaders", gatewayLog.getResponseHeaders() != null ?
                OBJECT_MAPPER.writeValueAsString(gatewayLog.getResponseHeaders().toSingleValueMap()) : null);
        values.put("httpStatus", gatewayLog.getHttpStatus());
        values.put("startTime", LocalDateTimeUtil.format(gatewayLog.getStartTime(), NORM_DATETIME_MS_FORMATTER));
        values.put("endTime", LocalDateTimeUtil.format(gatewayLog.getEndTime(), NORM_DATETIME_MS_FORMATTER));
        values.put("duration", gatewayLog.getDuration() != null ? gatewayLog.getDuration() + " ms" : null);
        log.info("[writeAccessLog][网关日志：{}]",
                OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(values)
        );
    }


    /**
     * 参考 {@link ModifyRequestBodyGatewayFilterFactory} 实现
     * <p>
     * 差别主要在于使用 modifiedBody 来读取 Request Body 数据
     */
    private Mono<Void> filterWithRequestBody(
            ServerWebExchange exchange,
            GatewayFilterChain chain,
            AccessLog gatewayLog
    ) {
        // 设置 Request Body 读取时，设置到网关日志
        // 此处 codecConfigurer.getReaders() 的目的，是解决 spring.codec.max-in-memory-size 不生效
        ServerRequest serverRequest = ServerRequest.create(exchange, codecConfigurer.getReaders());
        Mono<String> modifiedBody = serverRequest.bodyToMono(String.class).flatMap(body -> {
            gatewayLog.setRequestBody(body);
            return Mono.just(body);
        });

        // 创建 BodyInserter 对象
        BodyInserter<Mono<String>, ReactiveHttpOutputMessage> bodyInserter =
                BodyInserters.fromPublisher(modifiedBody, String.class);
        // 创建 CachedBodyOutputMessage 对象
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(exchange.getRequest().getHeaders());
        // the new content type will be computed by bodyInserter
        // and then set in the request decorator
        headers.remove(HttpHeaders.CONTENT_LENGTH); // 移除
        CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
        // 通过 BodyInserter 将 Request Body 写入到 CachedBodyOutputMessage 中
        return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {
            // 包装 Request，用于缓存 Request Body
            ServerHttpRequest decoratedRequest = requestDecorate(exchange, headers, outputMessage);
            // 包装 Response，用于记录 Response Body
            ServerHttpResponseDecorator decoratedResponse = recordResponseLog(exchange, gatewayLog);
            // 记录普通的
            return chain
                    .filter(exchange.mutate().request(decoratedRequest).response(decoratedResponse).build())
                    .then(Mono.fromRunnable(() -> writeAccessLog(gatewayLog)));
        }));
    }

    /**
     * 请求装饰器，支持重新计算 headers、body 缓存
     *
     * @param exchange      请求
     * @param headers       请求头
     * @param outputMessage body 缓存
     * @return 请求装饰器
     */
    private ServerHttpRequestDecorator requestDecorate(
            ServerWebExchange exchange,
            HttpHeaders headers,
            CachedBodyOutputMessage outputMessage
    ) {
        return new ServerHttpRequestDecorator(exchange.getRequest()) {

            @Override
            public HttpHeaders getHeaders() {
                long contentLength = headers.getContentLength();
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.putAll(super.getHeaders());
                if (contentLength > 0) {
                    httpHeaders.setContentLength(contentLength);
                } else {
                    // TODO: this causes a 'HTTP/1.1 411 Length Required' // on
                    // httpbin.org
                    httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                }
                return httpHeaders;
            }

            @Override
            public Flux<DataBuffer> getBody() {
                return outputMessage.getBody();
            }
        };
    }


    /**
     * 获得登录用户的编号
     *
     * @param exchange 请求
     * @return 用户编号
     */
    public static Long getLoginUserId(ServerWebExchange exchange) {
        return MapUtil.getLong(exchange.getAttributes(), LOGIN_USER_ID_ATTR);
    }

    /**
     * 获得登录用户的类型
     *
     * @param exchange 请求
     * @return 用户类型
     */
    public static Integer getLoginUserType(ServerWebExchange exchange) {
        return MapUtil.getInt(exchange.getAttributes(), LOGIN_USER_TYPE_ATTR);
    }

    private byte[] readContent(List<? extends DataBuffer> dataBuffers) {
        // 合并多个流集合，解决返回体分段传输
        DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
        DataBuffer join = dataBufferFactory.join(dataBuffers);
        byte[] content = new byte[join.readableByteCount()];
        join.read(content);
        // 释放掉内存
        DataBufferUtils.release(join);
        return content;
    }

}
