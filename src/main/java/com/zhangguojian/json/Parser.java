package com.zhangguojian.json;


import com.zhangguojian.json.exception.JSONException;
import com.zhangguojian.json.exception.NoViableTokenException;

public class Parser {

    private Lexer lexer;

    public Parser(String input) {
        this.lexer = new Lexer(input);
    }

    Object parse() throws JSONException {
        Object value = value();
        Token token = lexer.getNextToken();
        if(token!= Token.EOF){
            throw new NoViableTokenException("Unexpected token is " + token.tokenType);
        }
        return value;
    }

    Object value() throws JSONException {
        Token token = lexer.getNextToken();
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