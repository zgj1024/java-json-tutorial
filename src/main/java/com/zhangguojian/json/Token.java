package com.zhangguojian.json;

public class Token {
    public TokenType tokenType;
    public String text;

    public Token(TokenType tokenType, String text) {
        this.tokenType = tokenType;
        this.text = text;
    }

}
