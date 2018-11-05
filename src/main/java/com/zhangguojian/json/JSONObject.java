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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class JSONObject<K, V> extends HashMap<K, V> {

    public static final JSONObject<String,Object> EMPTY = new JSONObject<>();

    public static JSONObject fromObject(String input) throws JSONException {
        return new Parser(input).parse().objectValue();
    }

    public static <K,V> JSONObject<String,Object> fromObject(Map<K,V> map) throws JSONException {
        if (map == null) {
           return new JSONObject<>();
        } else {
            JSONObject<String,Object> jsonObject = new JSONObject<>(map.size());
            for (final Entry<K, V> e : map.entrySet()) {
                if(e.getKey() == null) {
                    throw new NullException("Null key.");
                }
                final Object value = e.getValue();
                if (value != null) {
                    jsonObject.put(String.valueOf(e.getKey()), JSON.wrap(value));
                }
            }
            return jsonObject;
        }
    }

    public static JSONObject<String,Object> fromObject(Object bean) throws JSONException {
        Class<?> cls = bean.getClass();

        JSONObject<String,Object> jsonObject = new JSONObject<>();


        boolean includeSuperClass = cls.getClassLoader() != null;
        //如果不是 Integer 之类的原生类型，拿父类及自己所有的函数
        Method[] methods = includeSuperClass ? cls.getMethods() : cls.getDeclaredMethods();
        for (final Method method : methods) {
            final int modifiers = method.getModifiers();
            if (Modifier.isPublic(modifiers)
                    && !Modifier.isStatic(modifiers)
                    && method.getParameterTypes().length == 0
                    && !method.isBridge()
                    && method.getReturnType() != Void.TYPE
                    && isValidMethodName(method.getName())) {
                //用方法名做为 key，比如 getName 会返回 name
                final String key = getKeyNameFromMethod(method);
                if (key != null && !key.isEmpty()) {
                    try {
                        final Object result = method.invoke(bean);
                        if (result != null) {
                            jsonObject.put(key, JSON.wrap(result));
                            // we don't use the result anywhere outside asList wrap
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

    public JSONObject(int size){
        super(size);
    }

    public JSONObject(){
        super();
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
        return JSON.stringify(this);
    }
}
