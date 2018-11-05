package com.zhangguojian.json;


import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import com.zhangguojian.json.exception.CastException;
import com.zhangguojian.json.exception.JSONException;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

        assertThat(JSON.stringify("\u4f60\u597d\u4e16\u754c")).isEqualTo("\"你好世界\"");
        assertThat(JSON.stringify("\u0080\u0081")).isEqualTo("\"\\u0080\\u0081\"");

        assertThat(JSON.stringify(213)).isEqualTo("213");
        assertThat(JSON.stringify(new BigInteger("12344444"))).isEqualTo("12344444");

        JSONArray<Integer> jsonArray = new JSONArray<>();
        jsonArray.add(1);
        jsonArray.add(2);
        jsonArray.add(3);
        jsonArray.add(4);
        assertThat(JSON.stringify(jsonArray)).isEqualTo("[1,2,3,4]");


        LinkedBlockingQueue<Integer> q=  new LinkedBlockingQueue<>();
        q.add(1);
        q.add(2);
        q.add(3);
        q.add(4);
        assertThat(JSON.stringify(q)).isEqualTo("[1,2,3,4]");

        Integer []array = {1,2,3,4};
        assertThat(JSON.stringify(array)).isEqualTo("[1,2,3,4]");

        int []array2 = {1,2,3,4};
        assertThat(JSON.stringify(array2)).isEqualTo("[1,2,3,4]");

        JSONArray<String> jsonArray2 = new JSONArray<>();
        jsonArray2.add("\"");
        jsonArray2.add("\t");
        assertThat(JSON.stringify(jsonArray2)).isEqualTo("[\"\\\"\",\"\\t\"]");

        Person person = new Person();
        person.setName("李四");
        person.setAge(68);
        person.setIsOk(true);
        person.setArray1(Arrays.asList(1,2,3,4));
        person.setArray2(array2);
        person.setQueue(q);

        Person son = new Person();
        son.setName("no one");
        son.setAge(1);

        person.setSon(son);
        JSONObject result = JSON.parse(JSON.stringify(person)).objectValue().getJSONObject("son");
        assertThat(result.getString("name")).isEqualTo("no one");
        assertThat(result.getInt("age")).isEqualTo(1);

    }

    @Test
    public void testStringifyMap(){
        assertThat(JSON.stringify(JSONObject.EMPTY)).isEqualTo("{}");

        JSONObject<String,Object> map1 = new JSONObject<>();
        map1.put("name","张三");
        map1.put("age",26);
        assertThat(JSON.stringify(map1)).isEqualTo("{\"name\":\"张三\",\"age\":26}");

        Map<String,Object> map2 = new HashMap<>();
        map2.put("name","张三");
        map2.put("age",26);
        assertThat(JSON.stringify(map1)).isEqualTo("{\"name\":\"张三\",\"age\":26}");

    }

}