package com.zhangguojian.json;

import com.zhangguojian.json.bean.User;
import com.zhangguojian.json.exception.CastException;
import com.zhangguojian.json.exception.JSONException;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.zhangguojian.json.JSON.parse;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JSONTest {

    @Test
    public void testStringify(){
        List<Integer> array = Arrays.asList(1,2,3,4);
        Assert.assertEquals("[1,2,3,4]", JSON.stringify(array));
    }

    @Test
    public void testStringifyWithWriter(){
        List<Integer> array = Arrays.asList(1,2,3,4);
        Assert.assertEquals("[1,2,3,4]", JSON.stringify(array));
    }

    @Test
    public void testReadFile() throws IOException, JSONException {
        FileReader fileReader = new FileReader(JSONTest.class.getClassLoader().getResource("juejin-me.json").getPath());
        JSONElement jsonElement = parse(fileReader);
        fileReader.close();
        Assert.assertTrue(jsonElement.isJSONObject());
    }

    @Test
    public void testParseBindJSONElement() throws IllegalAccessException, ParseException, IOException, JSONException, InstantiationException, InvocationTargetException, ClassNotFoundException {
        Assert.assertEquals(null, parse("null",Object.class));
        Assert.assertEquals(true,JSON.parse("true",Boolean.class));
        Assert.assertEquals(false,JSON.parse("false",Boolean.class));

        Assert.assertEquals(Character.valueOf('f'),JSON.parse("\"f\"",Character.class));

        Assert.assertEquals("hello",JSON.parse("\"hello\"",String.class));

        Assert.assertEquals(Integer.valueOf(123),JSON.parse("123",Integer.class));
        Assert.assertEquals(123F,JSON.parse("123",Float.class),0.001f);



    }

    @Test
    public void testParseArray() throws IllegalAccessException, ParseException, IOException, JSONException, InstantiationException, InvocationTargetException, ClassNotFoundException {
        ArrayList array = new ArrayList();
        array.add(1);array.add(2);array.add(3);array.add(4);
        Assert.assertEquals(array.toString(), parse("[1,2,3,4]", ArrayList.class).toString());


        int expected[] = new int[]{1,2,3,4};
        int result[] = parse("[1,2,3,4]", int[].class);

        Assert.assertEquals(expected.length,result.length);
        Assert.assertEquals(expected[0],result[0]);
        Assert.assertEquals(expected[3],result[3]);

        Assert.assertEquals(array.toString(), parse("[1,2,3,4]", List.class).toString());

        assertThatThrownBy(() -> parse("[1,2,3,4]", LinkedList.class))
                .isInstanceOf(CastException.class);


    }

    @Test
    public void testParseWithGeneric() throws IllegalAccessException, ParseException, InstantiationException, JSONException, ClassNotFoundException, InvocationTargetException, IOException {
        LinkedList<BigInteger> list =  parse("[1,2,3,4]",new TypeReference<LinkedList<BigInteger>>(){});
        Assert.assertEquals(4,list.size() );
        Assert.assertEquals(BigInteger.valueOf(1L),list.get(0) );
        Assert.assertEquals(BigInteger.valueOf(4L),list.get(3) );

        LinkedList<List<BigInteger>> list2 =  parse("[[1,2,3,4]]",new TypeReference<LinkedList<LinkedList<BigInteger>>>(){});
        Assert.assertEquals(1,list2.size() );

        LinkedList<BigInteger[]> list3 =  parse("[[1,2,3,4]]",new TypeReference<LinkedList<BigInteger[]>>(){});
        Assert.assertEquals(1,list3.size() );


        JSONContext jsonContext = new JSONContext();
        jsonContext.putImplCls(List.class,LinkedList.class);
        List<BigInteger[]> list4 =  parse("[[1,2,3,4]]",new TypeReference<List<BigInteger[]>>(){},jsonContext);
        Assert.assertEquals(1,list4.size() );
        Assert.assertEquals(LinkedList.class,list4.getClass());
    }

    @Test
    public void testObject() throws IllegalAccessException, ParseException, IOException, JSONException, InstantiationException, InvocationTargetException, ClassNotFoundException {
        String json = "{\"name\":\"张三\",\"age\":24,\"birthday\":\"2008-08-08 08:08:00\"}";
        User user = parse(json, User.class);

        Assert.assertEquals(user.getName(),"张三");

        String jsonStr = "{\"birthday\":\"2007-1-28 12:23:00\"}";

        user = JSON.parse(jsonStr,User.class);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Assert.assertEquals(user.getBirthday(),sdf.parse("2007-1-28 12:23:00"));

    }

    @Test
    public void testMap() throws IllegalAccessException, ParseException, IOException, JSONException, InstantiationException, InvocationTargetException, ClassNotFoundException {

        String json = "{\"age1\":33,\"age2\":24}";

        Map<String,Integer> map =  parse(json,new TypeReference<HashMap<String,Integer>>(){});

        Assert.assertEquals(Integer.valueOf(33),map.get("age1"));
        Assert.assertEquals(Integer.valueOf(24),map.get("age2"));
    }

    @Test
    public void testAlias() throws IllegalAccessException, ParseException, IOException, JSONException, InstantiationException, InvocationTargetException, ClassNotFoundException {
        User user = parse("{\"alias\":\"lee\"}",User.class);
        Assert.assertEquals("lee",user.getAliasName());
    }
}