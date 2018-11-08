package com.zhangguojian.json.deserializer;

import com.zhangguojian.json.CustomDeserializer;

import java.sql.Timestamp;
import java.text.ParseException;


public class TimestampDeserializer implements CustomDeserializer<Long,Timestamp> {

    @Override
    public Timestamp deserialize(Long input) throws ParseException {
        return new Timestamp(input);
    }
}
