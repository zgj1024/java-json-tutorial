package com.zhangguojian.json;

import com.zhangguojian.json.bean.Person;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONTest {
    @Test
    public void parse() throws Exception {
        Object object = JSON.parse("{\"name\":\"张三\",\"age\":18,\"girlfriends\":[{\"name\":\"LiLy\"},{\"name\":\"Sandy\"}]}");
        HashMap<String,Object> oldBoy = (HashMap<String, Object>) object;
        Assert.assertEquals("张三",oldBoy.get("name"));
        List<HashMap<String,Object>> girlfriends = (List<HashMap<String, Object>>) oldBoy.get("girlfriends");
        Assert.assertEquals("LiLy",girlfriends.get(0).get("name"));
    }

    @Test
    public void stringify() throws Exception {
        Assert.assertEquals("true",JSON.stringify(true));

        Assert.assertEquals("1234",JSON.stringify(BigInteger.valueOf(1234L)));

        Assert.assertEquals("\"LiLy\"",JSON.stringify("LiLy"));

        String nullStr =null;
        Assert.assertEquals("null",JSON.stringify(nullStr));

        Assert.assertEquals("你好世界","\u4f60\u597d\u4e16\u754c");

        Assert.assertEquals("\"你好世界\"",JSON.stringify("\u4f60\u597d\u4e16\u754c"));
        Assert.assertEquals("\"\\t你好\\n\"",JSON.stringify("\t你好\n"));


        List<Integer> intList = Arrays.asList(1,2,3,4,5);
        Assert.assertEquals("[1,2,3,4,5]",JSON.stringify(intList));

        Integer aList[]={1,2,3,4,5};
        Assert.assertEquals("[1,2,3,4,5]",JSON.stringify(aList));

        int array[]={1,2,3,4,5};
        Assert.assertEquals("[1,2,3,4,5]",JSON.stringify(array));

        List<Integer> nullList = null;
        Assert.assertEquals("null",JSON.stringify(nullList));

        Map<String,Object> personMap = new HashMap<>();
        personMap.put("name","张三");
        Assert.assertEquals("{\"name\":\"张三\"}",JSON.stringify(personMap));

        Person person = new Person();
        person.setName("张三");
        Assert.assertEquals("{\"name\":\"张三\"}",JSON.stringify(person));
    }

}