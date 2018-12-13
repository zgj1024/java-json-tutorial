package com.zhangguojian.json;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class StringifyUtils {

    public static String Stringify(Object object) {
        if (object == null) {
            return "null";
        } else if (object.getClass() == String.class) {
            String value = (String) object;
            return Stringify(value);
        }else if(object instanceof JSONElement){
            return ((JSONElement) object).stringify();
        } else if (object instanceof Number) {
            return Stringify((Number) object);
        } else if (object instanceof Boolean) {
            return Stringify((Boolean) object);
        } else  if(object instanceof Character){
            return object.toString();
        }else if(object instanceof Object[]){
            return Stringify((Object[]) object);
        }else if(object.getClass().isArray()){
            return StringifyWithObjectArray(object);
        }else if(object instanceof Collection){
            return Stringify((Collection)object);
        }else if(object instanceof Map){
            return Stringify((Map)object);
        }
        return StringifyObject(object);
    }

    public static String Stringify(Number number) {
        if (number == null) {
            return "null";
        } else if (number instanceof Double) {
            double d = number.doubleValue();
            if (Double.isNaN(d) || Double.isInfinite(d)) {
                throw new IllegalArgumentException("Numeric values must be finite, but was " + d);

            }
        } else if (number instanceof Float) {
            float f = number.floatValue();
            if (Float.isNaN(f) || Float.isInfinite(f)) {
                throw new IllegalArgumentException("Numeric values must be finite, but was " + f);
            }
        }
        return number.toString();
    }

    public static String Stringify(Boolean bool) {
        if (bool == null) {
            return "null";
        }
        return bool ? "true" : "false";
    }

    public static String Stringify(String str) {
        if (str == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < str.length(); i++) {
            Character character = str.charAt(i);
            switch (character) {
                case '\t':
                    sb.append("\\t");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                case '\ufeff':
                    break;
                default:
                    if ((character >= '\u0080' && character < '\u00a0')
                            || (character >= '\u2000' && character < '\u2100')) {
                        String h = Integer.toHexString(character);
                        sb.append("\\u");
                        for (int j = 0; j < 4 - h.length(); j++) {
                            sb.append("0");
                        }
                        sb.append(h);
                    } else {
                        sb.append(character);
                    }
                    break;
            }
        }
        sb.append("\"");
        return sb.toString();
    }

    public static String Stringify(Object[] objects){
        if(objects == null){
            return "null";
        } else if(objects.length == 0){
            return "[]";
        }else {
            StringBuilder sb = new StringBuilder("[");
            for (Object object : objects) {
                sb.append(Stringify(object)).append(',');
            }
            return sb.replace(sb.length() - 1, sb.length(), "]").toString();
        }
    }

    private static String StringifyWithObjectArray(Object obj){
        assert obj.getClass().isArray();
        if (Array.getLength(obj) == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < Array.getLength(obj); i++) {
            sb.append(Array.get(obj, i)).append(",");
        }
        return sb.replace(sb.length() - 1, sb.length(), "]").toString();
    }

    public static String Stringify(Collection collection) {
        if(collection == null){
            return "null";
        }else if(collection.isEmpty()){
            return "[]";
        }
        StringBuilder sb=  new StringBuilder("[");
        for(Object obj:collection){
            sb.append(Stringify(obj)).append(',');
        }

        return sb.replace(sb.length() - 1, sb.length(), "]").toString();
    }

    public static String Stringify(Map<?, ?> map) {
        if (map == null) {
            return "null";
        } else if (map.isEmpty()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder("{");
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry.getKey() == null) {
                sb.append("null");
            } else {
                sb.append(Stringify(entry.getKey().toString()));
            }

            sb.append(":").append(Stringify(entry.getValue())).append(',');
        }
        sb.replace(sb.length() - 1, sb.length(), "}");
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private static String StringifyObject(Object object){
        assert object != null;
        //普通的 Object 对象
        try {
            StringBuilder sb = new StringBuilder("{");
            List<Method> methodList = ReflectUtils.getMethods(object);
            for (Method method : methodList) {
                String key = ReflectUtils.getKeyNameFromGetMethod(method);
                if (key != null && !key.isEmpty()) {
                    Object result = method.invoke(object);
                    if (result == null) {
                        continue;
                    }
                    //如果有 JSONSerialize 的注解，最终得到会是已经被处理过的结果。
                    JSONSerialize jsonSerialize = ReflectUtils.getAnnotation(method, JSONSerialize.class);
                    if (jsonSerialize != null) {
                        //获取注解中的using 类
                        String className = jsonSerialize.using().getName();
                        //get class Name
                        //获取 using 中的 泛型类。
                        CustomSerializer serializer = (CustomSerializer) Class.forName(className).newInstance();
                        result = serializer.serializeValue(result);
                    }

                    JSONAlias jsonAlias = ReflectUtils.getAnnotation(method, JSONAlias.class);
                    if(jsonAlias !=null){
                        key = jsonAlias.name();
                    }

                    sb.append(Stringify(key)).append(":").append(Stringify(result)).append(",");
                }
            }
            return sb.replace(sb.length() - 1, sb.length(), "}").toString();
        } catch (IllegalAccessException | InvocationTargetException | ClassNotFoundException | InstantiationException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
