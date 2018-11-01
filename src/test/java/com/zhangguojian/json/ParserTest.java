package com.zhangguojian.json;

import com.zhangguojian.json.exception.InvalidCharacterException;
import com.zhangguojian.json.exception.JSONException;
import com.zhangguojian.json.exception.NoViableTokenException;

import com.zhangguojian.json.exception.NumberParseException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ParserTest {

    @Test
    public void parseNull() throws JSONException {
        assertThat(new Parser(new Lexer("null")).parse())
                .isEqualTo(null);
        assertThat(new Parser(new Lexer("null\r")).parse())
                .isEqualTo(null);
    }

    @Test
    public void parseBoolean() throws JSONException {
        assertThat(new Parser(new Lexer("true")).parse())
                .isEqualTo(Boolean.TRUE);
        assertThat(new Parser(new Lexer("false")).parse())
                .isEqualTo(Boolean.FALSE);

        assertThatThrownBy(() -> new Parser(new Lexer("true true")).parse())
                .isInstanceOf(NoViableTokenException.class);
    }

    @Test
    public void parseString() throws JSONException {
        assertThatThrownBy(() -> new Parser(new Lexer("\"\"\"")).parse())
                .isInstanceOf(InvalidCharacterException.class);

        assertThat(new Parser(new Lexer("\"hello world\"")).parse())
                .isEqualTo("hello world");
    }

    @Test
    public void parseDouble() throws JSONException {
        assertThat(new Parser(new Lexer("3.14159E10")).parse())
                .isEqualTo(3.14159E10);

        assertThatThrownBy(() -> new Parser(new Lexer("3E308")).parse())
                .isInstanceOf(NumberParseException.class);
    }

}
