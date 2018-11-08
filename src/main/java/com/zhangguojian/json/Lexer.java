package com.zhangguojian.json;

import com.zhangguojian.json.exception.InvalidCharacterException;

public class Lexer  {

    private String input;

    private int p = -1;
    private char c;

    private char EOF = (char) -1;

    public Lexer(String input) {
        this.input = input;
        nextChar();
    }

    public Token getNextToken() throws InvalidCharacterException {
        while (c != EOF) {
            switch (c) {
                case '\r':
                case '\n':
                case '\t':
                case ' ':
                    ws();
                    break;
                case 'n':
                    return scanNull();
                case 'f':
                    return scanFalse();
                case 't':
                    return scanTrue();
                case '\"':
                    return scanString();
                case '-':
                    return scanNum();
                case '[':
                    nextChar();
                    return Token.LB;
                case ']':
                    nextChar();
                    return Token.RB;
                case ',':
                    nextChar();
                    return Token.COMMA;
                case '{':
                    nextChar();
                    return Token.LP;
                case ':':
                    nextChar();
                    return Token.COLON;
                case '}':
                    nextChar();
                    return Token.RP;
                default:
                    if(isDigit()){
                        return scanNum();
                    }
                    throw new InvalidCharacterException("invalid character: " + c);
            }
        }
        return Token.EOF;
    }

    private Token scanText(String keyword, Token token) throws InvalidCharacterException {
        assert (keyword != null);
        for (int i = 0; i < keyword.length(); i++) {
            if (c != keyword.charAt(i)) {
                throw new InvalidCharacterException("invalid character: " + keyword.substring(0, i) + c);
            }
            nextChar();
        }
        if (isSeparatorChar()) {
            return token;
        }
        throw new InvalidCharacterException("invalid character: " + keyword + c);
    }

    private Token scanNull() throws InvalidCharacterException {
        return scanText("null", Token.NULL);
    }

    private Token scanTrue() throws InvalidCharacterException {
        return scanText("true", Token.TRUE);
    }

    private Token scanFalse() throws InvalidCharacterException {
        return scanText("false", Token.FALSE);
    }

    private Token scanString() throws InvalidCharacterException {
        assert (c == '\"');
        StringBuilder sb = new StringBuilder();

        sb.append(sb);
        nextChar();

        while (c != EOF) {
            if (c == '\"') {
                nextChar();
                return new Token(TokenType.STR, sb.toString());
            } else if (c == '\\') {
                nextChar();
                ESCAPE(sb);
            } else {
                sb.append(c);
                nextChar();
            }
        }

        throw new InvalidCharacterException("invalid token: " + sb.toString());
    }

    private void ESCAPE(StringBuilder sb) throws InvalidCharacterException {
        switch (c) {
            case '\"':
                sb.append('\"');
                break;
            case 'r':
                sb.append('\r');
                break;
            case 'n':
                sb.append('\n');
                break;
            case 'f':
                sb.append('\f');
                break;
            case 'b':
                sb.append('\b');
                break;
            case 't':
                sb.append('\t');
                break;
            case '/':
                sb.append('/');
                break;
            case 'u':
                //处理 unicode 转义
                nextChar();
                int i = 0;
                StringBuilder unicode = new StringBuilder("");
                while (i < 4 && isHex()) {
                    unicode.append(c);
                    nextChar();
                    i++;
                }

                if (i == 4) {
                    String unicodeStr = unicode.toString();
                    if ("feff".equals(unicodeStr)) {//UTF-8 Boom
                        return;
                    }
                    sb.append((char) Integer.parseInt(unicodeStr, 16));
                    return;
                }
                throw new InvalidCharacterException("invalid token: " + unicode.toString());
            case '\\':
                sb.append('\\');
                break;
            default:
                throw new InvalidCharacterException("invalid token: " + sb.toString());
        }
        nextChar();
    }

    /* num: int frac exp */
    private Token scanNum() throws InvalidCharacterException {
        assert(c == '-' || isDigit());
        StringBuilder sb = new StringBuilder();
        scanInt(sb);
        scanFrac(sb);
        scanExp(sb);
        if(isSeparatorChar()){
            return new Token(TokenType.NUM,sb.toString());
        }
        throw new InvalidCharacterException("invalid num "+ sb.toString());
    }

    /* int: digit | onenine digits|  '-' digit | '-' onenine digits */
    private void scanInt(StringBuilder sb){
        if(c == '-'){
            sb.append(c);
            nextChar();
        }
        if(c == '0'){
            sb.append(c);
            nextChar();
        }else {
            while(isDigit()){
                sb.append(c);
                nextChar();
            }
        }
    }

    /* frac: '' || '.' digits */
    private void scanFrac(StringBuilder sb){
        if(c=='.'){
            sb.append(c);
            nextChar();
            while (isDigit()){
                sb.append(c);
                nextChar();
            }
        }
    }

   /*exp: "" | ("E"|'e') sign digits*/
    private void scanExp(StringBuilder sb) throws InvalidCharacterException {
        if(c != 'e' && c != 'E')
            return;

        sb.append(c);
        nextChar();

        if(c == '+' || c=='-'){
            sb.append(c);
            nextChar();
        }

        //如果 E 后没有数字的话，是非法的数字，比如 10E
        if(!isDigit()){
            throw new InvalidCharacterException("invalid num "+sb.toString());
        }

        while (isDigit()){
            sb.append(c);
            nextChar();
        }
    }

    private boolean isSeparatorChar() {
        return c == ' ' || c == ',' || c == '}' || c == ']' || c == '\n' || c == '\r'
                || c == '\t' || c == EOF || c == '\f' || c == '\b';
    }

    private boolean isHex() {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F';
    }

    private boolean isDigit(){
        return c== '0' || isOneNine();
    }

    private boolean isOneNine(){
        return c>='1' && c<='9';
    }

    private void ws() {
        while (c == ' ' || c == '\t' || c == '\r' || c == '\n')
            nextChar();
    }

    private void nextChar() {
        p++;
        if (p < input.length()) {
            c = input.charAt(p);
        } else {
            c = EOF;
        }
    }

}
