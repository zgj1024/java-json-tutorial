package com.zhangguojian.json;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TypeReferenceTest {

    @Test
    public void testConstruction(){
        TypeReference typeReference = new TypeReference<List<Integer>>(){};
        Assert.assertEquals("java.util.List<java.lang.Integer>",typeReference.getType().toString());

        typeReference = new TypeReference<int[]>(){};
        Assert.assertEquals(int[].class,typeReference.getType());

        typeReference = new TypeReference<Integer>(){};
        Assert.assertEquals(Integer.class,typeReference.getType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidConstruction(){
        TypeReference typeReference = new TypeReference(){};
    }

    /**
     * 没什么用的
     */
    @Test
    public void testCompare(){
        Assert.assertEquals(0,new TypeReference<List<Integer>>(){}.compareTo(null));
    }
}