package com.zhangguojian.json;

import com.zhangguojian.json.exception.InvalidCharacterException;
import com.zhangguojian.json.exception.JSONException;
import com.zhangguojian.json.exception.NoViableTokenException;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

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
    public void parseArray() throws JSONException {
        //数组为空
        assertThat(new Parser("[]").parse())
                .isEqualTo(Collections.EMPTY_LIST);
        //[1,2,3,4]
        assertThat(new Parser("[1,2,3,4]").parse())
                .isEqualTo(Arrays.asList(1.0,2.0,3.0,4.0));
        //递归的情况 [[1,2],[3,4],[]]
        assertThat(new Parser("[[1,2],[3,4],[]]").parse())
                .isEqualTo(Arrays.asList(Arrays.asList(1.0,2.0),
                                         Arrays.asList(3.0,4.0),
                                         Collections.EMPTY_LIST));

        //更深层的递归 [[1,2,[3]],[3,4],[]]
        assertThat(new Parser("[[1,2,[3]],[3,4],[]]").parse())
                .isEqualTo(Arrays.asList(
                        Arrays.asList(1.0,2.0,Arrays.asList(3.0))
                        ,Arrays.asList(3.0,4.0)
                        ,Collections.EMPTY_LIST));

        //不同类型的数组 [true,false,null,"Hello",3.1415E10]
        assertThat(new Parser("[true,false,null,\"Hello\",3.1415E10]").parse())
                .isEqualTo(Arrays.asList(Boolean.TRUE,Boolean.FALSE,null,"Hello",3.1415E10));

        //解析失败的情况
        assertThatThrownBy(() -> new Parser("[,]").parse())
                .isInstanceOf(NoViableTokenException.class);

        assertThatThrownBy(() -> new Parser("[1,2,]").parse())
                .isInstanceOf(NoViableTokenException.class);
    }

    @Test
    public void parseEOF() throws JSONException {
        assertThatThrownBy(() -> new Parser("").parse())
                .isInstanceOf(NoViableTokenException.class);
    }
}
