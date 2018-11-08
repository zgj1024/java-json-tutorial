package com.zhangguojian.json.serializer;

import com.zhangguojian.json.CustomSerializer;

import java.sql.Timestamp;

public class TimestampSerializer implements CustomSerializer<Timestamp,Long> {

    @Override
    public Long serializeValue(Timestamp input) {
        return input.getTime();
    }
}
