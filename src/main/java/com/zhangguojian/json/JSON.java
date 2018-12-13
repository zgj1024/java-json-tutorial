package com.zhangguojian.json;

import com.zhangguojian.json.exception.CastException;
import com.zhangguojian.json.exception.JSONException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class JSON {

    public static JSONElement parse(String input) throws JSONException, IOException, InvocationTargetException, ClassNotFoundException, InstantiationException, ParseException, IllegalAccessException {
        return parse(new StringReader(input));
    }

    public static JSONElement parse(Reader input) throws JSONException, IOException {
        return new Parser(input).parse();
    }

    public static <T> T parse(Reader input, Class<T> cls) throws JSONException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, ParseException, InvocationTargetException {
        return parse(input, cls, new JSONContext());
    }

    public static <T> T parse(String input, Class<T> cls) throws JSONException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, ParseException, InvocationTargetException {
        return parse(new StringReader(input), cls, new JSONContext());
    }

    public static <T> T parse(String input, Class<T> cls, JSONContext context) throws IOException, JSONException, InvocationTargetException, ClassNotFoundException, InstantiationException, ParseException, IllegalAccessException {
        JSONElement jsonResult = new Parser(input).value();
        if (jsonResult == null) {
            return null;
        }
        return bind(jsonResult, cls, context);
    }

    public static <T> T parse(Reader input, Class<T> cls, JSONContext context) throws IOException, JSONException, InvocationTargetException, ClassNotFoundException, InstantiationException, ParseException, IllegalAccessException {
        JSONElement jsonResult = new Parser(input).value();
        if (jsonResult == null) {
            return null;
        }
        return bind(jsonResult, cls, context);
    }

    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    public static <T> T parse(String input, TypeReference valueTypeRef) throws IllegalAccessException, ParseException, InstantiationException, JSONException, IOException, InvocationTargetException, ClassNotFoundException {
        return parse(new StringReader(input), valueTypeRef, new JSONContext());
    }

    public static <T> T parse(String input, TypeReference valueTypeRef,JSONContext jsonContext) throws IllegalAccessException, ParseException, InstantiationException, JSONException, IOException, InvocationTargetException, ClassNotFoundException {
        return parse(new StringReader(input), valueTypeRef, jsonContext);
    }


    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    public static <T> T parse(Reader input, TypeReference valueTypeRef, JSONContext context) throws JSONException, IllegalAccessException, InstantiationException, InvocationTargetException, ParseException, ClassNotFoundException, IOException {
        JSONElement element = new Parser(input).value();
        return bind(element, valueTypeRef, context);
    }

    public static <T> T bind(JSONElement element, TypeReference valueTypeRef, JSONContext context) throws IllegalAccessException, CastException, ParseException, InstantiationException, InvocationTargetException, ClassNotFoundException {
        if (element == null || element.isJSONNull()) {
            return null;
        }
        Type type = valueTypeRef.getType();
        if (type instanceof Class) {
            Class<T> cls = (Class<T>) type;
            return bind(element, cls, context);
        }

        if (element instanceof JSONArray) {
            return bind(((JSONArray) element).getElements(), valueTypeRef, context);
        }

        if (element instanceof JSONObject) {
            return bind(((JSONObject) element).getMembers(), valueTypeRef, context);
        }

        throw new CastException(element.getClass() + " can not cast to " + valueTypeRef.getType() );
    }

    private static <T> T bind(Object obj, TypeReference valueTypeRef, JSONContext context) throws IllegalAccessException, CastException, ParseException, InstantiationException, InvocationTargetException, ClassNotFoundException {
        if (obj == null) {
            return null;
        }
        if (obj instanceof JSONElement) {
            JSONElement jsonElement = (JSONElement) obj;
            return bind(jsonElement, valueTypeRef, context);
        }
        Type type = valueTypeRef.getType();
        if (type instanceof Class) {
            Class<T> cls = (Class<T>) type;
            return bind(obj, cls, context);
        }

        if (obj instanceof Collection || obj.getClass().isArray()) {
            return bindArray(obj, valueTypeRef, context);
        }

        if (obj instanceof Map) {
            return bindObj((Map<?,?>)obj, valueTypeRef, context);
        }
        throw new CastException(obj.getClass() + " can not cast to " + valueTypeRef.getType() );

    }


    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    private static <T> T bind(JSONElement jsonResult, Class<T> cls, JSONContext context) throws IllegalAccessException, InstantiationException, CastException, InvocationTargetException, ParseException, ClassNotFoundException {
        if (jsonResult == null || jsonResult.isJSONNull()) {
            return null;
        }

        if (cls.isInstance(jsonResult) && context.getImplCls(cls) == null) {//类型相等
            return (T) jsonResult;
        }

        if (cls == String.class) {
            return (T) jsonResult.getAsString();
        }
        if (cls == Character.class || cls == char.class) {
            return (T) Character.valueOf(jsonResult.getAsCharacter());
        }

        if (jsonResult.isJSONPrimitive()) {
            JSONPrimitive jsonPrimitive = (JSONPrimitive) jsonResult;
            return bind(jsonPrimitive.getValue(), cls, context);
        }

        if (jsonResult.isJSONArray()) {
            JSONArray jsonArray = (JSONArray) jsonResult;
            return bind(jsonArray.getElements(), cls, context);
        }

        JSONObject jsonObject = (JSONObject) jsonResult;
        return bind(jsonObject.getMembers(), cls, context);
    }

    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    private static <T> T bind(Object result, Class<T> cls, JSONContext context) throws IllegalAccessException, InstantiationException, CastException, InvocationTargetException, ParseException, ClassNotFoundException {
        if (result == null) {
            return null;
        } else if (result instanceof JSONElement) {
            JSONElement jsonElement = (JSONElement) result;
            return bind(jsonElement, cls, context);
        } else if (cls.isInstance(result) && context.getImplCls(cls) == null) {//类型相等
            return (T) result;
        } else if (result instanceof Boolean && cls == Boolean.class || cls == boolean.class) {
            return (T) result;
        } else if (result instanceof Character && cls == Character.class || cls == char.class) {
            return (T) result;
        } else if (result instanceof Number) {
            Number number = (Number) result;
            if (cls == Byte.class || cls == byte.class) {
                return (T) Byte.valueOf(number.byteValue());
            } else if (cls == Short.class || cls == short.class) {
                return (T) Short.valueOf(number.shortValue());
            } else if (cls == Integer.class || cls == int.class) {
                return (T) Integer.valueOf(number.intValue());
            } else if (cls == Long.class || cls == long.class) {
                return (T) Long.valueOf(number.longValue());
            } else if (cls == BigInteger.class && number instanceof BigInteger) {
                return (T) number;
            } else if (cls == BigInteger.class) {
                return (T) BigInteger.valueOf(number.longValue());
            } else if (cls == Float.class || cls == float.class) {
                return (T) Float.valueOf(number.floatValue());
            } else if (cls == Double.class || cls == double.class) {
                return (T) Double.valueOf(number.doubleValue());
            } else if (cls == BigDecimal.class) {
                return (T) BigDecimal.valueOf(((Number) result).doubleValue());
            }
        }

        Class implCls = cls;
        if (context.getImplCls(implCls) != null) {
            implCls = context.getImplCls(implCls);
        }

        if ((isArray(cls) && isArray(result.getClass()))) {
            return bindArray(result, cls, context);
        }

        if (Map.class.isAssignableFrom(implCls)) {
            throw new CastException(result.getClass() + " can not cast to " + cls + " please use TypeReference");
        }


        return (T) bindObj((Map<?, ?>) result, implCls, context);

    }

    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    private static <T> T bindArray(Object result, Class<T> cls, JSONContext context) throws IllegalAccessException, CastException, ParseException, InstantiationException, InvocationTargetException, ClassNotFoundException {
        assert isArray(result.getClass()) && isArray(cls);

        if (cls.isArray()) {
            if (result instanceof Collection) {
                Collection resultCollection = (Collection) result;
                int size = resultCollection.size();
                //Object array = cls.n
                int i = 0;
                Object bindArray = Array.newInstance(cls.getComponentType(), size);
                for (Object obj : resultCollection) {
                    // arrayList.
                    Array.set(bindArray, i, bind(obj, cls.getComponentType(), context));
                    i++;
                }
                return (T) bindArray;
            } else {
                int size = Array.getLength(result);
                Object bindArray = Array.newInstance(cls.getComponentType(), size);
                for (int i = 0; i < size; i++) {
                    Array.set(bindArray, i, bind(bindArray, cls.getComponentType(), context));
                }
                return (T) bindArray;
            }
        }

        throw new CastException(result.getClass() + " can not cast to " + cls + " please use TypeReference");

    }

    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    /**
     * 将 map 和 object 绑定在一起
     * @param result
     */
    private static <T> T bindObj(Map<?, ?> jsonObject, Class<T> cls, JSONContext context) throws
            CastException, IllegalAccessException, InstantiationException, InvocationTargetException, ParseException, ClassNotFoundException {
        //如果是抽象类 而且 没有实现类 抛出异常了
        if ((cls.isInterface() || Modifier.isAbstract(cls.getModifiers())) && context.getImplCls(cls) == null) {
            throw new CastException(jsonObject.getClass() + " can not cast to " + cls);
        }

        Class<T> impl = cls;
        if (context.getImplCls(cls) != null) {
            impl = context.getImplCls(cls);
        }
        T resultObject = impl.newInstance();

        List<Method> methodsList = ReflectUtils.getMethods(resultObject);
        for (Method method : methodsList) {
            final String key = ReflectUtils.getKeyNameFromSetMethod(method);
            if (key != null && !key.isEmpty()) {
                Object value = jsonObject.get(key);
                Object result = null;
                if (value == null) continue;
                //如果方法中有泛型
                // ，但只支持实现了 Collection 接口的泛型

                //如果有 JSONSerialize 的注解，最终得到会是已经被处理过的结果。
                JSONDeserialize jsonDeserialize = ReflectUtils.getAnnotation(method, JSONDeserialize.class);
                if (jsonDeserialize != null) {
                    //获取注解中的using 类
                    String className = jsonDeserialize.using().getName();

                    //get class Name
                    CustomDeserializer deserializer = (CustomDeserializer) Class.forName(className).newInstance();

                    result = deserializer.deserialize(value);
                } else {
                    Type type = method.getParameterTypes()[0];
                    result = bind(value, new TypeReference(type) {}, context);
                }

                method.invoke(resultObject, result);
            }
        }

        return resultObject;
    }


    private static boolean isImplClass(Class cls) {
        if (cls.isInterface() || Modifier.isAbstract(cls.getModifiers())) {
            return false;
        }
        return true;
    }


    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    private static <T> T bindArray(Object array, TypeReference valueTypeRef, JSONContext context) throws InvocationTargetException, CastException, InstantiationException, IllegalAccessException, ParseException, ClassNotFoundException {
        Type type = valueTypeRef.getType();
        if (type instanceof Class) {
            Class<T> cls = (Class<T>) type;
            return bind(array, cls, context);
        } else if (type instanceof ParameterizedType) {
            Class<T> cls = (Class<T>) ((ParameterizedType) type).getRawType();

            if (!isImplClass(cls) && context.getImplCls(cls) == null) {
                throw new CastException(cls + " is interface or abstract or implements Class not find ," + array.getClass() + " can not cast to " + cls);
            }

            if (!(Collection.class.isAssignableFrom(cls))) {
                throw new CastException(array.getClass() + " can not cast to " + cls.getClass());
            }
            Class<T> impl = cls;
            if (context.getImplCls(cls) != null) {
                impl = context.getImplCls(cls);
            }

            Collection resultCollection = (Collection) impl.newInstance();

            Type type2 = ((ParameterizedType) type).getActualTypeArguments()[0];

            if (Collection.class.isAssignableFrom(array.getClass())) {
                Collection arrayCollection = (Collection) array;
                for (Object o : arrayCollection) {
                    if (type2 instanceof ParameterizedType) {
                        resultCollection.add(bind(o, new TypeReference(type2) {
                        }, context));
                    } else {
                        resultCollection.add(bind(o, (Class<T>) type2, context));
                    }
                }
                return (T) resultCollection;
            }
            if (array.getClass().isArray()) {
                int size = Array.getLength(array);
                for (int i = 0; i < size; i++) {
                    resultCollection.add(bind(Array.get(array, i), (Class<T>) type2, context));
                }
            }

            return (T) resultCollection;
        }
        throw new CastException(array.getClass() + " can not cast to " + type);

    }

    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    private static <T> T bindObj(Map<?,?> map, TypeReference valueTypeRef, JSONContext context) throws InvocationTargetException, CastException, InstantiationException, IllegalAccessException, ParseException, ClassNotFoundException {
        Type type = valueTypeRef.getType();
        if (type instanceof Class) {
            Class<T> cls = (Class<T>) type;
            return bind(map, cls, context);
        } else if (type instanceof ParameterizedType) {
            Class<T> cls = (Class<T>) ((ParameterizedType) type).getRawType();

            if (!isImplClass(cls) && context.getImplCls(cls) == null) {
                throw new CastException(cls + " is interface or abstract or implements Class not find ," + cls.getClass() + " can not cast to " + cls);
            }

            if (!(Map.class.isAssignableFrom(cls))) {
                throw new CastException(map.getClass() + " can not cast to " + cls.getClass());
            }
            Class<T> impl = cls;
            if (context.getImplCls(cls) != null) {
                impl = context.getImplCls(cls);
            }

            Type valueType = ((ParameterizedType) type).getActualTypeArguments()[1];

            Map resultMap = (Map) impl.newInstance();
            for(Map.Entry<?,?> member : map.entrySet()){

                String key = member.getKey().toString();


                if(valueType instanceof Class){
                    resultMap.put(key, bind(member.getValue(),
                            (Class<?>) valueType, context));
                }else {
                    resultMap.put(key, bind(member.getValue(),
                            new TypeReference(valueType) {}, context));
                }
            }

            return (T) resultMap;
        }
        throw new CastException(map.getClass() + " can not cast to " + type);
    }

    @SuppressWarnings("uncheck")
    private static boolean isArray(Class cls) {
        if (cls.isArray() || Collection.class.isAssignableFrom(cls)) {
            return true;
        }
        return false;
    }

    public static String stringify(Object o) {
        return StringifyUtils.Stringify(o);
    }

    public static JSONWriter stringify(Object o, Writer out) throws IOException {
        JSONWriter jsonWriter = new JSONWriter(out);
        jsonWriter.value(o);
        return jsonWriter;
    }

}
