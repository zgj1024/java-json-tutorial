package com.zhangguojian.json;

import com.zhangguojian.json.exception.InvalidCharacterException;
import com.zhangguojian.json.exception.JSONException;
import com.zhangguojian.json.exception.NoViableTokenException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static com.zhangguojian.json.TokenType.*;

public class Parser {

    private Lexer input;
    private Token forward;

    public Parser(String input) throws InvalidCharacterException {
        this.input = new Lexer(input);
        this.forward = this.input.getNextToken();
    }

    JSONValue parse() throws JSONException {
        Object value = value();
        match(EOF);
        return JSONValue.of(value);
    }

    private static boolean isDecimalNotation(final String val) {
        return val.indexOf('.') > -1 || val.indexOf('e') > -1
                || val.indexOf('E') > -1 || "-0".equals(val);
    }

    static Number parseNumber(final String val){
        if (isDecimalNotation(val)) {
            final Double d = Double.valueOf(val);
            if (d.isInfinite() || d.isNaN()) {
                return new BigDecimal(val);
            }
            return d;
        }

        BigInteger bigIntValue = new BigInteger(val);
        if(bigIntValue.bitLength()<=31){
            return bigIntValue.intValue();
        }
        if(bigIntValue.bitLength()<=63){
            return bigIntValue.longValue();
        }
        return bigIntValue;

    }

    public Object value() throws JSONException {
        switch (forward.tokenType) {
            case NULL:
                match(NULL);
                return null;
            case TRUE:
                match(TRUE);
                return Boolean.TRUE;
            case FALSE:
                match(FALSE);
                return Boolean.FALSE;
            case STR:
                String strValue = forward.text;
                match(STR);
                return strValue;
            case NUM:
                String numText =forward.text;
                match(NUM);
                return parseNumber(numText);
            case LB:
                return parseArray();
            case LP:
                return parseObj();
            default:
                throw new NoViableTokenException("Unexpected token is " + forward.tokenType);
        }
    }

    /* array: '[' ']' | '[' elements '] */
    private JSONArray parseArray() throws JSONException {
        match(LB);

        if (forward.tokenType == RB) {
            match(RB);
            return JSONArray.EMPTY;
        } else {
            JSONArray array = new JSONArray();
            elements(array);
            match(RB);
            return array;
        }
    }

    /* elements: element (',' element)* */
    private void elements(List<Object> array) throws JSONException {
        element(array);
        while (forward.tokenType == COMMA) {
            match(COMMA);
            elements(array);
        }
    }

    /* element: value */
    private void element(List<Object> array) throws JSONException {
        array.add(value());
    }

    /* object: '{' '}' | '{' members '}' */
    private JSONObject parseObj() throws JSONException {
        match(LP);
        if (forward.tokenType == RP) {
            match(RP);
            return JSONObject.EMPTY;
        } else {
            JSONObject objMap = new JSONObject();
            members(objMap);
            match(RP);
            return objMap;
        }
    }

    /* members : member , members */
    private void members(JSONObject objMap) throws JSONException {
        member(objMap);
        while (forward.tokenType == COMMA) {
            match(COMMA);
            members(objMap);
        }
    }

    /* member: string ':' element */
    private void member(JSONObject map) throws JSONException {
        String key = forward.text;
        match(STR);
        match(COLON);
        map.put(key, value());
    }

    private void match(TokenType tokenType) throws JSONException {
        if (forward.tokenType == tokenType) {
            this.forward = input.getNextToken();
        } else {
            throw new NoViableTokenException("expected token is " + tokenType
                    + " but actual is " + forward.tokenType);
        }
    }
}
