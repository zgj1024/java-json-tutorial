package com.zhangguojian.json.deserializer;

import com.zhangguojian.json.CustomDeserializer;
import com.zhangguojian.json.JSONPrimitive;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateDeserializer implements CustomDeserializer<JSONPrimitive, Date> {
    private final static DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Date deserialize(JSONPrimitive input) throws ParseException {
        return sdf.parse(input.getAsString());
    }
}
