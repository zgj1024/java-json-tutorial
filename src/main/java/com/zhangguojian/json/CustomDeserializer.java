package com.zhangguojian.json;

import java.text.ParseException;

public interface CustomDeserializer <Source,Target> {
    Target CustomDeserializer(Source input) throws ParseException;
}
