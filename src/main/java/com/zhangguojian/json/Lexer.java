package com.zhangguojian.json;

public class Lexer {

    private String input;

    private int p = -1;
    private char c;

    private char EOF = (char) -1;

    public Lexer(String input){
        this.input = input;
        nextChar();
    }

    public Token getNextToken(){
        //TODO
        return null;
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