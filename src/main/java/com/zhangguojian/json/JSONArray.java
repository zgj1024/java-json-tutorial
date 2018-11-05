package com.zhangguojian.json;

import com.zhangguojian.json.exception.CastException;
import com.zhangguojian.json.exception.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class JSONArray<E> extends ArrayList<E> {
    public static JSONArray<Object> EMPTY = new JSONArray<>();

    public JSONArray(int initialCapacity) {
        super(initialCapacity);
    }

    public JSONArray() {
    }

    public static <T> JSONArray<T> fromObject(T[] objects) {
        if (objects == null) {
            return new JSONArray<>();
        }
        JSONArray<T> array = new JSONArray<>(objects.length);
        array.addAll(Arrays.asList(objects));
        return array;
    }

    public static <T> JSONArray<T> fromObject(Collection<T> c)  {
        if (c == null || c.isEmpty()) {
            return  new JSONArray<>();
        } else {
            JSONArray<T> array = new JSONArray<>(c.size());
            array.addAll(c);
            return array;
        }
    }

    public static JSONArray<Object> fromObject(Object o) throws CastException {
        if(o==null){
            throw new CastException("null can not cast to array");
        }
        if(!o.getClass().isArray())
            throw new CastException(o.toString() + " can not cast to array");

        JSONArray<Object> array = new JSONArray<>();
        int length = Array.getLength(o);
        array.ensureCapacity(length);
        for (int i = 0; i < length; i += 1) {
            array.add(JSON.wrap(Array.get(o, i)));
        }
        return array;
    }

    public static <T> JSONArray<T> asList(T ...args){
        JSONArray<T> jsonArray = new JSONArray<>();
        jsonArray.addAll(Arrays.asList(args));
        return jsonArray;
    }

    @Override
    public String toString() {
        return JSON.stringify(this);
    }
}
