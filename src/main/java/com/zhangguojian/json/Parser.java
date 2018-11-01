package com.zhangguojian.json;

import com.zhangguojian.json.exception.InvalidCharacterException;
import com.zhangguojian.json.exception.JSONException;
import com.zhangguojian.json.exception.NoViableTokenException;

public class Parser {

    private Lexer input;

    public Parser(Lexer input) {
        this.input = input;
    }

     Object parse() throws JSONException {
        Object value = value();
        Token token = input.getNextToken();
        if( token!= Token.EOF){
            throw new NoViableTokenException("Unexpected token is " + token.tokenType);
        }
        return value;
    }

    private Object value() throws JSONException {
        Token token = input.getNextToken();
        switch (token.tokenType){
            case NULL:
                return null;
            case TRUE:
                return Boolean.TRUE;
            case FALSE:
                return Boolean.FALSE;
            case STR:
                return token.text;
            default:
                throw new NoViableTokenException("Unexpected token is " + token.tokenType);
        }
    }
}
