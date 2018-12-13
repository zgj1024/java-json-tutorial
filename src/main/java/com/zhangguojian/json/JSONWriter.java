package com.zhangguojian.json;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class JSONWriter implements Closeable, Flushable {

    private Writer out;

    public JSONWriter(Writer out) {
        if (out == null) {
            throw new NullPointerException("out can not null");
        }
        this.out = out;
    }

    public JSONWriter nullValue() throws IOException {
        out.write("null");
        return this;
    }

    public JSONWriter value(Object object) throws IOException {
        if (object == null) {
            return nullValue();
        } else if (object.getClass() == String.class) {
            return value((String) object);
        } else if (object instanceof JSONElement) {
            return value((JSONElement) object);
        } else if (object instanceof Number) {
            return value((Number) object);
        } else if (object instanceof Boolean) {
            return value((Boolean) object);
        } else if (object instanceof Character) {
            out.write((Character) object);
            return this;
        } else if (object instanceof Object[]) {
            return value((Object[]) object);
        } else if (object.getClass().isArray()) {
            return valueArrayObject(object);
        } else if (object instanceof Collection) {
            return value((Collection) object);
        } else if (object instanceof Map) {
            return value((Map) object);
        }
        return valueObject(object);
    }

    public JSONWriter value(Number number) throws IOException {
        if (number == null) {
            return nullValue();
        }
        out.write(StringifyUtils.Stringify(number));
        return this;
    }


    public JSONWriter value(Boolean bool) throws IOException {
        if (bool == null) {
            return nullValue();
        }

        if (bool) {
            out.write("true");
        } else {
            out.write("false");
        }
        return this;
    }

    public JSONWriter value(String str) throws IOException {
        if (str == null) {
            return nullValue();
        }
        out.write('"');
        for (int i = 0; i < str.length(); i++) {
            char character = str.charAt(i);
            switch (character) {
                case '\t':
                    out.write("\\t");
                    break;
                case '\n':
                    out.write("\\n");
                    break;
                case '\r':
                    out.write("\\r");
                    break;
                case '\f':
                    out.write("\\f");
                    break;
                case '\b':
                    out.write("\\b");
                    break;
                case '"':
                    out.write("\\\"");
                    break;
                case '\\':
                    out.write("\\\\");
                    break;
                case '/':
                    out.write("\\/");
                    break;
                case '\ufeff':
                    break;
                default:
                    if ((character >= '\u0080' && character < '\u00a0')
                            || (character >= '\u2000' && character < '\u2100')) {
                        String h = Integer.toHexString(character);
                        out.write("\\u");
                        for (int j = 0; j < 4 - h.length(); j++) {
                            out.write("0");
                        }
                        out.write(h);
                    } else {
                        out.write(character);
                    }
                    break;
            }
        }
        out.write("\"");
        return this;
    }

    public JSONWriter value(Object[] objects) throws IOException {
        if (objects == null) {
            return nullValue();
        } else if (objects.length == 0) {
            out.write("[]");
            return this;
        } else {
            out.write('[');
            int size = objects.length;
            int i = 0;
            for (Object object : objects) {
                value(object);
                i++;
                if (i != size) {
                    out.write(',');
                }
            }

            out.write(']');
        }
        return this;
    }

    public JSONWriter valueArrayObject(Object obj) throws IOException {
        assert obj.getClass().isArray();
        if (Array.getLength(obj) == 0) {
            out.write("[]");
            return this;
        }

        out.write('[');
        int size = Array.getLength(obj);
        for (int i = 0; i < size; i++) {
            value(Array.get(obj, i));
            if (i != size - 1) {
                out.write(',');
            }
        }
        out.write(']');
        return this;
    }

    public JSONWriter value(Collection collection) throws IOException {
        if (collection == null) {
            return nullValue();
        } else if (collection.isEmpty()) {
            out.write("[]");
            return this;
        }

        out.write('[');
        int size = collection.size();
        int i = 0;
        for (Object obj : collection) {
            value(obj);
            i++;
            if (i != size) {
                out.write(',');
            }
        }
        out.write(']');
        return this;
    }

    public JSONWriter value(Map<?, ?> map) throws IOException {
        if (map == null) {
            return nullValue();
        } else if (map.isEmpty()) {
            out.write("{}");
            return this;
        }

        int size = map.size();
        int i = 0;
        out.write('{');
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry.getKey() == null) {
                out.write("null");
            } else {
                value(entry.getKey().toString());
            }
            out.write(":");
            value(entry.getValue());
            i++;
            if (i != size) {
                out.write(',');
            }
        }
        out.write('}');
        return this;
    }

    public JSONWriter value(JSONElement jsonElement) throws IOException {
        if (jsonElement == null || jsonElement.isJSONNull()) {
            return nullValue();
        } else if (jsonElement.isJSONPrimitive()) {
            JSONPrimitive jsonPrimitive = (JSONPrimitive) jsonElement;
            return value(jsonPrimitive.getValue());
        } else if (jsonElement.isJSONArray()) {
            JSONArray jsonArray = (JSONArray) jsonElement;
            return value(jsonArray.getElements());
        } else {
            JSONObject jsonObject = (JSONObject) jsonElement;
            return value(jsonObject.getMembers());
        }
    }

    @SuppressWarnings("unchecked")
    private JSONWriter valueObject(Object object) {
        assert object != null;
        //普通的 Object 对象
        try {
            out.write('{');
            List<Method> methodList = ReflectUtils.getMethods(object);

            String lastkey = null;
            Object lastValue = null;
            for (Method method : methodList) {
                final String key = ReflectUtils.getKeyNameFromGetMethod(method);
                if (key != null && !key.isEmpty()) {
                    Object result = method.invoke(object);
                    if (result == null) {
                        continue;
                    }
                    //如果有 JSONSerialize 的注解，最终得到会是已经被处理过的结果。
                    JSONSerialize jsonSerialize = ReflectUtils.getAnnotation(method, JSONSerialize.class);
                    if (jsonSerialize != null) {
                        //获取注解中的using 类
                        String className = jsonSerialize.using().getName();
                        //get class Name
                        //获取 using 中的 泛型类。
                        CustomSerializer serializer = (CustomSerializer) Class.forName(className).newInstance();
                        result = serializer.serializeValue(result);
                    }
                    if (lastValue != null) {
                        value(lastkey);
                        out.write(':');
                        value(lastValue);
                        out.write(',');
                    }
                    lastkey = key;
                    lastValue = result;
                }
            }
            if (lastValue == null) {
                out.write('}');
            } else {
                value(lastkey);
                out.write(':');
                value(lastValue);
                out.write('}');
            }

            return this;
        } catch (IllegalAccessException | InvocationTargetException | ClassNotFoundException | InstantiationException | ParseException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}
