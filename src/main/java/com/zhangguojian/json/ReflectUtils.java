package com.zhangguojian.json;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class ReflectUtils {

    /**
     * 用途比较窄，只适用于转换普通的 java Bean;
     *
     * @param bean
     * @return
     */
    public static Map<String, Object> ToMap(Object bean) throws InvocationTargetException, IllegalAccessException {
        Map<String, Object> res = new HashMap<>();
        List<Method> methods = getMethodsOfGet(bean);
        for (Method method : methods) {
            String key = getKeyOfGetMethod(method.getName());
            if(key == null )
                continue;
            Object object = method.invoke(bean);
            res.put(key,object);
        }

        return res;
    }


    private static String getKeyOfGetMethod(String methodName) {
        assert methodName != null;
        assert methodName.startsWith("get") || methodName.startsWith("is");

        String key;
        if (methodName.startsWith("get")) {
            key = methodName.substring(3);
        } else {
            key = methodName.substring(2);//is开头的
        }
        //开头不允许 key 首字母是小写，这不符号骆驼峰明命名法
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

    /**
     * 获取所有的 get 方法
     *
     * @return
     */
    static List<Method> getMethodsOfGet(Object object) {
        //的参数
        return getObjectMethods(object).stream()
                .filter(method -> method.getParameters().length == 0)// java Bean 的 方法是没有参数的
                .filter(method -> (method.getName().length() > 3 && method.getName().startsWith("get"))
                        || (method.getName().length() > 2 && method.getName().startsWith("is")))//是以 get 开头的或者 is 开头的
                .collect(Collectors.toList());
    }

    /**
     * 获取所有的 get、set 方法
     *
     * @param bean
     * @return
     */
    static List<Method> getObjectMethods(Object bean) {
        List<Method> methodList = new ArrayList<>();
        Class<?> cls = bean.getClass();
        boolean includeSuperClass = cls.getClassLoader() != null;
        //如果不是 Integer 之类的原生类型，拿父类及自己所有的函数
        Method[] methods = includeSuperClass ? cls.getMethods() : cls.getDeclaredMethods();
        for (final Method method : methods) {
            final int modifiers = method.getModifiers();
            if (Modifier.isPublic(modifiers)
                    && !Modifier.isStatic(modifiers)
                    && !method.isBridge()
                    && isValidMethodName(method.getName())) {
                methodList.add(method);
            }
        }
        return methodList;
    }

    private static boolean isValidMethodName(String name) {
        return !"getClass".equals(name) && !"getDeclaringClass".equals(name);
    }

}
