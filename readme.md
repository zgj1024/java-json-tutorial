# stringify object to JSONStr

上几章的内容是，将 JSON字符串 转成 java对象。 而这部分的内容是，java对象 如何转成 JSON字符串。


这东西说简单还挺简单，就是有点烦。

## Boolean、Null、Num
如果是 Boolean 、NUll、或者是 Num 的还是挺容易的。直接调用 `toString()` 就可以了。很容易写出这样的代码
```java
public static String stringify(Object obj) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
	if(obj == null){
    	return "null";
    } else if(obj.getClass() == Boolean.class || obj instanceof Number){
        return obj.toString();
    }else if(obj instanceof String){
        return stringify((String)obj);
    }
}
```

## string 类型
string 类型就比较复杂了。因为还有转义，以及 unicode 的处理。这部分尤其是 unicode 的转义，是在是有点麻烦，因为 java 中的 unicode 是会自动转的

```java
Assert.assertEquals("你好世界","\u4f60\u597d\u4e16\u754c");// true；
```

所以就不大清楚，什么字符串应该进行转义，什么字符串应该不转。我也是看 https://github.com/stleary/JSON-java/blob/master/JSONObject.java#L1945 这里也明白如何去写。
才写出这样的函数。
```java
    private static String stringify(String str){
        if(str == null){
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\"");

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
        return sb.append("\"").toString();
    }
```


## 数组类型
数组类型，有点麻烦。因为有几种方式都能表达出数组。

1、 实现了 Collection 接口的类，比如 List，Queue 这样的类。用`obj instanceof Collection`就判断出
2、 Object[]，可以用 `obj instanceof Object[]`,进行判断
3、 原生类型数组 int[]，可以用 `obj.getClass().isArray()` 进行判断。 

不难，就是有点烦。

对于 Collection 的 stringify，可以写成这样

```java
    private static String stringify(Collection collection) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if(collection == null){
            return  "null";
        }
        StringBuilder sb = new StringBuilder("[");
        for (Object obj : collection){
            sb.append(JSON.stringify(obj)).append(",");
        }
        return sb.replace(sb.length()-1,sb.length(),"]").toString();

    }
```

而对于 Object[] 和 int[] 的情况，可以写成这样。

```java
	private static String stringifyArray(Object array) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if(array == null){
            return  "null";
        }
        StringBuilder sb = new StringBuilder("[");
        for(int i = 0 ; i < Array.getLength(array);i++){
            sb.append(JSON.stringify(Array.get(array,i))).append(",");
        }

        return sb.replace(sb.length()-1,sb.length(),"]").toString();
    }
```    
  

## Object 类型  

这部分有几种思路的

1、 创建 JSONObject 来表示对象。如果是 Object 或者是 Map 类型的对象，可以通过接口`JSONObject.fromObject(Object obj)`转成 JSONObject，当然是利用反射。然后重载 JSONObject 的 toString 方法就可以了。
2. 不创建 JSONObject，因为使用者的目的仅仅是 stringify ，而不是去拼接 JSONObject。用反射直接转成 Map，然后再调用  stringify 即可。
3、 连 map 都不创建了，一边遍历反射的元素、一边进行 stringify 岂不美哉。 

几种思路我都实现过了。其实都要用反射的。没什么不同。矛盾主要是代码应该放在一个类中包装，还是直接放在函数中罢了。
来讲讲 2 种实现方式。

### Object 转 Map

有些朋友可能想到用  BeanUtils ,将 obj 转成 Map
```java
BeanMap objectMap = new BeanMap(obj);
return stringify(objectMap);
```

但比较坑爹的是，比如有个类是 Person，只有一个字段叫 name。
这里的 objectMap 返回的结果是
![](https://www.tuchuang001.com/images/2018/11/09/QQ20181109-173120.png)

如果直接 `stringify(objectMap)` 进行递归转JSON字符串的话，因为 class 属性的关系就会进行死循环了。。。直接爆栈了。

而为何会得到两个属性？

因为 BeanMap 这个方法不是通过反射直接获取字段的值，而是用反射获取对应的Get方法的值。比如是 name 字段，BeanMap 实际上获取的是 getName 方法的值。而每个类中都有 getClass() 方法。所以就有两个字段了。坑爹吧。
如果你想通过，`objectMap.put("class",null)` 这样搞，也是不行的。

看 BeanMap.put 的源码，它这里的 Map 结构还真是 Map 来的，就是 Object 的字段和 Map 是映射关系的。你如果你设置 `objectMap.put("name",null)` ，原来的 obj 的 name 的值也会被对应改成 null 的。。。
而你通过 `objectMap.put("class",null)` 的时候，并没有 `setClass` 的方法，所以会直接报错。
```java
   public Object put(Object name, Object value) throws IllegalArgumentException, ClassCastException {
        if (this.bean == null) {
            return null;
        } else {
            Object oldValue = this.get(name);
            Method method = this.getWriteMethod(name);
            if (method == null) {
                throw new IllegalArgumentException("The bean of type: " + this.bean.getClass().getName() + " has no property called: " + name);
            } else {
                IllegalArgumentException iae;
                try {
                    Object[] arguments = this.createWriteMethodArguments(method, value);
                    method.invoke(this.bean, arguments);
                    Object newValue = this.get(name);
                    this.firePropertyChange(name, oldValue, newValue);
                    return oldValue;
                } catch (InvocationTargetException var7) {
                    iae = new IllegalArgumentException(var7.getMessage());
                    if (!BeanUtils.initCause(iae, var7)) {
                        this.logInfo(var7);
                    }

                    throw iae;
                } catch (IllegalAccessException var8) {
                    iae = new IllegalArgumentException(var8.getMessage());
                    if (!BeanUtils.initCause(iae, var8)) {
                        this.logInfo(var8);
                    }

                    throw iae;
                }
            }
        }
    }
```

所以唯有恶心地写下这样的代码
```java
private static String stringify(Map<Object,Object> map) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
	if (map == null) {
    	return "null";
    } else if (map.isEmpty()) {
        return "{}";
    }

    StringBuilder sb = new StringBuilder("{");
    for (Map.Entry<?, ?> entry : map.entrySet()) {
    	if(entry.getKey() instanceof String){
        	if("class".equals(entry.getKey())){//神奇的特殊处理
            	continue;
            }
            sb.append(JSON.stringify(entry.getKey())).append(":").append(JSON.stringify(entry.getValue())).append(",");
        }
    }
    return sb.replace(sb.length() - 1, sb.length(), "}").toString();
}
```

那么如果自己写一个 Object 转 Map，要怎样做？

首先，自己通过反射获取所有的域是 public 的名字的前缀是 get 开头的所有方法
Map 的 key 就是 get（比如是 getName） 后面的 name，
Map 的 value 只要调用这个方法获取就可以了。  

具体实现，你可以参考 https://github.com/stleary/JSON-java/blob/master/JSONObject.java#L1458
或者我的 https://github.com/zgj1024/java-json-tutorial/blob/master/src/main/java/com/zhangguojian/json/ReflectUtils.java