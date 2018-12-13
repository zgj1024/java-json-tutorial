package com.zhangguojian.json;

import com.zhangguojian.json.exception.CastException;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;

public class JSONArrayTest {

    private final JSONArray expected = JSONArray.of(
            JSONPrimitive.of(true),
            JSONPrimitive.of(123),
            JSONPrimitive.of('c'),
            JSONPrimitive.of("123"),
            JSONNull.INSTANCE
    );

    @Test
    public void testConstruction(){
        Assert.assertEquals(JSONArray.EMPTY,JSONArray.of());
        Assert.assertEquals(3,JSONArray.of(JSONPrimitive.of(1),
                                                         JSONPrimitive.of(2),
                                                          JSONPrimitive.of(3)).size());

        Object[] objects  = null;
        Assert.assertEquals(JSONArray.EMPTY,JSONArray.of(objects));
        objects = new Integer[]{1,2,3};
        Assert.assertEquals(3,JSONArray.of(objects).size());
        Assert.assertEquals(JSONPrimitive.of(1),JSONArray.of(objects).get(0));


        try {
            Object arrayObj = null;
            Assert.assertEquals(JSONArray.EMPTY,JSONArray.of(arrayObj));
            arrayObj = new int[]{1,2,3};
            Assert.assertEquals(3,JSONArray.of(arrayObj).size());
        }catch (CastException e){
            e.printStackTrace();
        }

        List<Integer> arrayList = null;
        Assert.assertEquals(JSONArray.EMPTY,JSONArray.of(arrayList));
        Assert.assertEquals(3,JSONArray.of(Arrays.asList(1,2,3)).size());


    }

    @Test(expected = CastException.class)
    public void testConstructionWithNotArray() throws CastException {
        JSONArray.of(new HashMap<String,String>());
    }

    @Test
    public void testGetElement(){
        List<JSONElement> jsonElements = JSONArray.of(Arrays.asList(1,2,3)).getElements();
        assertEquals(JSONPrimitive.of(1),jsonElements.get(0));
    }


    @Test
    public void testIsArray() {
        assertTrue(JSONArray.EMPTY.isJSONArray());
        assertEquals(JSONArray.EMPTY, JSONArray.EMPTY.getAsJSONArray());
    }

    @Test
    public void testAdd() {
        JSONArray array = new JSONArray();
        array.add(true);
        array.add(123);
        array.add('c');
        array.add("123");
        Number NULL = null;
        array.add(NULL);


        assertEquals(expected,array);

        array = new JSONArray();
        array.addAll(expected);
        assertEquals(expected,array);
    }

    @Test
    public void testGetSetRemoveSizeContain(){
        JSONArray array = new JSONArray();
        array.addAll(expected);

        JSONElement firstElement = array.set(0,JSONNull.INSTANCE);
        assertEquals(firstElement,JSONPrimitive.of(true));

        firstElement = array.get(0);
        assertEquals(firstElement,JSONNull.INSTANCE);

        firstElement = array.remove(0);
        assertEquals(firstElement,JSONNull.INSTANCE);

        assertTrue(array.remove(JSONPrimitive.of(123)));

        firstElement = array.get(0);
        assertEquals(JSONPrimitive.of('c'),firstElement);

        assertTrue(array.contains(firstElement));

        int size = array.size();
        assertEquals(3,size);
    }

    @Test
    public void testIterator(){
        Iterator<JSONElement> iterator = expected.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(expected.get(0),iterator.next());
        assertEquals(expected.get(1),iterator.next());
        assertEquals(expected.get(2),iterator.next());
        assertEquals(expected.get(3),iterator.next());
        assertEquals(expected.get(4),iterator.next());
    }

    @Test
    public void testGetAs() throws CastException {

        assertEquals(true,JSONArray.of(JSONPrimitive.of(true)).getAsBoolean());
        assertThatThrownBy(expected::getAsBoolean)
                .isInstanceOf(IllegalStateException.class);


        assertEquals('c',JSONArray.of(JSONPrimitive.of('c')).getAsCharacter());
        assertThatThrownBy(expected::getAsCharacter)
                .isInstanceOf(IllegalStateException.class);

        assertEquals("123123",JSONArray.of(JSONPrimitive.of("123123")).getAsString());
        assertThatThrownBy(expected::getAsString)
                .isInstanceOf(IllegalStateException.class);

        assertEquals((byte) 1,JSONArray.of(JSONPrimitive.of(1)).getAsByte());
        assertThatThrownBy(expected::getAsByte)
                .isInstanceOf(IllegalStateException.class);

        assertEquals((short)1,JSONArray.of(JSONPrimitive.of(1)).getAsShort());
        assertThatThrownBy(expected::getAsShort)
                .isInstanceOf(IllegalStateException.class);

        assertEquals(123,JSONArray.of(JSONPrimitive.of(123)).getAsInt());
        assertThatThrownBy(expected::getAsInt)
                .isInstanceOf(IllegalStateException.class);

        assertEquals(123L,JSONArray.of(JSONPrimitive.of(123)).getAsLong());
        assertThatThrownBy(expected::getAsLong)
                .isInstanceOf(IllegalStateException.class);

        assertEquals(BigInteger.valueOf(123L),JSONArray.of(JSONPrimitive.of(123)).getAsBigInteger());
        assertThatThrownBy(expected::getAsBigInteger)
                .isInstanceOf(IllegalStateException.class);

        assertEquals(123F,JSONArray.of(JSONPrimitive.of(123)).getAsFloat(),0.0001);
        assertThatThrownBy(expected::getAsFloat)
                .isInstanceOf(IllegalStateException.class);

        assertEquals(123,JSONArray.of(JSONPrimitive.of(123)).getAsDouble(),0.0001);
        assertThatThrownBy(expected::getAsDouble)
                .isInstanceOf(IllegalStateException.class);

        assertEquals(BigDecimal.valueOf(123.0),JSONArray.of(JSONPrimitive.of(123.0)).getAsBigDecimal());
        assertThatThrownBy(expected::getAsBigDecimal)
                .isInstanceOf(IllegalStateException.class);

        assertEquals(123.0D,JSONArray.of(JSONPrimitive.of(123.0)).getAsNumber());
        assertThatThrownBy(expected::getAsNumber)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void testHashCode(){
        assertEquals(1616636717,expected.hashCode());
    }

    @Test
    public void testDeepCopy(){
        JSONArray New = new JSONArray();

        JSONArray array = new JSONArray();
        array.addAll(expected);
        array.add(New);

        JSONArray copy = array.deepCopy();
        assertEquals(array,copy);

        New.add(JSONNull.INSTANCE);
        assertNotEquals(array,copy);
    }


    @Test
    public void testStringify() throws CastException {
        Assert.assertEquals("[1,2,3]",JSONArray.of(new int[]{1,2,3}).stringify());
        Assert.assertEquals("[1,2,3]",JSONArray.of(new int[]{1,2,3}).toString());

    }
}
