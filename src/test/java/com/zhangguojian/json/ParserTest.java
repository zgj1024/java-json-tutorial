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
    }

    @Test
    public void parseEOF() throws JSONException {
        assertThatThrownBy(() -> new Parser("").parse())
                .isInstanceOf(NoViableTokenException.class);
    }
}
