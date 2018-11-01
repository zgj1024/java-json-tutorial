package com.zhangguojian.json;

import com.zhangguojian.json.exception.JSONException;
import com.zhangguojian.json.exception.NoViableTokenException;
import com.zhangguojian.json.exception.NumberParseException;

public class Parser {

    private Lexer input;

    public Parser(Lexer input) {
        this.input = input;
    }

    Object parse() throws JSONException {
        Object value = value();
        Token token = input.getNextToken();
        if (token != Token.EOF) {
            throw new NoViableTokenException("Unexpected token is " + token.tokenType);
        }
        return value;
    }

    private Object value() throws JSONException {
        Token token = input.getNextToken();
        switch (token.tokenType) {
            case NULL:
                return null;
            case TRUE:
                return Boolean.TRUE;
            case FALSE:
                return Boolean.FALSE;
            case STR:
                return token.text;
            case NUM:
                Double v = Double.valueOf(token.text);
                if (Double.isInfinite(v)) {
                    throw new NumberParseException("nums: " + token.text + " is too big or too small");
                }
                return v;

            default:
                throw new NoViableTokenException("Unexpected token is " + token.tokenType);
        }
    }
}
