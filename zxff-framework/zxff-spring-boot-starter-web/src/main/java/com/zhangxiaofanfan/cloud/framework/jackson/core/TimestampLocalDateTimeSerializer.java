package com.zhangxiaofanfan.cloud.framework.jackson.core;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * TODO
 *
 * @author zhangxiaofanfan
 * @date 2024-07-04 09:16:31
 */
public class TimestampLocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    public static final TimestampLocalDateTimeSerializer INSTANCE = new TimestampLocalDateTimeSerializer();

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // 将 LocalDateTime 对象，转换为 Long 时间戳
        gen.writeNumber(value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

}