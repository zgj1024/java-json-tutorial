package com.zhangguojian.json;

import java.util.HashMap;
import java.util.Map;

public class JSONContext {
    public  Map<Class,Class> implMap = new HashMap<>();
    public  Map<Class,Class> aliasMap = new HashMap<>();


    public void addImpl(Class inf,Class impl){
        implMap.put(inf,impl);
    }
}
