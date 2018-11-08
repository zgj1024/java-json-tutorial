package com.zhangguojian.json;


import com.zhangguojian.json.exception.InvalidCharacterException;
import com.zhangguojian.json.exception.JSONException;
import com.zhangguojian.json.exception.NoViableTokenException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.zhangguojian.json.TokenType.*;

public class Parser {

    private Lexer lexer;
    private Token forward;

    public Parser(String input) throws InvalidCharacterException {
        this.lexer = new Lexer(input);
        this.forward = lexer.getNextToken();
    }

    Object parse() throws JSONException {
        Object value = value();
        match(EOF);
        return value;
    }

    Object value() throws JSONException {
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
                match(NUM);
                return numValue;
            case LB:
                return parseArray();
            default:
                throw new NoViableTokenException("Unexpected token is " + forward.tokenType);
        }
    }

    /* array: '[' ']' | '[' elements '] */
    private List<Object> parseArray() throws JSONException {
        match(LB);

        if(forward.tokenType == RB){
            match(RB);
            return Collections.emptyList();
        }else {
            List<Object> array = new ArrayList<>();
            elements(array);
            match(RB);
            return array;
        }
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

    private void match(TokenType tokenType) throws JSONException {
        if (forward.tokenType == tokenType) {
            this.forward = lexer.getNextToken();
        } else {
            throw new NoViableTokenException("expected token is " + tokenType
                    + "but actual is " + forward.tokenType);
        }
    }
}