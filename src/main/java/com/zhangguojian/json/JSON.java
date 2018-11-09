package com.zhangguojian.json;

import com.zhangguojian.json.exception.JSONException;

public class JSON {
    public static Object parse(String input) throws JSONException {
        return new Parser(input).parse();
    }

    public static String stringify(Object obj){
        //TODO
        return "";
    }
}