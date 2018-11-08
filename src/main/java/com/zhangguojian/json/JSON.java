package com.zhangguojian.json;

import com.zhangguojian.json.exception.CastException;
import com.zhangguojian.json.exception.JSONException;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.*;

public class JSON {

    public static JSONValue parse(String input) throws JSONException {
        return new Parser(input).parse();
    }

    public static <T> T parse(String input, Class<T> cls) throws IllegalAccessException, JSONException, InstantiationException, InvocationTargetException {
        return parse(input, cls, new JSONContext());
    }


    public static <T> T parse(String input, Class<T> cls, JSONContext context) throws JSONException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Object jsonResult = new Parser(input).value();
        if (jsonResult == null) {
            return null;
        }
        return bind(jsonResult, cls, context);
    }

    public static <T> T parse(String input, TypeReference valueTypeRef, JSONContext context) throws JSONException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Object jsonResult = new Parser(input).value();
        if (jsonResult == null) {
            return null;
        }
        if(!(jsonResult instanceof JSONArray)){
            throw new CastException(jsonResult.getClass() + " can not cast to " +  valueTypeRef.getType());
        }
        return bind((JSONArray)jsonResult, valueTypeRef, context);
    }

    public static  <T> T bind(JSONArray array, TypeReference valueTypeRef,JSONContext context) throws InvocationTargetException, CastException, InstantiationException, IllegalAccessException {
        Type type = valueTypeRef.getType();
        if(type instanceof Class){
            Class<T> cls = (Class<T>) type;
            return bind(array, cls,context);
        }else if(type instanceof ParameterizedType){
            Class<T> cls= (Class<T>) ((ParameterizedType) type).getRawType();

            if ((cls.isInterface() || Modifier.isAbstract(cls.getModifiers())) && context.implMap.get(cls) == null) {
                throw new CastException(cls +" is interface or abstract or implements Class not find ,"+ array.getClass() + " can not cast to " + cls);
            }

            if(!(Collection.class.isAssignableFrom(cls))){
                throw new CastException(array.getClass() + " can not cast to " + cls.getClass());
            }
            Class<T> impl = cls;
            if (context.implMap.get(cls) != null) {
                impl = context.implMap.get(cls);
            }

            Collection collection = (Collection) impl.newInstance();

            Type type2 = ((ParameterizedType) type).getActualTypeArguments()[0];

            for(Object o : array){
                collection.add(bind(o, (Class<T>) type2,context));
            }
            return (T) collection;
        }
        throw new CastException(array.getClass() + " can not cast to " + type);

    }

    @SuppressWarnings(value={"unchecked", "rawtypes"})
    public static <T> T bind(Object jsonResult, Class<T> cls, JSONContext context) throws IllegalAccessException, InstantiationException, CastException, InvocationTargetException {

        //JSONArray 是 List 的一个实例，所以， cls 为 List 的时候，会直接返回 JSONArray
        if (cls.isInstance(jsonResult) && context.implMap.get(cls) == null) {//类型相等
            return (T) jsonResult;
        }

        if (jsonResult instanceof Boolean && (cls == Boolean.class ||cls == boolean.class)) {
            Boolean t = (Boolean) jsonResult;
            return (T) t;
        }

        if (jsonResult instanceof Number) {//number类型
            if (cls == Byte.class || cls == byte.class) {
                Byte number = ((Number) jsonResult).byteValue();
                return (T) number;
            } else if (cls == Short.class || cls == short.class) {
                Short number = ((Number) jsonResult).shortValue();
                return (T) number;
            } else if (cls == Integer.class || cls == int.class) {
                Integer number = ((Number) jsonResult).intValue();
                return (T) number;
            } else if (cls == Long.class || cls == long.class) {
                Long number = ((Number) jsonResult).longValue();
                return (T) number;
            } else if (cls == BigInteger.class) {
                BigInteger number = BigInteger.valueOf(((Number) jsonResult).longValue());
                return (T) number;
            } else if (cls == Float.class || cls == float.class) {
                Float number = ((Number) jsonResult).floatValue();
                return (T) number;
            } else if (cls == Double.class || cls == double.class) {
                Double number = ((Number) jsonResult).doubleValue();
                return (T) number;
            } else if (cls == BigDecimal.class) {
                BigDecimal number = BigDecimal.valueOf(((Number) jsonResult).doubleValue());
                return (T) number;
            }
        }


        //这种方式 泛型不够 泛型
        //如果是实现了 Collection的接口的具体类，比如 LinkedList等
        if (Collection.class.isAssignableFrom(cls) && jsonResult instanceof JSONArray) {
            Class CollectionImpl = context.implMap.get(cls);
            if (CollectionImpl == null) {
                CollectionImpl = cls;
            }
            Collection collection = (Collection) CollectionImpl.newInstance();
            JSONArray jsonArrayResult = (JSONArray) jsonResult;
            for (Object object : jsonArrayResult) {
                collection.add(bind(object, object.getClass(), context));
            }
            return (T) collection;
        }

        //Map type 也一样吧。
        if (Map.class.isAssignableFrom(cls) && jsonResult instanceof JSONObject) {
            JSONObject<String, Object> jsonObject = (JSONObject<String, Object>) jsonResult;
            Class mapImpl = context.implMap.get(cls);
            if (mapImpl == null) {
                mapImpl = cls;
            }

            Map map = (Map) mapImpl.newInstance();
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                Object value = entry.getValue();
                map.put(entry.getKey(), bind(entry.getValue(), value.getClass(), context));
            }
            return (T) map;
        }

        if (!(jsonResult instanceof JSONObject)) {//只支持 JSONObject 转成对象
            throw new CastException(jsonResult.getClass() + " can not cast to " + cls);
        }
        //普通的 Object 类，用反射
        return bindObj((JSONObject<String, Object>) jsonResult, cls, context);
    }

    private static <T> T bindObj(JSONObject<String, Object> jsonObject, Class<T> cls, JSONContext context) throws CastException, IllegalAccessException, InstantiationException, InvocationTargetException {
        //如果是抽象类 而且 没有实现类 抛出异常了
        if ((cls.isInterface() || Modifier.isAbstract(cls.getModifiers())) && context.implMap.get(cls) == null) {
            throw new CastException(jsonObject.getClass() + " can not cast to " + cls);
        }

        Class<T> impl = cls;
        if (context.implMap.get(cls) != null) {
            impl = context.implMap.get(cls);
        }
        T resultObject = impl.newInstance();

        List<Method> methodsList = ReflectUtils.getMethods(resultObject);
        for (Method method : methodsList) {
            final String key = ReflectUtils.getKeyNameFromSetMethod(method);
            if (key != null && !key.isEmpty()) {
                Object value = jsonObject.get(key);
                if (value == null) continue;
                //如果方法中有泛型，但只支持实现了 Collection 接口的泛型
                if(value instanceof JSONArray && method.getGenericParameterTypes().length == 1){
                    Object result = bind((JSONArray) value,new TypeReference(method.getGenericParameterTypes()[0]){}, context);
                    method.invoke(resultObject,result);
                }else {
                    method.invoke(resultObject,bind(value, value.getClass(), context));
                }
            }
        }

        return resultObject;
    }

    public static String stringify(Object o) {
        if (o == null) {
            return "null";
        }
        if (o instanceof String) {
            return stringify((String) (o));
        } else if (o instanceof Number || o instanceof Boolean) {
            return o.toString();
        } else if (o instanceof Collection) {
            return stringify((Collection) o);
        } else if (o.getClass().isArray()) {
            return stringifyWithArrayObject(o);
        } else if (o instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) o;
            return stringify(map);
        } else {
            return stringifySimpleObject(o);
        }
    }

    private static String stringify(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < str.length(); i++) {
            Character character = str.charAt(i);
            switch (character) {
                case '\t':
                    sb.append("\\t");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                case '\ufeff':
                    break;
                default:
                    if ((character >= '\u0080' && character < '\u00a0')
                            || (character >= '\u2000' && character < '\u2100')) {
                        String h = Integer.toHexString(character);
                        sb.append("\\u");
                        for (int j = 0; j < 4 - h.length(); j++) {
                            sb.append("0");
                        }
                        sb.append(h);
                    } else {
                        sb.append(character);
                    }
                    break;
            }
        }
        sb.append("\"");
        return sb.toString();
    }

    private static String stringify(Collection collection) {
        if (collection == null) {
            return "";
        } else if (collection.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (Object obj : collection) {
            sb.append(JSON.stringify(wrap(obj))).append(",");
        }
        sb.replace(sb.length() - 1, sb.length(), "]");
        return sb.toString();
    }

    private static String stringifyWithArrayObject(Object obj) {
        if (Array.getLength(obj) == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < Array.getLength(obj); i++) {
            sb.append(Array.get(obj, i)).append(",");
        }
        return sb.replace(sb.length() - 1, sb.length(), "]").toString();
    }

    private static String stringify(Map<?, ?> map) {
        if (map == null) {
            return "";
        } else if (map.isEmpty()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder("{");
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            sb.append(JSON.stringify(entry.getKey())).append(":").append(JSON.stringify(entry.getValue())).append(",");
        }
        sb.replace(sb.length() - 1, sb.length(), "}");
        return sb.toString();
    }

    private static String stringifySimpleObject(Object o) {
        assert o != null;
        //普通的 Object 对象
        try {
            StringBuilder sb = new StringBuilder("{");
            List<Method> methodList = ReflectUtils.getMethods(o);
            for (Method method : methodList) {
                final String key = ReflectUtils.getKeyNameFromGetMethod(method);
                if (key != null && !key.isEmpty()) {
                    Object result = method.invoke(o);
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
                        jsonSerialize.using().getTypeParameters();
                        CustomSerializer serializer = (CustomSerializer) Class.forName(className).newInstance();
                        result = serializer.serializeValue(result);
                    }
                    sb.append(stringify(key)).append(":").append(stringify(result)).append(",");
                }
            }
            return sb.replace(sb.length() - 1, sb.length(), "}").toString();
        } catch (IllegalAccessException | InvocationTargetException | ClassNotFoundException | InstantiationException | ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static Object wrap(Object object) {
        try {

            if (object instanceof JSONObject || object instanceof JSONArray

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

}
