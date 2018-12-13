package com.zhangguojian.json;

import com.zhangguojian.json.exception.CastException;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;

import static org.junit.Assert.*;

public class JSONPrimitiveTest {

    @Test(expected = CastException.class)
    public void testValidObject() throws CastException {
        JSONPrimitive.of(new HashMap<String,String>());
    }
    @Test
    public void testBoolean() {
        JSONPrimitive json = JSONPrimitive.of(Boolean.TRUE);
        assertTrue(json.isJSONPrimitive());
        assertTrue(json.isBoolean());
        assertTrue(json.getAsBoolean());

        json = JSONPrimitive.of(1);
        assertFalse(json.getAsBoolean());

        json = JSONPrimitive.of("true");
        assertFalse(json.isBoolean());
        assertTrue(json.getAsBoolean());

        json = JSONPrimitive.of("false");
        assertFalse(json.isBoolean());
        assertFalse(json.getAsBoolean());

        json = JSONPrimitive.of("1.3");
        assertFalse(json.getAsBoolean());

        json = JSONPrimitive.of(false);
        assertTrue(json.isBoolean());
        assertEquals("false", json.getAsString());
    }

    @Test
    public void testParsingStringAsNumber() {
        JSONPrimitive json = JSONPrimitive.of("1");
        assertFalse(json.isNumber());
        assertEquals(1D, json.getAsDouble(), 0.0001);
        assertEquals(1F, json.getAsFloat(), 0.0001);
        assertEquals(1, json.getAsInt());
        assertEquals(1L, json.getAsLong());
        assertEquals((short) 1, json.getAsShort());
        assertEquals((byte) 1, json.getAsByte());
        assertEquals(new BigInteger("1"), json.getAsBigInteger());
        assertEquals(new BigDecimal("1"), json.getAsBigDecimal());
    }

    @Test
    public void testStringsAndChar() {
        JSONPrimitive json = JSONPrimitive.of("abc");

        assertEquals('a', json.getAsCharacter());
        assertEquals("abc", json.getAsString());

        json = JSONPrimitive.of('z');
        assertTrue(json.isString());
        assertTrue(json.isCharacter());
        assertEquals('z', json.getAsCharacter());
    }

    @Test
    public void testExponential() {
        JSONPrimitive json = JSONPrimitive.of("1E+7");

        assertEquals(new BigDecimal("1E+7"), json.getAsBigDecimal());
        assertEquals(new Double("1E+7"), json.getAsDouble(), 0.00001);
        assertEquals(new Float("1E+7"), json.getAsDouble(), 0.00001);
        assertEquals(JSONPrimitive.of(1E+7).hashCode(), JSONPrimitive.of(1E+7f).hashCode());
        assertEquals(JSONPrimitive.of(1E+7).hashCode(), JSONPrimitive.of(new BigDecimal("1E+7")).hashCode());

        assertTrue(JSONPrimitive.of(1E+7D).isDouble());
        assertTrue(JSONPrimitive.of(1E+7f).isFloat());
        assertTrue(JSONPrimitive.of(new BigDecimal("1E+7")).isBigDecimal());


        try {
            json.getAsInt();
            fail("Integers can not handle exponents like this.");
        } catch (NumberFormatException expected) {
        }
    }

    @Test
    public void testByteEqualsShort() {
        JSONPrimitive p1 = JSONPrimitive.of((byte) 10);
        JSONPrimitive p2 = JSONPrimitive.of((short) 10);
        assertTrue(p1.isByte());
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    public void testIntEqualsShort() {
        JSONPrimitive p1 = JSONPrimitive.of(10);
        JSONPrimitive p2 = JSONPrimitive.of((short) 10);
        assertTrue(p1.isInt());
        assertTrue(p2.isShort());
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    public void testLongEqualsShort() {
        JSONPrimitive p1 = JSONPrimitive.of(10L);
        JSONPrimitive p2 = JSONPrimitive.of((short) 10);
        assertTrue(p1.isLong());
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    public void testBigIntEqualsShort() {
        JSONPrimitive p1 = JSONPrimitive.of(new BigInteger("10"));
        assertTrue(p1.isBigInteger());
        JSONPrimitive p2 = JSONPrimitive.of((short) 10);
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    public void testEqual() {
        assertEquals(JSONPrimitive.of(Float.NaN), JSONPrimitive.of(Double.NaN));
        assertEquals(JSONPrimitive.of("abc"), JSONPrimitive.of("abc"));
        String a = null, b = null;
        assertEquals(JSONPrimitive.of(a), JSONPrimitive.of(b));
        a = "abc";
        b = "abc";
        assertEquals(JSONPrimitive.of(a), JSONPrimitive.of(b));
        assertFalse(JSONPrimitive.of(a).equals("abc"));
        assertFalse(JSONPrimitive.of(123).equals(JSONPrimitive.of("123")));
        assertFalse(JSONPrimitive.of(123).equals(JSONPrimitive.of("123").getAsJSONPrimitive()));


    }

    @Test
    public void testHashCode(){
        String a = null;
        assertEquals(31, JSONPrimitive.of(a).hashCode());
        a = "abc";
        assertEquals(96354, JSONPrimitive.of(a).hashCode());
    }


    @Test
    public void testToString(){
        assertEquals("31", JSONPrimitive.of(31).toString());

    }

    @Test
    public void testDeepCopy() {
        JSONPrimitive a = JSONPrimitive.of("a");
        assertSame(a, a.deepCopy()); // Primitives are immutable!
    }
}