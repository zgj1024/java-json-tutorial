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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ParserTest {


    @Test
    public void testParseNull() throws JSONException, IOException {
        assertThat(new Parser("null").parse())
                .isEqualTo(JSONNull.INSTANCE);
        assertThat(new Parser("null\r").parse())
                .isEqualTo(JSONNull.INSTANCE);
    }

    @Test
    public void testParseBoolean() throws JSONException, IOException {
        assertThat(new Parser("true").parse().getAsBoolean())
                .isEqualTo(Boolean.TRUE);
        assertThat(new Parser("false").parse().getAsBoolean())
                .isEqualTo(Boolean.FALSE);

        assertThatThrownBy(() -> new Parser("true true").parse())
                .isInstanceOf(NoViableTokenException.class);
    }

    @Test
    public void testParseString() throws JSONException, IOException {
        assertThatThrownBy(() -> new Parser("\"\"\"").parse())
                .isInstanceOf(InvalidCharacterException.class);

        assertThat(new Parser("\"hello world\"").parse().getAsString())
                .isEqualTo("hello world");
    }

    @Test
    public void testParseNum() throws JSONException, IOException {
        assertThat(new Parser("3.14159E10").parse().getAsNumber())
                .isEqualTo(3.14159E10);

        assertThat(new Parser("3.14159E310").parse().getAsNumber())
                .isEqualTo(new BigDecimal("3.14159E310"));

        assertThat(new Parser("9999999999999").parse().getAsNumber())
                .isEqualTo(new Long("9999999999999"));

        assertThat(new Parser("9999999999999999999999").parse().getAsNumber())
                .isEqualTo(new BigInteger("9999999999999999999999"));

        //会溢出
        assertThat(new Parser("9999999999999999999999").parse().getAsNumber().intValue())
                .isEqualTo(new Integer("-1304428545"));

    }

    @Test
    public void testParseArray() throws JSONException, IOException {
        //数组为空
        assertThat(new Parser("[]").parse().getAsJSONArray())
                .isEqualTo(JSONArray.EMPTY);
        //[1,2,3,4]
        assertThat(new Parser("[1,2,3,4]").parse().getAsJSONArray())
                .isEqualTo(JSONArray.of(JSONPrimitive.of(1),
                                        JSONPrimitive.of(2),
                                        JSONPrimitive.of(3),
                                        JSONPrimitive.of(4)));
        //递归的情况 [[1,2],[3,4],[]]
        assertThat(new Parser("[[1,2],[3,4],[]]").parse().getAsJSONArray())
                .isEqualTo(JSONArray.of(JSONArray.of(JSONPrimitive.of(1),JSONPrimitive.of(2)),
                                        JSONArray.of(JSONPrimitive.of(3),JSONPrimitive.of(4)),
                                        JSONArray.EMPTY));

        //更深层的递归 [[1,2,[3]],[3,4],[]]
        assertThat(new Parser("[[1,2,[3]],[3,4],[]]").parse().getAsJSONArray())
                .isEqualTo(JSONArray.of(
                        JSONArray.of(JSONPrimitive.of(1),JSONPrimitive.of(2),JSONArray.of(JSONPrimitive.of(3))),
                        JSONArray.of(JSONPrimitive.of(3),JSONPrimitive.of(4)),
                        JSONArray.EMPTY));
        //不同类型的数组 [true,false,null,"Hello",3.1415E10]
        assertThat(new Parser("[true,false,null,\"Hello\",3.1415E10]").parse().getAsJSONArray())
                .isEqualTo(JSONArray.of(JSONPrimitive.of(Boolean.TRUE),
                                        JSONPrimitive.of(Boolean.FALSE),
                                        JSONNull.INSTANCE,
                                        JSONPrimitive.of("Hello"),
                                        JSONPrimitive.of(3.1415E10)));

        //解析失解析失败的情况败的情况
        assertThatThrownBy(() -> new Parser("[,]").parse().getAsJSONArray())
                .isInstanceOf(NoViableTokenException.class);

        assertThatThrownBy(() -> new Parser("[1,2,]").parse().getAsJSONArray())
                .isInstanceOf(NoViableTokenException.class);
    }

    @Test
    public void testParseObj() throws JSONException, IOException {
        JSONObject v1 =  new Parser("{\"name\":\"John Smith\",\"age\":15}").parse().getAsJSONObject();
        assertThat(v1.get("name").getAsString()).isEqualTo("John Smith");
        assertThat(v1.getAsJsonPrimitive("age").getAsDouble()).isEqualTo(15.0);

        String content = new String(Files.readAllBytes(Paths.get("src/test/data/juejin-me.json")));

        JSONObject v2 =  new Parser(content).parse().getAsJSONObject();
        JSONObject v3 =  v2.getAsJsonObject("d");
        assertThat(v3.get("username").getAsString()).isEqualTo("挖坑英雄小王");
        assertThat(v3.get("jobTitle").getAsString()).isEqualTo("首席挖坑员");

        JSONObject v4 =  new Parser("{}").parse().getAsJSONObject();
        assertThat(v4).isEqualTo(JSONObject.EMPTY);

        assertThatThrownBy(() -> new Parser("{,}").parse())
                .isInstanceOf(NoViableTokenException.class);

    }
}
