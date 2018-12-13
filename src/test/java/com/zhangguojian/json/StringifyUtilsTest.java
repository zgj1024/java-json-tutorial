package com.zhangguojian.json;

import com.zhangguojian.json.bean.User;
import com.zhangguojian.json.exception.JSONException;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;

public class StringifyUtilsTest {


    @Test
    public void testNum(){
        double a = 2.33;
        assertEquals("2.33",StringifyUtils.Stringify(a));

        float b = 2.33f;
        assertEquals("2.33",StringifyUtils.Stringify(b));

        int c = 1 ;
        assertEquals("1",StringifyUtils.Stringify(c));

        long d = 2 ;
        assertEquals("2",StringifyUtils.Stringify(d));

        final Double e = Double.NaN;
        assertThatThrownBy(()->StringifyUtils.Stringify(e)).isInstanceOf(IllegalArgumentException.class);

        final Float f = Float.NaN;
        assertThatThrownBy(()->StringifyUtils.Stringify(f)).isInstanceOf(IllegalArgumentException.class);

        Double g = null;
        assertEquals("null",StringifyUtils.Stringify(g));

        Object obj = 123;
        assertEquals("123",StringifyUtils.Stringify(obj));

    }

    @Test
    public void testString(){
        String Null = null;
        assertEquals("null",StringifyUtils.Stringify(Null));

        String a = "12321";
        assertEquals("\"12321\"",StringifyUtils.Stringify(a));

        assertEquals("\"\\\"\\/\\t\\n\\f\\b\\n\\r\\\\\"",StringifyUtils.Stringify("\"/\t\n\f\b\n\r\\"));
        assertEquals("\"你好世界\"",StringifyUtils.Stringify("\ufeff\u4f60\u597d\u4e16\u754c"));
        assertEquals("\"\\u0080\\u0081\"",StringifyUtils.Stringify("\u0080\u0081"));

        Object object = "你好";
        assertEquals("\"你好\"",StringifyUtils.Stringify(object));

    }

    @Test
    public void testBoolean(){
        Boolean Null = null;
        assertEquals("null",StringifyUtils.Stringify(Null));
        assertEquals("true",StringifyUtils.Stringify(true));
        assertEquals("false",StringifyUtils.Stringify(false));

        Object object = true;
        assertEquals("true",StringifyUtils.Stringify(object));
    }

    @Test
    public void testChar(){
        Character Null = null;
        assertEquals("null",StringifyUtils.Stringify(Null));
        assertEquals("ß",StringifyUtils.Stringify('ß'));

        Object object = 'ß';
        assertEquals("ß",StringifyUtils.Stringify(object));

    }

    @Test
    public void testArray(){
        Object[] objects = null;
        assertEquals("null",StringifyUtils.Stringify(objects));

        objects = new Object[]{};
        assertEquals("[]",StringifyUtils.Stringify(objects));

        objects = new Object[]{1,2L,3f,4d};
        assertEquals("[1,2,3.0,4.0]",StringifyUtils.Stringify(objects));

        Object object = objects;
        assertEquals("[1,2,3.0,4.0]",StringifyUtils.Stringify(object));

    }

    @Test
    public void testCollection(){
        List<Integer> array = null;
        assertEquals("null",StringifyUtils.Stringify(array));

        array = new LinkedList<>();
        assertEquals("[]",StringifyUtils.Stringify(array));

        array = Arrays.asList(1,2,3,4);
        assertEquals("[1,2,3,4]",StringifyUtils.Stringify(array));

        Object object = array;
        assertEquals("[1,2,3,4]",StringifyUtils.Stringify(object));
    }

    @Test
    public void testObjectArray(){
        int[] a = null;
        assertEquals("null",StringifyUtils.Stringify(a));

        a  = new int[]{};
        assertEquals("[]",StringifyUtils.Stringify(a));

        a = new int[]{1,2,3,4};
        assertEquals("[1,2,3,4]",StringifyUtils.Stringify(a));

        Object object = a;
        assertEquals("[1,2,3,4]",StringifyUtils.Stringify(object));
    }

    @Test
    public void testMap(){
        Map<String,Object> map = null;
        assertEquals("null",StringifyUtils.Stringify(map));
        map = new HashMap<>();

        assertEquals("{}",StringifyUtils.Stringify(map));

        map.put("name","张三");
        map.put("age",2);
        map.put(null,"null");
        assertEquals("{null:\"null\",\"name\":\"张三\",\"age\":2}",StringifyUtils.Stringify(map));

        Object object = map;
        assertEquals("{null:\"null\",\"name\":\"张三\",\"age\":2}",StringifyUtils.Stringify(object));
    }

    @Test
    public void testObj() throws ParseException, IllegalAccessException, IOException, JSONException, InstantiationException, InvocationTargetException, ClassNotFoundException {
        User user = null;
        assertEquals("null",StringifyUtils.Stringify(user));

        User son  = new User("son",1);
        //father is using JSONIgnore
        son.setFather(new User("father",32));
        son.setMother(new User("mother",32));
        String result = StringifyUtils.Stringify(son);
        User userResult = JSON.parse(result,User.class);
        assertNull(userResult.getFather());
        assertNotNull(userResult.getMother());

        //test Custom Serializer
        son  = new User();
        final  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        son.setBirthday(sdf.parse("2007-1-28 12:23:00"));

        result = StringifyUtils.Stringify(son);
        Assertions.assertThat(result).isIn("{\"birthday\":\"2007-01-28 12:23:00\",\"age\":0}","{\"age\":0,\"birthday\":\"2007-01-28 12:23:00\"}");

    }

    @Test
    public void testJSONEElement(){
        JSONElement jsonElement = JSONArray.of(JSONPrimitive.of(1),JSONPrimitive.of(2));
        assertEquals("[1,2]",StringifyUtils.Stringify(jsonElement));

        Object object = jsonElement;
        assertEquals("[1,2]",StringifyUtils.Stringify(object));
    }

    @Test
    public void testAlias(){
        User user = new User();
        user.setAliasName("chicken");
        Assertions.assertThat(StringifyUtils.Stringify(user)).isIn(
                "{\"age\":0,\"alias\":\"chicken\"}","{\"alias\":\"chicken\",\"age\":0}"
        );

    }
}