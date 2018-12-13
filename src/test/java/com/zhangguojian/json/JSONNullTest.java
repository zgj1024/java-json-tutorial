package com.zhangguojian.json;

import org.junit.Test;

import static org.junit.Assert.*;

public class JSONNullTest {

    @Test
    public void isJSONNull() {
        assertTrue(JSONNull.INSTANCE.isJSONNull());
    }

    @Test
    public void deepCopy() {
        assertEquals(JSONNull.INSTANCE, JSONNull.INSTANCE.deepCopy());
    }

    @Test
    public void testHashCode() {
        assertEquals(445884362,JSONNull.INSTANCE.hashCode());
    }

    @Test
    public void testIsNull() {
        assertEquals("null",JSONNull.INSTANCE.toString());
    }

    @Test
    public void equals() {
        assertEquals(JSONNull.INSTANCE, JSONNull.INSTANCE);
    }

}