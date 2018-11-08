package com.zhangguojian.json;

import com.zhangguojian.json.exception.InvalidCharacterException;

public class Lexer {

    private String input;

    private int p = -1;
    private char c;

    private char EOF = (char) -1;

    public Lexer(String input){
        this.input = input;
        nextChar();
    }


    public Token getNextToken() throws InvalidCharacterException {
        while (c!=EOF){
            switch (c){
                case '\r': case '\n': case '\t': case ' ':ws();break;
                case 'n':
                    return scanNull();
                case 'f':
                    return scanFalse();
                case 't':
                    return scanTrue();
                default:
                    throw new InvalidCharacterException("invalid character: " + c);
            }
        }
        return Token.EOF;
    }


    private Token scanText(String keyword,Token token) throws InvalidCharacterException {
        assert(keyword!=null);
        for(int i = 0 ; i < keyword.length() ;i++){
            if(c!=keyword.charAt(i)){
                throw new InvalidCharacterException("invalid character: "+ keyword.substring(0,i) + c);
            }
            nextChar();
        }
        if(isSeparatorChar()){
            return token;
        }
        throw new InvalidCharacterException("invalid character: "+ keyword + c);
    }

    private Token scanNull() throws InvalidCharacterException {
        assert c == 'n';
        return scanText("null", Token.NULL);
    }

    private Token scanTrue() throws InvalidCharacterException {
        assert c == 't';
        return scanText("true", Token.TRUE);
    }

    private Token scanFalse() throws InvalidCharacterException {
        assert c == 'f';
        return scanText("false", Token.FALSE);
    }

    private void ws(){
        while (c == ' ' || c== '\t' || c == '\r' || c == '\n')
            nextChar();
    }

    private boolean isSeparatorChar(){
        return c == ' ' || c == ',' || c == '}' || c == ']' || c == '\n' || c == '\r' || c == '\t'
                || c == EOF|| c == '\f' || c == '\b';
    }
    //下一个字符
    private void nextChar(){
        p++;
        if ( p < input.length()){
            c = input.charAt(p);
        }else {
            c= EOF;
        }
    }
}