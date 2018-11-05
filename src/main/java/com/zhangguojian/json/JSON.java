package com.zhangguojian.json;

import com.zhangguojian.json.exception.JSONException;

import java.util.Collection;
import java.util.Map;

public class JSON {

    public static JSONValue parse(String input) throws JSONException {
        return new Parser(input).parse();
    }

    public static String stringify(Object o) throws JSONException {
        if(o==null){return "null";}
        if(o instanceof String){
            return stringify((String)(o));
        }else if(o instanceof Number || o instanceof Boolean){
            return o.toString();
        }else if(o instanceof JSONArray){
            return stringify((JSONArray)(o));
        }else if(o instanceof Collection){
            return stringify(JSONArray.fromObject((Collection<?>) o));
        }else if(o instanceof Object[]){
            return stringify(JSONArray.fromObject((Object[]) o));
        }else if(o.getClass().isArray()){
            return stringify(JSONArray.fromObject(o));
        }else if(o instanceof JSONObject){
            return stringify((JSONObject)(o));
        }else if(o instanceof Map){
            Map<?, ?> map = (Map<?, ?>) o;
            return stringify(JSONObject.fromObject(map));
        }
        return stringify(JSONObject.fromObject(o));
    }

    public static String stringify(String str){
        if(str==null){return null;}
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < str.length(); i++) {
            Character character = str.charAt(i);
            switch (character){
                case '\t':
                    sb.append("\\t");break;
                case '\n':
                    sb.append("\\n");break;
                case '\r':
                    sb.append("\\r");break;
                case '\f':
                    sb.append("\\f");break;
                case '\b':
                    sb.append("\\b");break;
                case '"':
                    sb.append("\\\"");break;
                case '\\':
                    sb.append("\\\\");break;
                case '/':
                    sb.append("\\/");break;
                default:
                    sb.append(character);
                    break;
            }
        }
        sb.append("\"");
        return sb.toString();
    }

    public static String stringify(JSONObject jsonObject){
        if(jsonObject==null){return null;}

        return jsonObject.toString();
    }

    public static String stringify(JSONArray jsonArray){
        if(jsonArray==null){return null;}
        return jsonArray.toString();
    }
}
