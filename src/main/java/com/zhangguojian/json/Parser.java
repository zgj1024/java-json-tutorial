package com.zhangguojian.json;

import com.zhangguojian.json.exception.InvalidCharacterException;
import com.zhangguojian.json.exception.JSONException;
import com.zhangguojian.json.exception.NoViableTokenException;
import com.zhangguojian.json.exception.NumberParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zhangguojian.json.TokenType.*;
public class Parser {

    private Lexer input;
    private Token forward;
    private final static ArrayList<Object> EMPTY_ARRAY = new ArrayList<>();
    private final static Map<String,Object> EMPTY_OBJ = new HashMap<>();
    public Parser(Lexer input) throws InvalidCharacterException {
        this.input = input;
        this.forward = input.getNextToken();
    }

    Object parse() throws JSONException {
        Object value = value();
        match(EOF);
        return value;
    }

    private Object value() throws JSONException {
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
                Double numValue = Double.valueOf(forward.text);
                if (Double.isInfinite(numValue)) {
                    throw new NumberParseException("nums: " + forward.text + " is too big or too small");
                }
                match(NUM);
                return numValue;
            case LB:
                return parseArray();
            case LP:
                return parseObj();
            default:
                throw new NoViableTokenException("Unexpected token is " + forward.tokenType);
        }
    }

    /* array: '[' ']' | '[' elements '] */
    private List<Object> parseArray() throws JSONException {
        match(LB);

        List<Object> array = new ArrayList<>();
        if(forward.tokenType == RB){
            match(RB);
            return array;
        }else {
            elements(array);
            match(RB);
        }
        return  array;
    }

    /* elements: element (',' element)* */
    private void elements(List<Object> array) throws JSONException {
        element(array);
        while (forward.tokenType == COMMA){
            match(COMMA);
            elements(array);
        }
    }

    /* element: value */
    private void element(List<Object> array) throws JSONException {
        array.add(value());
    }

    /* object: '{' '}' | '{' members '}' */
    private Map<String,Object> parseObj () throws JSONException {
        match(LP);
        if(forward.tokenType == RP){
            match(RP);
            return EMPTY_OBJ;
        }else {
            Map<String,Object> objMap=new HashMap<>();
            members(objMap);
            match(RP);
            return objMap;
        }
    }

    /* members : member , members */
    private void members(Map<String,Object> objMap) throws JSONException {
        member(objMap);
        while (forward.tokenType == COMMA){
            match(COMMA);
            members(objMap);
        }
    }

    /* member: string ':' element */
    private void member(Map<String,Object> map) throws JSONException {
        String key = forward.text;
        match(STR);
        match(COLON);
        map.put(key,value());
    }

    private void match(TokenType tokenType) throws JSONException {
        if(forward.tokenType == tokenType){
            this.forward = input.getNextToken();
        }else {
            throw new NoViableTokenException("expected token is "+ tokenType
                    + " but actual is " + forward.tokenType);
        }
    }
}
