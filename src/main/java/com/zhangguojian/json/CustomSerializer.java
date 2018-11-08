package com.zhangguojian.json;

import java.text.ParseException;

/**
 * 自定义序列化基本，比如 DataTime 类型，希望序列化成 String
 */
public interface CustomSerializer<Source,Target> {
    Target serializeValue(Source input) throws ParseException;
}
