package com.zhangguojian.json;

import com.zhangguojian.json.exception.JSONException;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;

public class JSON {
    public static Object parse(String input) throws JSONException {
        return new Parser(input).parse();
    }

    public static String stringify(Object obj) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if(obj == null){
            return "null";
        } else if(obj.getClass() == Boolean.class || obj instanceof Number){
            return obj.toString();
        }else if(obj instanceof String){
            return stringify((String)obj);
        }else if(obj instanceof Collection){
            return stringify((Collection)obj);
        }else if(obj instanceof Object[] || obj.getClass().isArray()){
            return stringifyArray(obj);
        }else if(obj instanceof Map){
            return stringify((Map)obj);
        }

        return stringify(ReflectUtils.ToMap(obj));
    }

    private static String stringify(String str){
        if(str == null){
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\"");

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
        return sb.append("\"").toString();
    }

    private static String stringify(Collection collection) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if(collection == null){
            return  "null";
        }
        StringBuilder sb = new StringBuilder("[");
        for (Object obj : collection){
            sb.append(JSON.stringify(obj)).append(",");
        }
        return sb.replace(sb.length()-1,sb.length(),"]").toString();

    }

    private static String stringifyArray(Object array) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if(array == null){
            return  "null";
        }
        StringBuilder sb = new StringBuilder("[");
        for(int i = 0 ; i < Array.getLength(array);i++){
            sb.append(JSON.stringify(Array.get(array,i))).append(",");
        }

        return sb.replace(sb.length()-1,sb.length(),"]").toString();

    }

    private static String stringify(Map<Object,Object> map) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (map == null) {
            return "null";
        } else if (map.isEmpty()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder("{");
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if(entry.getKey() instanceof String){
                sb.append(JSON.stringify(entry.getKey())).append(":").append(JSON.stringify(entry.getValue())).append(",");
            }
        }
        return sb.replace(sb.length() - 1, sb.length(), "}").toString();
    }
}
