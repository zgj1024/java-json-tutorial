package com.zhangguojian.json;


import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import com.zhangguojian.json.exception.CastException;
import com.zhangguojian.json.exception.JSONException;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JSONTest {

    @Test
    public void testParse() throws IOException, JSONException {
        assertThat(JSON.parse("null")).isEqualTo(null);
        String content = new String(Files.readAllBytes(Paths.get("src/test/data/juejin-me.json")));


        assertThat(JSON.parse("12343").numberValue()).isEqualTo(12343);

        assertThat(JSON.parse("true").boolValue()).isEqualTo(true);

        assertThat(JSON.parse("\"Hello\"").strValue()).isEqualTo("Hello");


        JSONObject jsonObject = JSON.parse(content).objectValue();
        JSONObject data = jsonObject.getJSONObject("d");
        assertThat(data.getString("username")).isEqualTo("挖坑英雄小王");
        assertThat(data.getString("jobTitle")).isEqualTo("首席挖坑员");




        JSONArray array = JSON.parse("[1,2,3,4]").arrayValue();
        assertThat(array).isEqualTo(Arrays.asList(1,2,3,4));

        assertThatThrownBy(() -> new Parser("[1,2,3,4]").parse().objectValue())
                .isInstanceOf(CastException.class);


    }


    @Test
    public void testStringify() throws JSONException {
        Object NULL = null;
        assertThat(JSON.stringify(NULL)).isEqualTo("null");

        assertThat(JSON.stringify("\"/\t\n\f\b\n\r\\")).isEqualTo("\"\\\"\\/\\t\\n\\f\\b\\n\\r\\\\\"");
        assertThat(JSON.stringify(213)).isEqualTo("213");
        assertThat(JSON.stringify(new BigInteger("12344444"))).isEqualTo("12344444");

        JSONArray jsonArray = new JSONArray<>();
        jsonArray.add(1);
        jsonArray.add(2);
        jsonArray.add(3);
        jsonArray.add(4);
        assertThat(JSON.stringify(jsonArray)).isEqualTo("[1,2,3,4]");


        LinkedBlockingQueue<Integer> q=  new LinkedBlockingQueue();
        q.add(1);
        q.add(2);
        q.add(3);
        q.add(4);
        assertThat(JSON.stringify(q)).isEqualTo("[1,2,3,4]");

        Integer []array = {1,2,3,4};
        assertThat(JSON.stringify(array)).isEqualTo("[1,2,3,4]");

        Integer []array2 = {1,2,3,4};
        assertThat(JSON.stringify(array2)).isEqualTo("[1,2,3,4]");

        JSONArray<String> jsonArray2 = new JSONArray<>();
        jsonArray2.add("\"");
        jsonArray2.add("\t");

        assertThat(JSON.stringify(jsonArray2)).isEqualTo("[\"\\\"\",\"\\t\"]");

        Person person = new Person();
        person.setName("u87");
        assertThat(JSON.stringify(person)).isEqualTo("{\"name\":\"u87\"}");

    }
//    @Test
//    public void testParseWithGeneric() throws IOException, JSONException, IllegalAccessException, InvocationTargetException, InstantiationException {
//        assertThat(JSON.parse("123",BigInteger.class)).isEqualTo(BigInteger.valueOf(123L));
//
//        assertThat(JSON.parse("true",boolean.class)).isEqualTo(true);
//
//
//
//        assertThat(JSON.parse("\"Hello\"",String.class)).isEqualTo("Hello");
//        assertThat(JSON.parse("true",Boolean.class)).isEqualTo(true);
//
//        assertThat( JSON.parse("[1,2,3,4]", List.class)).isEqualTo(Arrays.asList(1,2,3,4));
//        assertThat( JSON.parse("[1,2,3,4]", Queue.class)).isEqualTo(Arrays.asList(1,2,3,4));
//
//        assertThat( JSON.parse("{\"name\":\"a\"}", Person.class).getName()).isEqualTo("a");
//    }

}