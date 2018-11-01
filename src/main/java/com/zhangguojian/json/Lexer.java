package com.zhangguojian.json;

import com.zhangguojian.json.exception.InvalidCharacterException;

public class Lexer {

    private String input;

    private int p = -1;
    private char c;

    private char EOF = (char) -1;

    public Lexer(String input) {
        this.input = input;
        nextChar();
    }

    private void ws() {
        while (c == ' ' || c == '\t' || c == '\r' || c == '\n')
            nextChar();
    }

    private boolean isSeparatorChar() {
        return c == ' ' || c == ',' || c == '}' || c == ']' || c == '\n' || c == '\r' || c == '\t' || c == EOF
                || c == '\f' || c == '\b';
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


    private boolean isHex() {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F';
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
                        break;
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

                default:
                    throw new InvalidCharacterException("invalid character: " + c);
            }
        }
        return Token.EOF;
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
