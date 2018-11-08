package com.zhangguojian.json.deserializer;

import com.zhangguojian.json.CustomDeserializer;

import java.sql.Timestamp;
import java.text.ParseException;


public class TimestampDeserializer implements CustomDeserializer<String,Timestamp> {

    @Override
    public Timestamp CustomDeserializer(String input) throws ParseException {
        return Timestamp.valueOf(input);
    }
}
