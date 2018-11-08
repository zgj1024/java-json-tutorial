package com.zhangguojian.json;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


/**
 * 可以参考 http://gafter.blogspot.com/2006/12/super-type-tokens.html 这篇文章
 * @param <T>
 */
public abstract class TypeReference<T> implements Comparable<TypeReference<T>> {

    private final Type _type;

    public TypeReference(Type _type) {
        this._type = _type;
    }

    protected TypeReference() {
        //获取带有泛型的父类
        Type superClass = this.getClass().getGenericSuperclass();
        if (superClass instanceof Class) {
            throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
        } else {
            this._type = ((ParameterizedType)superClass).getActualTypeArguments()[0];
        }
    }

    public Type getType() {
        return this._type;
    }

    @Override
    public int compareTo(TypeReference<T> o) {
        return 0;
    }
}
