package com.zhangguojian.json;

import com.zhangguojian.json.bean.User;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;

public class JSONObjectTest {

    JSONObject jsonObject = new JSONObject();

    @Test
    public void testConstruction(){
        User user = new User("chen",23);
        JSONObject object = JSONObject.of(user);
        Assert.assertEquals(JSONPrimitive.of("chen"),object.get("name"));
        Assert.assertEquals(JSONPrimitive.of(23),object.get("age"));

        Map<String,Integer> map = new HashMap<>();
        map.put("lee",23);
        map.put("chen",25);
        object = JSONObject.of(map);
        Assert.assertEquals(JSONPrimitive.of(23),object.get("lee"));

    }
    @Test
    public void isJSONObject() {
        assertTrue(JSONObject.EMPTY.isJSONObject());
    }

    @Test
    public void getAsJSONObject() {
        assertEquals(JSONObject.EMPTY,JSONObject.EMPTY.getAsJSONObject());
    }

    @Test
    public void testAddingAndRemoveObjectProperties() {
        JSONObject jsonObj = new JSONObject();
        String propertyName = "property";
        assertFalse(jsonObj.has(propertyName));
        assertNull(jsonObject.get(propertyName));

        JSONPrimitive value = JSONPrimitive.of("abc");
        jsonObj.add(propertyName,value);
        assertEquals(value,jsonObj.get(propertyName));

        JSONElement jsonElement = jsonObj.remove(propertyName);
        assertEquals(value,jsonElement);
        assertFalse(jsonObj.has(propertyName));
        assertNull(jsonObject.get(propertyName));
    }

    @Test
    public void testAddingNullPropertyValue() throws Exception {
        String propertyName = "property";
        JSONObject jsonObj = new JSONObject();
        jsonObj.add(propertyName, null);

        assertTrue(jsonObj.has(propertyName));

        JSONElement jsonElement = jsonObj.get(propertyName);
        assertNotNull(jsonElement);
        assertTrue(jsonElement.isJSONNull());
    }

    @Test
    public void testAddMoreTypeElement(){
        JSONObject jsonObject = new JSONObject();

        //cant not use null as Key
        assertThatThrownBy(()->jsonObject.add(null,JSONNull.INSTANCE))
                .isInstanceOf(NullPointerException.class);

        jsonObject.add("",JSONNull.INSTANCE);
        assertEquals(JSONNull.INSTANCE,jsonObject.get(""));


        jsonObject.addProperty("num",123);
        jsonObject.addProperty("bool",true);
        jsonObject.addProperty("char",'c');
        jsonObject.addProperty("string","string");

        JSONArray array = JSONArray.of(JSONPrimitive.of(1),JSONPrimitive.of(2));
        jsonObject.add("array",array);

        jsonObject.add("obj",JSONObject.EMPTY);

        assertEquals(123,jsonObject.get("num").getAsNumber());
        assertEquals(true,jsonObject.get("bool").getAsBoolean());
        assertEquals('c',jsonObject.get("char").getAsCharacter());
        assertEquals("string",jsonObject.get("string").getAsString());
        assertEquals(JSONPrimitive.of("string"),jsonObject.getAsJsonPrimitive("string"));
        assertEquals(array,jsonObject.getAsJsonArray("array"));
        assertEquals(JSONObject.EMPTY,jsonObject.getAsJsonObject("obj"));
    }

    @Test
    public void testDeepCopyAndHashCode(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.addProperty("num",123);
        jsonObject.addProperty("bool",true);
        jsonObject.addProperty("char",'c');
        jsonObject.addProperty("string","string");
        JSONArray array = JSONArray.of(JSONPrimitive.of(1),JSONPrimitive.of(2));
        jsonObject.add("array",array);

        JSONObject copy = jsonObject.deepCopy();
        assertEquals(jsonObject,copy);
        assertEquals(jsonObject.hashCode(),copy.hashCode());

        array.add(JSONPrimitive.of(3));
        assertNotEquals(jsonObject,copy);
        assertNotEquals(jsonObject.hashCode(),copy.hashCode());
    }

    @Test
    public void testKeySet(){
        JSONObject a = new JSONObject();

        a.add("foo", new JSONArray());
        a.add("bar", new JSONObject());

        assertEquals(2, a.size());
        assertEquals(2, a.keySet().size());
        assertEquals(2, a.entrySet().size());
        assertTrue(a.keySet().contains("foo"));
        assertTrue(a.keySet().contains("bar"));
    }

    @Test
    public void testStringify(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.add("name",JSONPrimitive.of("张三"));
        Assert.assertEquals("{\"name\":\"张三\"}",jsonObject.stringify());
    }
}