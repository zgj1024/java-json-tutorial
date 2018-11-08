package com.zhangguojian.json;

import java.text.ParseException;

public interface CustomDeserializer <Source,Target> {
    Target deserialize(Source input) throws ParseException;
}
