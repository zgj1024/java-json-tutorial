package com.zhangguojian.json;

public class Token {
    public final static Token TRUE = new Token(TokenType.TRUE,"true");
    public final static Token FALSE = new Token(TokenType.FALSE,"false");
    public final static Token NULL = new Token(TokenType.NULL,"null");
    public final static Token EOF = new Token(TokenType.EOF,"");

    public TokenType tokenType;
    public String text;

    public Token(TokenType tokenType, String text) {
        this.tokenType = tokenType;
        this.text = text;
    }

}
