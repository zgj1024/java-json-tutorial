package com.zhangguojian.json;

import com.zhangguojian.json.bean.User;
import com.zhangguojian.json.exception.JSONException;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JSONWriterTest {

    @Test
    public void testConstructor() throws IOException {
        try {
            new JSONWriter(null);
        } catch (Exception e) {
            assertEquals(NullPointerException.class, e.getClass());
        }

        JSONWriter jsonWriter = new JSONWriter(new FileWriter("Hello.json"));
        jsonWriter.value(Arrays.asList(1, 2, 3, 4));
        jsonWriter.flush();
        jsonWriter.close();

        assertTrue(Files.isReadable(Paths.get("Hello.json")));

        List<String> lines = Files.readAllLines(Paths.get("Hello.json"));
        assertEquals("[1,2,3,4]", lines.get(0));
    }

    @Test
    public void nullValue() throws IOException {
        StringWriter stringWriter = new StringWriter();

        Object object = null;
        JSON.stringify(object, stringWriter);

        assertEquals("null", stringWriter.toString());
    }

    @Test
    public void boolValue() throws IOException {
        StringWriter stringWriter = new StringWriter();

        Boolean object = null;
        JSON.stringify(object, stringWriter);
        stringWriter.close();
        assertEquals("null", stringWriter.toString());

        stringWriter = new StringWriter();
        object = true;
        JSON.stringify(object, stringWriter);
        stringWriter.close();
        assertEquals("true", stringWriter.toString());
    }

    @Test
    public void charValue() throws IOException {
        StringWriter stringWriter = new StringWriter();

        Character object = null;
        JSON.stringify(object, stringWriter);

        assertEquals("null", stringWriter.toString());

        stringWriter = new StringWriter();
        object = '∫';
        JSON.stringify(object, stringWriter);
        assertEquals("∫", stringWriter.toString());
    }

    @Test
    public void numberValue() throws IOException {
        Object number = 123;

        StringWriter stringWriter = new StringWriter();
        JSON.stringify(number, stringWriter);
        stringWriter.close();
        assertEquals("123", stringWriter.toString());


        Double Null = null;
        stringWriter = new StringWriter();
        JSON.stringify(Null, stringWriter);
        stringWriter.close();
        assertEquals("null", stringWriter.toString());
    }

    @Test
    public void testArray() throws IOException {
        Object[] objects = null;
        StringWriter stringWriter = new StringWriter();
        JSON.stringify(objects, stringWriter);
        stringWriter.close();
        assertEquals("null", stringWriter.toString());

        objects = new Object[]{};
        stringWriter = new StringWriter();
        JSON.stringify(objects, stringWriter);
        stringWriter.close();
        assertEquals("[]", stringWriter.toString());

        objects = new Object[]{1, 2, 3, 4};
        stringWriter = new StringWriter();
        JSON.stringify(objects, stringWriter);
        stringWriter.close();
        assertEquals("[1,2,3,4]", stringWriter.toString());
    }

    @Test
    public void testCollection() throws IOException {
        Collection collection = null;
        StringWriter stringWriter = new StringWriter();
        JSON.stringify(collection, stringWriter);
        stringWriter.close();
        assertEquals("null", stringWriter.toString());

        collection = new LinkedList<>();
        stringWriter = new StringWriter();
        JSON.stringify(collection, stringWriter);
        stringWriter.close();
        assertEquals("[]", stringWriter.toString());

        collection = Arrays.asList(1, 2, 3, 4);
        stringWriter = new StringWriter();
        JSON.stringify(collection, stringWriter);
        stringWriter.close();
        assertEquals("[1,2,3,4]", stringWriter.toString());
    }

    @Test
    public void testObjectArray() throws IOException {
        int[] array = null;
        StringWriter stringWriter = new StringWriter();
        JSON.stringify(array, stringWriter);
        stringWriter.close();
        assertEquals("null", stringWriter.toString());

        array = new int[]{};
        stringWriter = new StringWriter();
        JSON.stringify(array, stringWriter);
        stringWriter.close();
        assertEquals("[]", stringWriter.toString());

        array = new int[]{1, 2, 3, 4};
        stringWriter = new StringWriter();
        JSON.stringify(array, stringWriter);
        stringWriter.close();
        assertEquals("[1,2,3,4]", stringWriter.toString());
    }

    @Test
    public void testMap() throws IOException {
        Map<String, Object> map = null;
        StringWriter stringWriter = new StringWriter();
        JSON.stringify(map, stringWriter);
        stringWriter.close();
        assertEquals("null", stringWriter.toString());

        map = new HashMap<>();
        stringWriter = new StringWriter();
        JSON.stringify(map, stringWriter);
        stringWriter.close();
        assertEquals("{}", stringWriter.toString());

        map.put("name", "张三");
        map.put("age", 24);
        stringWriter = new StringWriter();
        JSON.stringify(map, stringWriter);
        stringWriter.close();
        assertEquals("{\"name\":\"张三\",\"age\":24}", stringWriter.toString());
    }

    @Test
    public void testString() throws IOException {
        String Null = null;
        StringWriter stringWriter = new StringWriter();
        JSON.stringify(Null, stringWriter);
        stringWriter.close();
        assertEquals("null", stringWriter.toString());

        String str = "12321";
        stringWriter = new StringWriter();
        JSON.stringify(str, stringWriter);
        stringWriter.close();
        assertEquals("\"12321\"", stringWriter.toString());

        str = "\"/\t\n\f\b\n\r\\";
        stringWriter = new StringWriter();
        JSON.stringify(str, stringWriter);
        stringWriter.close();
        assertEquals("\"\\\"\\/\\t\\n\\f\\b\\n\\r\\\\\"", stringWriter.toString());

        str = "\ufeff\u4f60\u597d\u4e16\u754c";
        stringWriter = new StringWriter();
        JSON.stringify(str, stringWriter);
        stringWriter.close();
        assertEquals("\"你好世界\"", stringWriter.toString());

        str = "\u0080\u0081";
        stringWriter = new StringWriter();
        JSON.stringify(str, stringWriter);
        stringWriter.close();
        assertEquals("\"\\u0080\\u0081\"", stringWriter.toString());

        Object object = "你好";
        stringWriter = new StringWriter();
        JSON.stringify(object, stringWriter);
        stringWriter.close();
        assertEquals("\"你好\"", stringWriter.toString());
    }

    @Test
    public void testJSONElement() throws IOException {
        JSONElement element = JSONNull.INSTANCE;
        StringWriter stringWriter = new StringWriter();
        JSON.stringify(element, stringWriter);
        stringWriter.close();
        assertEquals("null", stringWriter.toString());

        element = JSONPrimitive.of(true);
        stringWriter = new StringWriter();
        JSON.stringify(element, stringWriter);
        stringWriter.close();
        assertEquals("true", stringWriter.toString());

        element = JSONArray.of(JSONPrimitive.of(1), JSONPrimitive.of(2), JSONPrimitive.of(3));
        stringWriter = new StringWriter();
        JSON.stringify(element, stringWriter);
        stringWriter.close();
        assertEquals("[1,2,3]", stringWriter.toString());

        Map<String, Object> map = new HashMap<>();
        map.put("name", "张三");
        element = JSONObject.of(map);
        stringWriter = new StringWriter();
        JSON.stringify(element, stringWriter);
        stringWriter.close();
        assertEquals("{\"name\":\"张三\"}", stringWriter.toString());
    }


    @Test
    public void testObj() throws IOException, ParseException, ClassNotFoundException, InvocationTargetException, InstantiationException, JSONException, IllegalAccessException {

        User user = null;
        StringWriter stringWriter = new StringWriter();
        JSON.stringify(user, stringWriter);
        stringWriter.close();
        assertEquals("null", StringifyUtils.Stringify(user));


        User son = new User("son", 1);
        //father is using JSONIgnore
        son.setFather(new User("father", 32));
        son.setMother(new User("mother", 32));

        stringWriter = new StringWriter();
        JSON.stringify(son, stringWriter);
        stringWriter.close();
        String result = stringWriter.toString();

        User userResult = JSON.parse(result,User.class);
        Assert.assertNull(userResult.getFather());
        Assert.assertNotNull(userResult.getMother());

        //test Custom Serializer

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        son = new User();
        son.setBirthday(sdf.parse("2007-1-28 12:23:00"));

        stringWriter = new StringWriter();
        JSON.stringify(son, stringWriter);
        stringWriter.close();
        result = stringWriter.toString();
        son.setBirthday(sdf.parse("2007-1-28 12:23:00"));
        Assertions.assertThat(result).isIn("{\"birthday\":\"2007-01-28 12:23:00\",\"age\":0}","{\"age\":0,\"birthday\":\"2007-01-28 12:23:00\"}");

    }
}