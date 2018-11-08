package com.zhangguojian.json;


import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.*;

import com.zhangguojian.json.bean.Hero;
import com.zhangguojian.json.bean.Person;
import com.zhangguojian.json.exception.CastException;
import com.zhangguojian.json.exception.JSONException;
import org.junit.Assert;
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
    public void testParseWithBind() throws JSONException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException, ClassNotFoundException {
        assertThat(JSON.parse("null",Object.class)).isEqualTo(null);

        //boolean
        assertThat(JSON.parse("true",Boolean.class)).isEqualTo(true);
        assertThat(JSON.parse("true",boolean.class)).isEqualTo(true);
        assertThat(JSON.parse("false",Boolean.class)).isEqualTo(false);

        assertThat(JSON.parse("\"Hello\"" ,String.class)).isEqualTo("Hello");
        assertThat(JSON.parse("123" ,Integer.class)).isEqualTo(Integer.valueOf(123));
        assertThat(JSON.parse("123" ,int.class)).isEqualTo(Integer.valueOf(123));

        assertThat(JSON.parse("123" ,Short.class)).isEqualTo((short)123);
        assertThat(JSON.parse("123" ,Long.class)).isEqualTo((long)123);
        assertThat(JSON.parse("123" ,BigDecimal.class)).isEqualTo(new BigDecimal("123.0"));
        assertThat(JSON.parse("123" ,BigInteger.class)).isEqualTo(new BigInteger("123"));
        assertThat(JSON.parse("123" ,Float.class)).isEqualTo(123f);
        assertThat(JSON.parse("123" ,Double.class)).isEqualTo(123.0);

        assertThat(JSON.parse("[1,2,3,4]" ,List.class)).isEqualTo(JSONArray.asList(1,2,3,4));
        assertThat(JSON.parse("[1,2,3,4]" ,ArrayList.class)).isEqualTo(JSONArray.asList(1,2,3,4));

        LinkedList linkedList = new LinkedList();
        linkedList.add(1);
        linkedList.add(2);
        linkedList.add(3);
        linkedList.add(4);
        assertThat(JSON.parse("[1,2,3,4]" ,LinkedList.class)).isEqualTo(linkedList);


        //这是不能实例化的
        assertThatThrownBy(() -> JSON.parse("[1,2,3,4]" ,Queue.class))
                .isInstanceOf(InstantiationException.class);

        //要指定事例的对象。
        JSONContext jsonContext = new JSONContext();
        jsonContext.addImpl(Queue.class,LinkedBlockingQueue.class);

        LinkedBlockingQueue queueResult = new LinkedBlockingQueue();
        queueResult.add(1);queueResult.add(2);queueResult.add(3);queueResult.add(4);
        assertThat(JSON.parse("[1,2,3,4]" ,Queue.class,jsonContext).toString()).isEqualTo(queueResult.toString());


        HashMap hashMapResult = new HashMap();
        hashMapResult.put("name","张三");
        hashMapResult.put("list",Arrays.asList(1,2,3,4));

        //"强转成 HashMap 而不是 JSONObject"
        jsonContext.addImpl(HashMap.class,HashMap.class);
        assertThat(JSON.parse("{\"name\":\"张三\"}" ,HashMap.class,jsonContext).getClass()).isEqualTo(HashMap.class);

        jsonContext.addImpl(JSONArray.class,ArrayList.class);
        jsonContext.addImpl(JSONObject.class,HashMap.class);
        //"强转成 HashMap 而不是 JSONObject"
        //"强转成 JSONArray 而不是 ArrayList"
        //可以递归执行
        assertThat(JSON.parse("{\"name\":\"张三\",\"friends\":[{\"name\":\"李四\" }]}" ,HashMap.class,jsonContext).get("friends").getClass()).isEqualTo(ArrayList.class);
        assertThat(((ArrayList)JSON.parse("{\"name\":\"张三\",\"friends\":[{\"name\":\"李四\" }]}" ,HashMap.class,jsonContext).get("friends")).get(0).getClass()).isEqualTo(HashMap.class);

        //转成对象。难啊
        Hero hero = new Hero();
        hero.setName("金庸");
        assertThat(JSON.parse("{\"name\":\"金庸\"}" ,Hero.class,jsonContext).getName()).isEqualTo(hero.getName());

    }


    @Test
    public void TestParseWithGeneric() throws InvocationTargetException, InstantiationException, JSONException, IllegalAccessException, ParseException, ClassNotFoundException {
        JSONContext jsonContext = new JSONContext();
        jsonContext.addImpl(JSONArray.class,ArrayList.class);
        jsonContext.addImpl(List.class,ArrayList.class);

        LinkedList<BigInteger> BigIntegerList =  JSON.parse("[1,2,3,4]",new TypeReference<LinkedList<BigInteger>>(){},jsonContext);
        assert BigIntegerList != null;
        for(BigInteger bigInteger : BigIntegerList){
            assertThat(bigInteger.getClass()).isEqualTo(BigInteger.class);
        }

        List<Integer> intList =  JSON.parse("[1,2,3,4]",new TypeReference<List<Integer>>(){},jsonContext);
        assertThat(intList.getClass()).isEqualTo(ArrayList.class);
        for(Integer i : intList){
            assertThat(i.getClass()).isEqualTo(Integer.class);
        }

        Hero result = JSON.parse("{\"name\":\"金庸\",\"heroList\":[{\"name\":\"夏梦\"},{\"name\":\"大仲马\"}]}" ,Hero.class,jsonContext);
        assertThat(result.getHeroList().getClass()).isEqualTo(ArrayList.class);
//        assertThat(result.getHeroList().get(0).getName()).isEqualTo("夏梦");
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
        person.setOk(true);
        person.setArray1(Arrays.asList(1,2,3,4));
        person.setArray2(array2);
        person.setQueue(q);

        Person son = new Person();
        son.setName("no one");
        son.setAge(1);

        person.setSon(son);
        JSONObject result = JSON.parse(JSON.stringify(person)).objectValue();
        JSONObject sonResult =result.getJSONObject("son");

        assertThat(result.getBoolean("ok")).isEqualTo(true);
        assertThat(sonResult.getString("name")).isEqualTo("no one");
        assertThat(sonResult.getInt("age")).isEqualTo(1);

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

    @Test
    public void testJSONIgnore() throws JSONException {
        Person king = new Person();
        king.setName("king");
        king.setAge(70);

        assertThat(king.getCanNotStringify()).isEqualTo("canNotStringify");
        assertThat(king.getDeathAge()).isEqualTo(100);
        assertThat(king.getNumberOfEye()).isEqualTo(2);


        JSONObject result = JSON.parse(JSON.stringify(king)).objectValue();
        assertThat(result.get("canNotStringify")).isEqualTo(null);
        assertThat(result.get("deathAge")).isEqualTo(null);
        assertThat(result.get("numberOfEye")).isEqualTo(null);
        assertThat(result.get("numberOfEar")).isEqualTo(2);
    }

}