package com.zhangguojian.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;

public interface JSONElement{
    JSONElement deepCopy();

    static JSONElement of(Object object) {
        try {
            if (object instanceof JSONObject || object instanceof JSONArray

                    || object instanceof Byte || object instanceof Character
                    || object instanceof Short || object instanceof Integer
                    || object instanceof Long || object instanceof Boolean
                    || object instanceof Float || object instanceof Double
                    || object instanceof String || object instanceof BigInteger
                    || object instanceof BigDecimal || object instanceof Enum) {
                return JSONPrimitive.of(object);
            }
            if (object instanceof Object[]) {
                Object[] coll = (Object[]) object;
                return JSONArray.of(coll);
            }
            if (object instanceof Collection) {
                Collection<?> coll = (Collection<?>) object;
                return JSONArray.of(coll);
            }
            if (object.getClass().isArray()) {
                return JSONArray.of(object);
            }
            if (object instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) object;
                return JSONObject.of(map);
            }

            return JSONObject.of(object);
        } catch (Exception exception) {
            return null;
        }
    }

    default boolean isJSONArray() {
        return false;
    }

    default boolean isJSONObject() {
        return false;
    }

    default boolean isJSONPrimitive() {
        return false;
    }

    default boolean isJSONNull() {
        return false;
    }

    default JSONArray getAsJSONArray() {
        throw new IllegalStateException("Not a JSON Array: " + this);
    }

    default JSONObject getAsJSONObject() {
        throw new IllegalStateException("Not a JSON Object: " + this);
    }

    default JSONPrimitive getAsJSONPrimitive() {
        throw new IllegalStateException("Not a JSON Primitive: " + this);
    }

    default JSONObject getAsJSONNull() {
        throw new IllegalStateException("Not a JSON Null: " + this);
    }

    default boolean getAsBoolean() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    default Number getAsNumber() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    default String getAsString() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    default double getAsDouble() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    default float getAsFloat() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    default long getAsLong() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    default int getAsInt() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    default short getAsShort() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    default byte getAsByte() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    default char getAsCharacter() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    default BigDecimal getAsBigDecimal() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    default BigInteger getAsBigInteger() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }


    String stringify();
}
