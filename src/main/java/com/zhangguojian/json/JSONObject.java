package com.zhangguojian.json;

import com.zhangguojian.json.exception.CastException;
import com.zhangguojian.json.exception.JSONException;
import com.zhangguojian.json.exception.NullException;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class JSONObject<K, V> extends HashMap<K, V> {

    public static final JSONObject<String,Object> EMPTY = new JSONObject<>();

    private static final class Null {
        @Override
        public boolean equals(Object object) {
            return object == null || object == this;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public String toString() {
            return "null";
        }
    }
    public static final Object NULL = new Null();

    public static JSONObject fromObject(String input) throws JSONException {
        return new Parser(input).parse().objectValue();
    }

    public static JSONObject fromObject(Map<?,?> map) throws JSONException {
        if (map == null) {
           return EMPTY;
        } else {
            JSONObject jsonObject = new JSONObject(map.size());
            for (final Entry<?, ?> e : map.entrySet()) {
                if(e.getKey() == null) {
                    throw new NullPointerException("Null key.");
                }
                final Object value = e.getValue();
                if (value != null) {
                    jsonObject.put(String.valueOf(e.getKey()), wrap(value));
                }
            }
            return jsonObject;
        }
    }

    public static JSONObject fromObject(Object bean) throws JSONException {
        Class<?> klass = bean.getClass();

        JSONObject jsonObject = new JSONObject();
        boolean includeSuperClass = klass.getClassLoader() != null;

        Method[] methods = includeSuperClass ? klass.getMethods() : klass.getDeclaredMethods();
        for (final Method method : methods) {
            final int modifiers = method.getModifiers();
            if (Modifier.isPublic(modifiers)
                    && !Modifier.isStatic(modifiers)
                    && method.getParameterTypes().length == 0
                    && !method.isBridge()
                    && method.getReturnType() != Void.TYPE
                    && isValidMethodName(method.getName())) {
                final String key = getKeyNameFromMethod(method);
                if (key != null && !key.isEmpty()) {
                    try {
                        final Object result = method.invoke(bean);
                        if (result != null) {
                            jsonObject.put(key, wrap(result));
                            // we don't use the result anywhere outside of wrap
                            // if it's a resource we should be sure to close it
                            // after calling toString
                            if (result instanceof Closeable) {
                                try {
                                    ((Closeable) result).close();
                                } catch (IOException ignore) {
                                }
                            }
                        }
                    } catch (IllegalAccessException ignore) {
                    } catch (IllegalArgumentException ignore) {
                    } catch (InvocationTargetException ignore) {
                    }
                }
            }
        }
        return jsonObject;
    }


    private static boolean isValidMethodName(String name) {
        return !"getClass".equals(name) && !"getDeclaringClass".equals(name);
    }

    private static String getKeyNameFromMethod(Method method) {
        String key;
        final String name = method.getName();
        if (name.startsWith("get") && name.length() > 3) {
            key = name.substring(3);
        } else if (name.startsWith("is") && name.length() > 2) {
            key = name.substring(2);
        } else {
            return null;
        }
        // if the first letter in the key is not uppercase, then skip.
        // This is to maintain backwards compatibility before PR406
        // (https://github.com/stleary/JSON-java/pull/406/)
        if (Character.isLowerCase(key.charAt(0))) {
            return null;
        }
        if (key.length() == 1) {
            key = key.toLowerCase(Locale.ROOT);
        } else if (!Character.isUpperCase(key.charAt(1))) {
            key = key.substring(0, 1).toLowerCase(Locale.ROOT) + key.substring(1);
        }
        return key;
    }

    public static Object wrap(Object object) {
        try {
            if (object == null) {
                return NULL;
            }
            if (object instanceof JSONObject || object instanceof JSONArray
                    || NULL.equals(object)
                    || object instanceof Byte || object instanceof Character
                    || object instanceof Short || object instanceof Integer
                    || object instanceof Long || object instanceof Boolean
                    || object instanceof Float || object instanceof Double
                    || object instanceof String || object instanceof BigInteger
                    || object instanceof BigDecimal || object instanceof Enum) {
                return object;
            }

            if (object instanceof Collection) {
                Collection<?> coll = (Collection<?>) object;
                return JSONArray.fromObject(coll);
            }
            if (object.getClass().isArray()) {
                return JSONArray.fromObject(object);
            }
            if (object instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) object;
                return JSONObject.fromObject(map);
            }

            return JSONObject.fromObject(object);
        } catch (Exception exception) {
            return null;
        }
    }
    public JSONObject(int size){
        super(size);
    }

    public JSONObject(){
        super();
    }

    public JSONObject(Map<? extends K, ? extends V> m) {
        super(m);
    }


    private <T> T castBase(String key, Class<T> classz) throws NullException, CastException {
        Object v = get(key);
        if (classz.isInstance(v)) {
            return (T) v;
        }
        if (v == null) {
            throw new NullException("JSONObject[" + key + "] is null");
        } else {
            throw new CastException("JSONObject[" + key + "] is not a " + classz + " , but is " + v.getClass());
        }
    }

    public boolean getBoolean(String key) throws NullException, CastException {
        return castBase(key, Boolean.class);
    }

    public String getString(String key) throws NullException, CastException {
        return castBase(key, String.class);
    }

    public byte getByte(String key) throws NullException, CastException {
        return castBase(key, Number.class).byteValue();
    }

    public short getShort(String key) throws NullException, CastException {
        return castBase(key, Number.class).shortValue();
    }

    public int getInt(String key) throws NullException, CastException {
        return castBase(key, Number.class).intValue();
    }

    public long getLong(String key) throws NullException, CastException {
        return castBase(key, Number.class).longValue();
    }

    public float getFloat(String key) throws NullException, CastException {
        return castBase(key, Number.class).floatValue();
    }

    public double getDouble(String key) throws NullException, CastException {
        return castBase(key, Number.class).doubleValue();
    }

    public BigInteger getBigInt(String key) throws NullException, CastException {
        Object o = get(key);
        if(o instanceof BigInteger){
            return (BigInteger) o;
        }
        return BigInteger.valueOf(castBase(key, Long.class));
    }

    public BigDecimal getBigDecimal(String key) throws NullException, CastException {
        Object o = get(key);
        if(o instanceof  BigDecimal){
            return (BigDecimal) o;
        }
        return new BigDecimal(castBase(key, Double.class));
    }

    public JSONObject getJSONObject(String key) throws NullException, CastException {
        return castBase(key, JSONObject.class);
    }

    public JSONArray getJSONArray(String key) throws NullException, CastException {
        return castBase(key, JSONArray.class);
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder("{");

        for (Map.Entry  entry : entrySet()) {
            try {
                sb.append(JSON.stringify(entry.getKey())).append(":").append(JSON.stringify(entry.getValue())).append(",");
            } catch (JSONException e) {
                e.printStackTrace();
                return "";
            }
        }

        sb.replace(sb.length() - 1, sb.length(), "}");
        return sb.toString();
    }
}
