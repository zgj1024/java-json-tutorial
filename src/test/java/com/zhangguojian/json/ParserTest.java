package com.zhangguojian.json;

import com.zhangguojian.json.exception.InvalidCharacterException;
import com.zhangguojian.json.exception.JSONException;
import com.zhangguojian.json.exception.NoViableTokenException;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class ParserTest {
    @Test
    public void parseNull() throws JSONException {
        assertThat(new Parser("null").parse())
                .isEqualTo(null);
    }

    @Test
    public void parseBoolean() throws JSONException {
        assertThat(new Parser("true").parse())
                .isEqualTo(Boolean.TRUE);

        assertThat(new Parser("false").parse())
                .isEqualTo(Boolean.FALSE);

        //非法的JSON
        assertThatThrownBy(() -> new Parser("true true").parse())
                .isInstanceOf(NoViableTokenException.class);
    }

    @Test
    public void parseString() throws JSONException {
        assertThatThrownBy(() -> new Parser("\"\"\"").parse())
                .isInstanceOf(InvalidCharacterException.class);

        assertThat(new Parser("\"hello world\"").parse())
                .isEqualTo("hello world");
    }

    @Test
    public void parseNum() throws JSONException {
        assertThat(new Parser("3.14159E10").parse())
                .isEqualTo(3.14159E10);

        assertThat(new Parser("3.3E308").parse())
                .isEqualTo(Double.POSITIVE_INFINITY);

    }

    @Test
    public void parseEOF() throws JSONException {
        assertThatThrownBy(() -> new Parser("").parse())
                .isInstanceOf(NoViableTokenException.class);
    }
}
