package com.zhangguojian.json;

public class Token {

    public final static Token TRUE = new Token(TokenType.TRUE,"true");
    public final static Token FALSE = new Token(TokenType.FALSE,"false");
    public final static Token NULL = new Token(TokenType.NULL,"null");
    public final static Token EOF = new Token(TokenType.EOF,"");
    public final static Token LB = new Token(TokenType.LB,"[");
    public final static Token RB = new Token(TokenType.RB,"]");
    public final static Token COMMA = new Token(TokenType.COMMA,",");
    public final static Token LP = new Token(TokenType.LP,"{");
    public final static Token RP = new Token(TokenType.RP,"}");
    public final static Token COLON = new Token(TokenType.COLON,":");


    public TokenType tokenType;
    public String text;

    public Token(TokenType tokenType, String text) {
        this.tokenType = tokenType;
        this.text = text;
    }

}
