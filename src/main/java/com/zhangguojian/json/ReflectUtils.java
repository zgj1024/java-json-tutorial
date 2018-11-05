package com.zhangguojian.json;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReflectUtils {

    /**
     * javaBean 都有 get，set 方法的
     * Object 转成 Map<String,Object> 实际上是利用 get、set 方法，而不是获取 object 的字段值的
     * 而 Object 转成 Object ，不能确定每个字段值，所以会忽略泛型的方法
     * @param bean
     * @return
     */
    static List<Method> getMethodsForCovertMap(Object bean){
        List<Method> methodList = new ArrayList<>();
        Class<?> cls = bean.getClass();
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
                methodList.add(method);
            }
        }
        return methodList;
    }

    private static boolean isValidMethodName(String name) {
        return !"getClass".equals(name) && !"getDeclaringClass".equals(name);
    }


    /**
     * javaBean 方法对应的字段名
     * @param method
     * @return
     */
    public static String getKeyNameFromMethod(Method method) {
        String key;
        final String name = method.getName();
        if (name.startsWith("get") && name.length() > 3) {  //其他字段会是用 get 开头的
            key = name.substring(3);
        } else if (name.startsWith("is") && name.length() > 2) { //boolean 字段会是用 is开头的
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
}
