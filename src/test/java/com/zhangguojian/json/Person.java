package com.zhangguojian.json;

import java.util.List;
import java.util.Queue;

public class Person {

    private String name;

    private Boolean ok;

    private int age;

    private List<Integer> array1;

    private int[] array2;

    private Queue queue;

    private Person son;

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

    public List<Integer> getArray1() {
        return array1;
    }

    public void setArray1(List<Integer> array1) {
        this.array1 = array1;
    }

    public int[] getArray2() {
        return array2;
    }

    public void setArray2(int[] array2) {
        this.array2 = array2;
    }

    public Queue getQueue() {
        return queue;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }


    public Person getSon() {
        return son;
    }

    public void setSon(Person son) {
        this.son = son;
    }


    public Boolean isOk() {
        return ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }
}
