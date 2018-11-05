package com.zhangguojian.json;

import com.zhangguojian.json.exception.CastException;

public final class JSONValue {

    private String strVal;
    private Boolean boolVal;
    private Number numVal;
    private JSONObject objVal;
    private JSONArray arrayVal;

    JSONValue(String strVal) {
        this.strVal = strVal;
    }

    private JSONValue(Boolean boolVal) {
        this.boolVal = boolVal;
    }

    private JSONValue(Number numVal) {
        this.numVal = numVal;
    }

    private JSONValue(JSONObject objVal) {
        this.objVal = objVal;
    }

    private JSONValue(JSONArray jsonArray){
        this.arrayVal = jsonArray;
    }

    public static JSONValue of(Object obj) {

        assert (obj instanceof String || obj instanceof Boolean || obj instanceof Number || obj instanceof JSONObject || obj instanceof JSONArray || obj == null);

        if(obj instanceof String){
            return new JSONValue((String) obj);
        } else if(obj instanceof Boolean){
            return new JSONValue((Boolean) obj);
        }else if(obj instanceof Number){
            return new JSONValue((Number) obj);
        }else  if(obj instanceof JSONObject){
            return new JSONValue((JSONObject) obj);
        }else if(obj instanceof JSONArray){
            return new JSONValue((JSONArray) obj);
        }else {
            return null;
        }
    }

    public void checkIsNull(Object obj,Class cls) throws CastException {
        if(obj == null){
            throw new CastException("value type is not " + cls);
        }
    }

    public String strValue() throws CastException {
        checkIsNull(strVal,String.class);
        return strVal;
    }

    public Boolean boolValue() throws CastException {
        checkIsNull(boolVal,Boolean.class);
        return boolVal;
    }

    public Number numberValue() throws CastException {
        checkIsNull(numVal,Number.class);
        return numVal;
    }

    public JSONObject objectValue() throws CastException {
        checkIsNull(objVal,JSONObject.class);
        return objVal;
    }

    public JSONArray arrayValue() throws CastException {
        checkIsNull(arrayVal,JSONArray.class);
        return arrayVal;
    }
}
