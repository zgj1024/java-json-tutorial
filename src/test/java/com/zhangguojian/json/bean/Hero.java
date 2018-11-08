package com.zhangguojian.json.bean;


import com.zhangguojian.json.JSONDeserialize;
import com.zhangguojian.json.JSONSerialize;
import com.zhangguojian.json.deserializer.DateDeserializer;
import com.zhangguojian.json.deserializer.TimestampDeserializer;
import com.zhangguojian.json.serializer.DateSerializer;
import com.zhangguojian.json.serializer.TimestampSerializer;

import java.util.Date;
import java.sql.Timestamp;
import java.util.List;

public class Hero {
    private String name;
    private Timestamp birthDate;
    private Date deathDate;

    private List<Hero> heroList;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JSONSerialize(using = TimestampSerializer.class)
    public Timestamp getBirthDate() {
        return birthDate;
    }

    @JSONDeserialize(using = TimestampDeserializer.class)
    public void setBirthDate(Timestamp birthDate) {
        this.birthDate = birthDate;
    }

    @JSONSerialize(using = DateSerializer.class)
    public Date getDeathDate() {
        return deathDate;
    }

    @JSONDeserialize(using = DateDeserializer.class)
    public void setDeathDate(Date deathDate) {
        this.deathDate = deathDate;
    }

    public List<Hero> getHeroList() {
        return heroList;
    }

    public void setHeroList(List<Hero> heroList) {
        this.heroList = heroList;
    }
}
