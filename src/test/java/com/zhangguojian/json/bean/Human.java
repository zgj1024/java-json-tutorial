package com.zhangguojian.json.bean;

import com.zhangguojian.json.JSONIgnore;

public class Human implements Animals {
    @Override
    public int getDeathAge() {
        return 100;
    }

    @JSONIgnore
    public int getNumberOfEye (){return 2;}

    public int getNumberOfEar() { return 2;}
}
