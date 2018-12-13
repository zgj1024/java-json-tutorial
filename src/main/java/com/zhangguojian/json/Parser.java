package com.zhangguojian.json;

import com.zhangguojian.json.exception.JSONException;
import com.zhangguojian.json.exception.NoViableTokenException;

import java.io.IOException;
import java.io.Reader;

import static com.zhangguojian.json.ParserUtils.parseNumber;
import static com.zhangguojian.json.TokenType.*;

public class Parser {

    private Lexer input;
    private Token forward = null ;

    public Parser(Reader input)  {
        this.input = new Lexer(input);
    }

    public Parser(String input)  {
        this.input = new Lexer(input);
    }

    public JSONElement parse() throws JSONException, IOException {
        JSONElement value = value();
        match(EOF);
        return value;
    }


    public JSONElement value() throws JSONException, IOException {
        if(forward == null){
            forward = input.getNextToken();
        }
        switch (forward.tokenType) {
            case NULL:
                match(NULL);
                return JSONNull.INSTANCE;
            case TRUE:
                match(TRUE);
                return JSONPrimitive.of(true);
            case FALSE:
                match(FALSE);
                return JSONPrimitive.of(false);
            case STR:
                String strValue = forward.text;
                match(STR);
                return JSONPrimitive.of(strValue);
            case NUM:
                String numText =forward.text;
                match(NUM);
                return JSONPrimitive.of(parseNumber(numText));
            case BEGIN_ARRAY:
                return parseArray();
            case BEGIN_OBJ:
                return parseObj();
            default:
                throw new NoViableTokenException("Unexpected token is " + forward.tokenType);
        }
    }

    /* array: '[' ']' | '[' elements '] */
    private JSONArray parseArray() throws JSONException, IOException {
        match(BEGIN_ARRAY);

        if (forward.tokenType == END_ARRAY) {
            match(END_ARRAY);
            return JSONArray.EMPTY;
        } else {
            JSONArray array =  new JSONArray();
            elements(array);
            match(END_ARRAY);
            return array;
        }
    }

    /* elements: element (',' element)* */
    private void elements(JSONArray array) throws JSONException, IOException {
        element(array);
        while (forward.tokenType == COMMA) {
            match(COMMA);
            elements(array);
        }
    }

    /* element: value */
    private void element(JSONArray array) throws JSONException, IOException {
        array.add(value());
    }

    /* object: '{' '}' | '{' members '}' */
    private JSONObject parseObj() throws JSONException, IOException {
        match(BEGIN_OBJ);
        if (forward.tokenType == END_OBJ) {
            match(END_OBJ);
            return JSONObject.EMPTY;
        } else {
            JSONObject objMap = new JSONObject();
            members(objMap);
            match(END_OBJ);
            return objMap;
        }
    }

    /* members : member , members */
    private void members(JSONObject objMap) throws JSONException, IOException {
        member(objMap);
        while (forward.tokenType == COMMA) {
            match(COMMA);
            members(objMap);
        }
    }

    /* member: string ':' element */
    private void member(JSONObject map) throws JSONException, IOException {
        String key = forward.text;
        match(STR);
        match(COLON);
        map.add(key, value());
    }

    public Token match(TokenType tokenType) throws JSONException, IOException {
        if (forward.tokenType == tokenType) {
            Token token = forward;
            this.forward = input.getNextToken();
            return token;
        } else {
            throw new NoViableTokenException("expected token is " + tokenType
                    + " but actual is " + forward.tokenType);
        }
    }

}
