package com.zhangguojian.json;

import com.zhangguojian.json.exception.JSONException;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;

public class JSON {

    public static JSONValue parse(String input) throws JSONException {
        return new Parser(input).parse();
    }

    public static String stringify(Object o) {
        if (o == null) {
            return "null";
        }
        if (o instanceof String) {
            return stringify((String) (o));
        } else if (o instanceof Number || o instanceof Boolean) {
            return o.toString();
        } else if (o instanceof Collection) {
            return stringify((Collection) o);
        } else if (o.getClass().isArray()) {
           return stringifyWithArrayObject(o);
        } else if (o instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) o;
            return stringify(map);
        }
        try {
            return JSONObject.fromObject(o).toString();
        }catch (Exception e){
            e.getMessage();
            return "";
        }
    }


    private static String stringify(String str) {
        if (str == null) {
            return null;
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


    private static String stringify(Collection collection) {
        if (collection == null) {
            return "";
        } else if (collection.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (Object obj : collection) {
            sb.append(JSON.stringify(wrap(obj))).append(",");
        }
        sb.replace(sb.length() - 1, sb.length(), "]");
        return sb.toString();
    }

    private static String stringifyWithArrayObject(Object obj) {
        if (Array.getLength(obj) == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < Array.getLength(obj); i++) {
            sb.append(Array.get(obj, i)).append(",");
        }
        return sb.replace(sb.length() - 1, sb.length(), "]").toString();
    }

    private static String stringify(Map<?, ?> map) {
        if (map == null) {
            return "";
        } else if (map.isEmpty()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder("{");
        for (Map.Entry entry : map.entrySet()) {
            sb.append(JSON.stringify(entry.getKey())).append(":").append(JSON.stringify(entry.getValue())).append(",");
        }
        sb.replace(sb.length() - 1, sb.length(), "}");
        return sb.toString();
    }


    public static Object wrap(Object object) {
        try {

            if (object instanceof JSONObject || object instanceof JSONArray

                    || object instanceof Byte || object instanceof Character
                    || object instanceof Short || object instanceof Integer
                    || object instanceof Long || object instanceof Boolean
                    || object instanceof Float || object instanceof Double
                    || object instanceof String || object instanceof BigInteger
                    || object instanceof BigDecimal || object instanceof Enum) {
                return object;
            }

            if (object instanceof Collection) {
                Collection<?> coll = (Collection<?>) object;
                return JSONArray.fromObject(coll);
            }
            if (object.getClass().isArray()) {
                return JSONArray.fromObject(object);
            }
            if (object instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) object;
                return JSONObject.fromObject(map);
            }

            return JSONObject.fromObject(object);
        } catch (Exception exception) {
            return null;
        }
    }

}
