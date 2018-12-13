package com.zhangguojian.json.bean;

import com.zhangguojian.json.JSONAlias;
import com.zhangguojian.json.JSONIgnore;

import java.util.Date;

public class User implements Person{

    private String name;

    private int age;

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public User() {
    }

    private Date birthday;

    private User father;

    private User mother;

    private String aliasName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @JSONIgnore
    public User getFather() {
        return father;
    }

    public void setFather(User father) {
        this.father = father;
    }

    public User getMother() {
        return mother;
    }

    public void setMother(User mother) {
        this.mother = mother;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @JSONAlias(name = "alias")
    public String getAliasName() {
        return aliasName;
    }

    @JSONAlias(name = "alias")
    public User setAliasName(String aliasName) {
        this.aliasName = aliasName;
        return this;
    }
}
