package com.zhangguojian.json;

import com.zhangguojian.json.exception.CastException;
import com.zhangguojian.json.exception.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class JSONArray<E> extends ArrayList<E> {
    public static JSONArray EMPTY = new JSONArray();

    public JSONArray(int initialCapacity) {
        super(initialCapacity);
    }

    public JSONArray() {
    }

    public static JSONArray fromObject(Object[] objects) {
        if (objects == null) {
            return JSONArray.EMPTY;
        }
        JSONArray array = new JSONArray(objects.length);
        array.addAll(Arrays.asList(objects));
        return array;
    }

    public static JSONArray<?> fromObject(Collection<?> c)  {
        if (c == null || c.isEmpty()) {
            return JSONArray.EMPTY;
        } else {
            JSONArray array = new JSONArray(c.size());
            array.addAll(c);

            return array;
        }
    }

    public static JSONArray<?> fromObject(Object o) throws CastException {
        if(o==null){
            throw new CastException("null can not cast to array");
        }
        if(!o.getClass().isArray())
            throw new CastException(o.toString() + " can not cast to array");

        JSONArray array = new JSONArray();
        int length = Array.getLength(o);
        array.ensureCapacity(length);
        for (int i = 0; i < length; i += 1) {
            array.add(JSONObject.wrap(Array.get(array, i)));
        }
        return array;
    }


    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (E o : this) {
            try {
                sb.append(JSON.stringify(o)).append(",");
            } catch (JSONException e) {
                e.printStackTrace();
                return "";
            }
        }
        sb.replace(sb.length() - 1, sb.length(), "]");
        return sb.toString();
    }
}
