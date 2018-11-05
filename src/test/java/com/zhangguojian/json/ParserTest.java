package com.zhangguojian.json;
import com.zhangguojian.json.exception.InvalidCharacterException;
import com.zhangguojian.json.exception.JSONException;
import com.zhangguojian.json.exception.NoViableTokenException;

import org.junit.Test;


import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ParserTest {


    @Test
    public void testParseNull() throws JSONException {
        assertThat(new Parser("null").parse())
                .isEqualTo(null);
        assertThat(new Parser("null\r").parse())
                .isEqualTo(null);
    }

    @Test
    public void testParseBoolean() throws JSONException {
        assertThat(new Parser("true").parse().boolValue())
                .isEqualTo(Boolean.TRUE);
        assertThat(new Parser("false").parse().boolValue())
                .isEqualTo(Boolean.FALSE);

        assertThatThrownBy(() -> new Parser("true true").parse())
                .isInstanceOf(NoViableTokenException.class);
    }

    @Test
    public void testParseString() throws JSONException {
        assertThatThrownBy(() -> new Parser("\"\"\"").parse())
                .isInstanceOf(InvalidCharacterException.class);

        assertThat(new Parser("\"hello world\"").parse().strValue())
                .isEqualTo("hello world");
    }

    @Test
    public void testParseNum() throws JSONException {
        assertThat(new Parser("3.14159E10").parse().numberValue())
                .isEqualTo(3.14159E10);

        assertThat(new Parser("3.14159E310").parse().numberValue())
                .isEqualTo(new BigDecimal("3.14159E310"));

        assertThat(new Parser("9999999999999").parse().numberValue())
                .isEqualTo(new Long("9999999999999"));

        assertThat(new Parser("9999999999999999999999").parse().numberValue())
                .isEqualTo(new BigInteger("9999999999999999999999"));

        //会溢出
        assertThat(new Parser("9999999999999999999999").parse().numberValue().intValue())
                .isEqualTo(new Integer("-1304428545"));

    }

    @Test
    public void testParseArray() throws JSONException {
        //数组为空
        assertThat(new Parser("[]").parse().arrayValue())
                .isEqualTo(Collections.emptyList());
        //[1,2,3,4]
        assertThat(new Parser("[1,2,3,4]").parse().arrayValue())
                .isEqualTo(Arrays.asList(1,2,3,4));
        //递归的情况 [[1,2],[3,4],[]]
        assertThat(new Parser("[[1,2],[3,4],[]]").parse().arrayValue())
                .isEqualTo(Arrays.asList(Arrays.asList(1,2),Arrays.asList(3,4), Collections.emptyList()));

        //更深层的递归 [[1,2,[3]],[3,4],[]]
        assertThat(new Parser("[[1,2,[3]],[3,4],[]]").parse().arrayValue())
                .isEqualTo(Arrays.asList(Arrays.asList(1,2, Collections.singletonList(3))
                        ,Arrays.asList(3,4)
                        ,Collections.emptyList()));
        //不同类型的数组 [true,false,null,"Hello",3.1415E10]
        assertThat(new Parser("[true,false,null,\"Hello\",3.1415E10]").parse().arrayValue())
                .isEqualTo(Arrays.asList(Boolean.TRUE,Boolean.FALSE,null,"Hello",3.1415E10));

        //解析失败的情况
        assertThatThrownBy(() -> new Parser("[,]").parse().arrayValue())
                .isInstanceOf(NoViableTokenException.class);

        assertThatThrownBy(() -> new Parser("[1,2,]").parse().arrayValue())
                .isInstanceOf(NoViableTokenException.class);
    }

    @Test
    public void testParseObj() throws JSONException, IOException {
        JSONObject v1 =  new Parser("{\"name\":\"John Smith\",\"age\":15}").parse().objectValue();
        assertThat(v1.get("name")).isEqualTo("John Smith");
        assertThat(v1.getDouble("age")).isEqualTo(15.0);

        String content = new String(Files.readAllBytes(Paths.get("src/test/data/juejin-me.json")));

        JSONObject v2 =  new Parser(content).parse().objectValue();
        JSONObject v3 =  v2.getJSONObject("d");
        assertThat(v3.get("username")).isEqualTo("挖坑英雄小王");
        assertThat(v3.get("jobTitle")).isEqualTo("首席挖坑员");

        JSONObject v4 =  new Parser("{}").parse().objectValue();
        assertThat(v4).isEmpty();

        assertThatThrownBy(() -> new Parser("{,}").parse())
                .isInstanceOf(NoViableTokenException.class);

    }
}
