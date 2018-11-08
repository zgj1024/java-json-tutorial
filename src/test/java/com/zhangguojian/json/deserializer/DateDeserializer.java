package com.zhangguojian.json.deserializer;

import com.zhangguojian.json.CustomDeserializer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateDeserializer implements CustomDeserializer<String,Date> {
    private  static DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Date CustomDeserializer(String input) throws ParseException {
        return sdf.parse(input);
    }
}
