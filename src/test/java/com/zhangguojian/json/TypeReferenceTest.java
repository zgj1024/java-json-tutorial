package com.zhangguojian.json;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class TypeReferenceTest {
    @Test
    public void getType() throws Exception {

        Type type = new TypeReference<LinkedList<Integer>>(){}.getType();
        Type type3 = new TypeReference<LinkedList>(){}.getType();


        System.out.println(type3 instanceof ParameterizedType);
        System.out.println(type3 instanceof Class);

        Class<?> cc = (Class<?>) type3;
        Collection collection2 = (Collection) cc.newInstance();

        Class<?> cls= (Class<?>) ((ParameterizedType) type).getRawType();
//
        Collection collection = (Collection) cls.newInstance();
        Type type2 = ((ParameterizedType) type).getActualTypeArguments()[0];
//        collection.add(1);
//        collection.add(1);
        //System.out.println(new TypeReference<List<Integer>>(){}.getType());
    }

}