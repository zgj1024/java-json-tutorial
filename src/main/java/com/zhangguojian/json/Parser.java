package com.zhangguojian.json;

import com.zhangguojian.json.exception.InvalidCharacterException;
import com.zhangguojian.json.exception.JSONException;
import com.zhangguojian.json.exception.NoViableTokenException;
import com.zhangguojian.json.exception.NumberParseException;

import java.util.ArrayList;
import java.util.List;

import static com.zhangguojian.json.TokenType.*;
public class Parser {

    private Lexer input;
    private Token forward;
    private final static ArrayList<Object> EMPTY_ARRAY = new ArrayList<>();
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
            default:
                throw new NoViableTokenException("Unexpected token is " + forward.tokenType);
        }
    }

    public List<Object> parseArray() throws JSONException {
        match(LB);
        if(forward.tokenType == RB){
            match(RB);
            return EMPTY_ARRAY;
        }

        List<Object> objectList = new ArrayList<>();
        objectList.add(value());

        while (forward.tokenType == COMMA){
            match(COMMA);
            objectList.add(value());
        }

        match(RB);
        return  objectList;
    }
//    /* array: '[' elements ']' */
//    public JSONArray parseArray() throws JSONException {
//        match(LB);
//        JSONArray array = elements();
//        match(RB);
//        return  array;
//    }

    private JSONArray elements() throws JSONException {
        if(forward.tokenType == RB){
            return  JSONArray.EMPTY_ARRAY;
        }

        JSONArray array =new JSONArray();
        array.add(value());
        while (forward.tokenType == COMMA){
            match(COMMA);
            array.add(value());
        }
        return array;
    }

    public void match(TokenType tokenType) throws JSONException {
        if(forward.tokenType == tokenType){
            this.forward = input.getNextToken();
        }else {
            throw new NoViableTokenException("expected token is "+ tokenType
                    + " but actual is " + forward.tokenType);
        }
    }
}
