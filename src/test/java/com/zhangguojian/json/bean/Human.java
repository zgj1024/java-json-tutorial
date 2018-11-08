package com.zhangguojian.json.bean;

import com.zhangguojian.json.JSONIgnore;
import com.zhangguojian.json.JSONSerialize;
import com.zhangguojian.json.serializer.DateSerializer;
import com.zhangguojian.json.serializer.TimestampSerializer;

import java.sql.Timestamp;
import java.util.Date;

public class Human implements Animals {

    Timestamp birthDate;

    Date deathDate;

    @Override
    public int getDeathAge() {
        return 100;
    }

    @JSONIgnore
    public int getNumberOfEye (){return 2;}

    public int getNumberOfEar() { return 2;}


    @JSONSerialize(using = TimestampSerializer.class)
    public Timestamp getBirthDate() {
        return birthDate;
    }


    public void setBirthDate(Timestamp birthDate) {
        this.birthDate = birthDate;
    }

    public Date getDeathDate() {
        return deathDate;
    }

    @JSONSerialize(using = DateSerializer.class)
    public void setDeathDate(Date deathDate) {
        this.deathDate = deathDate;
    }
}
