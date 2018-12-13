package com.zhangguojian.json;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JSONContext {
    private Map<Class,Class> implMap = new ConcurrentHashMap<>();

    public Class getImplCls(Class cls){
        return implMap.get(cls);
    }

    public void putImplCls(Class cls,Class impl){
        implMap.put(cls,impl);
    }

}
