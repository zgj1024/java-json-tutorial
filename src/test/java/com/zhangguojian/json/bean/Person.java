package com.zhangguojian.json.bean;

import com.zhangguojian.json.JSONDeserialize;
import com.zhangguojian.json.JSONSerialize;
import com.zhangguojian.json.deserializer.DateDeserializer;
import com.zhangguojian.json.serializer.DateSerializer;

import java.util.Date;

public interface Person {

    @JSONSerialize(using = DateSerializer.class)
    Date getBirthday();

    @JSONDeserialize(using = DateDeserializer.class)
    void setBirthday(Date birthday);
}
