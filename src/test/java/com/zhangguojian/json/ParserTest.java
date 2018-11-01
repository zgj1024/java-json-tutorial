package com.zhangguojian.json;

import com.sun.tools.javac.util.List;
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
    public void parseNum() throws JSONException {
        assertThat(new Parser(new Lexer("3.14159E10")).parse())
                .isEqualTo(3.14159E10);

        assertThatThrownBy(() -> new Parser(new Lexer("3E308")).parse())
                .isInstanceOf(NumberParseException.class);
    }

    @Test
    public void parseArray() throws JSONException {
        //数组为空
        assertThat(new Parser(new Lexer("[]")).parse())
                .isEqualTo(List.nil());
        //[1,2,3,4]
        assertThat(new Parser(new Lexer("[1,2,3,4]")).parse())
                .isEqualTo(List.of(1.0,2.0,3.0,4.0));
        //递归的情况 [[1,2],[3,4],[]]
        assertThat(new Parser(new Lexer("[[1,2],[3,4],[]]")).parse())
                .isEqualTo(List.of(List.of(1.0,2.0),List.of(3.0,4.0),List.nil()));

        //更深层的递归 [[1,2,[3]],[3,4],[]]
        assertThat(new Parser(new Lexer("[[1,2,[3]],[3,4],[]]")).parse())
                .isEqualTo(List.of(List.of(1.0,2.0,List.of(3.0))
                        ,List.of(3.0,4.0)
                        ,List.nil()));
        //不同类型的数组 [true,false,null,"Hello",3.1415E10]
        assertThat(new Parser(new Lexer("[true,false,null,\"Hello\",3.1415E10]")).parse())
                .isEqualTo(List.of(Boolean.TRUE,Boolean.FALSE,null,"Hello",3.1415E10));

        //解析失败的情况
        assertThatThrownBy(() -> new Parser(new Lexer("[,]")).parse())
                .isInstanceOf(NoViableTokenException.class);

        assertThatThrownBy(() -> new Parser(new Lexer("[1,2,]")).parse())
                .isInstanceOf(NoViableTokenException.class);
    }

}
