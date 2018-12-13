package com.zhangguojian.json.serializer;

import com.zhangguojian.json.CustomSerializer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateSerializer implements CustomSerializer<Date,String> {
    private final static DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public String serializeValue(Date input) throws ParseException {
        return sdf.format(input);
    }
}