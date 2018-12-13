package com.zhangguojian.json;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class JSONObject implements JSONElement{

    private final TreeMap<String, JSONElement> members =
            new TreeMap<>();

    public static final JSONObject EMPTY = new JSONObject();

    public TreeMap<String, JSONElement> getMembers() {
        return members;
    }

    @Override
    public boolean isJSONObject() {
        return true;
    }

    @Override
    public JSONObject getAsJSONObject() {
        return this;
    }

    @Override
    public String stringify() {
        return StringifyUtils.Stringify(members);
    }

    public static JSONObject of(Map<?,?> map){
        JSONObject jsonObject = new JSONObject();
        for(Map.Entry<?,?> element:map.entrySet()){
            jsonObject.add(element.getKey().toString(), JSONElement.of(element.getValue()));
        }
        return jsonObject;
    }

    public static JSONObject of(Object bean){
        if(bean instanceof Map){
            return of((Map)bean);
        }
        JSONObject jsonObject = new JSONObject();
        List<Method> methodList = ReflectUtils.getMethods(bean);
        for (Method method : methodList) {
            final String key = ReflectUtils.getKeyNameFromGetMethod(method);
            if (key != null && !key.isEmpty()) {
                try {
                    final Object result = method.invoke(bean);
                    if (result != null) {
                        jsonObject.add(key, JSONElement.of(result));
                        // we don't use the result anywhere outside asList wrap
                        // if it's a resource we should be sure to close it
                        // after calling toString
                        if (result instanceof Closeable) {
                            try {
                                ((Closeable) result).close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonObject;
    }


    @Override
    public JSONObject deepCopy() {
        JSONObject result = new JSONObject();
        for (Map.Entry<String, JSONElement> entry : members.entrySet()) {
            result.add(entry.getKey(), entry.getValue().deepCopy());
        }
        return result;
    }

    public void add(String property, JSONElement value) {
        if (value == null) {
            members.put(property, JSONNull.INSTANCE);
        }else {
            members.put(property, value);
        }
    }

    public JSONElement remove(String property) {
        return members.remove(property);
    }

    public void addProperty(String property, String value) {
        add(property, value==null?JSONNull.INSTANCE:JSONPrimitive.of(value));
    }

    public void addProperty(String property, Number value) {
        add(property, value==null?JSONNull.INSTANCE:JSONPrimitive.of(value));
    }

    public void addProperty(String property, Boolean value) {
        add(property, value==null?JSONNull.INSTANCE:JSONPrimitive.of(value));
    }

    public void addProperty(String property, Character value) {
        add(property, value==null?JSONNull.INSTANCE:JSONPrimitive.of(value));
    }

    public Set<Map.Entry<String, JSONElement>> entrySet() {
        return members.entrySet();
    }

    public Set<String> keySet() {
        return members.keySet();
    }

    public int size() {
        return members.size();
    }

    public boolean has(String memberName) {
        return members.containsKey(memberName);
    }

    public JSONElement get(String memberName) {
        return members.get(memberName);
    }

    public JSONElement getAsJsonPrimitive(String memberName) {
        return members.get(memberName);
    }

    public JSONArray getAsJsonArray(String memberName) {
        return (JSONArray) members.get(memberName);
    }

    public JSONObject getAsJsonObject(String memberName) {
        return (JSONObject) members.get(memberName);
    }

    @Override
    public boolean equals(Object o) {
        return (o == this) || (o instanceof JSONObject && ((JSONObject) o).members.equals(members));
    }

    @Override
    public int hashCode() {
        return members.hashCode();
    }
}
