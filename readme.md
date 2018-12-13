# 使用方式

## JSON字符串解析成对象

### 普通对象
```
User user =  JSON.parse("{\"name\":\"张三\"}",User.class);

Boolean bool = JSON.parse("true",Boolean.class);// bool is true
String str = JSON.parse("\"hello\"",String.class);// str is "hello"
```

### 泛型的支持

泛型要使用 TypeReference 对象才能实现
```java
LinkedList<Integer> intList =  JSON.parse("[1,2,3,4]",new TypeReference<LinkedList<Integer>>(){});

/*如果是 interface 非实现类，要先指明类型*/
JSONContext context = new JSONContext();
jsonContext.putImplCls(List.class,LinkedList.class);
//这样的话，会返回 LinkedList
List<Integer> intList =  JSON.parse("[1,2,3,4]",new TypeReference<List<Integer>>(){},context);
```

### 非泛型

- null 类型会转成 JSONNull
```java
JSONElement Null = parse("null");
Assert.assertTrue(Null instanceof JSONNull);
```

- 数字、字符、字符串类型会转成 JSONPrimitive
```java
JSONElement primitive = parse("123");
Assert.assertTrue(primitive instanceof JSONPrimitive);
```

- JSON的对象会转成 JSONObject
```java
JSONElement jsonObject = JSON.parse("[1,2,3,4]");
Assert.assertTrue(jsonObject instanceof JSONArray);
```

- JSON的对象会转成 JSONObject 
```java
JSONElement jsonObject = JSON.parse("{\"name\":\"张三\"}"
Assert.assertTrue(jsonObject instanceof JSONObject);
```


## 对象转成 JSON 字符串

非常简单
```java
Person person = new Person();
person.setName("张三");

JSON.stringify(person); // result is {"name":"张三"}
```

## 扩展语法

### 忽略一些字段  JSONIgnore
有些属性不想转成 JSON 字符串在，get函数上添加 @JSONIgnore 注解即可。
比如是这样
```java
public class Person {

    private String name ;
    private int age;
    
    // 很多 get set
    @JSONIgnore
    public int getAge(){
		return this.age ;
    }
}
```

这样的话，age 字段是不会被转成 JSON 字符串的。

同样的，如果 JSON 字符串转成的对象的某些字段不希望被赋值，也可以这样做

### JSONSerialize

有一些需求是，对象中的字段类型要和 JSON 中的不一样。例如：时间。

对象中有时间对象的时候特别麻烦，很多时候你会想将 Date 格式化成字符串传输，比如是 "2018-09-02 02:00:00 "而不是将对象变成 `{"year":2018,month:12...}` 这种东西

而在这个框架中，你只需将在 get 方法中给字段注解就可以了。

```java
@JSONSerialize(using = TimestampSerializer.class)
public Timestamp getBirthDate() {
    return birthDate;
}
```

而 TimestampSerializer 这个类是必须要实现我预留的接口才可以的。
```java
public class DateDeserializer implements CustomDeserializer<JSONPrimitive, Date> {
    private final static DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Date deserialize(JSONPrimitive input) throws ParseException {
        return sdf.parse(input.getAsString());
    }
}
```

测试用例是这样的
```java
@Test
public void testSerializer(){
    User user = new User();
    final  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    son.setBirthday(sdf.parse("2007-1-28 12:23:00"));
    
    String result = StringifyUtils.Stringify(son);
    //result is 
    assertEquals("{\"birthday\":\"2007-1-28 12:23:00\"}",result);
}
```

### JSONDeserialize

JSONDeserialize 和上面的需求是类似，只是 Deserialize 用与讲 JSON 字符串转成对象。 
用例如下
```java
String jsonStr = "{\"birthday\":\"2007-1-28 12:23:00\"}";

User user = JSON.parse(jsonStr,User.class);
final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
Assert.assertEquals(user.getBirthday(),sdf.parse("2007-1-28 12:23:00"));
```

### 别名  JSONAlias
```java
@JSONAlias(name = "alias")
public String getAliasName() {
    return aliasName;
}
```

User 类中添加这个注解后，转成 JSON 字符串后字段名 aliasName 会自动变成 alias
比如，在没有添加 JSONAlias 修饰前，stringify 后会是 `"{"aliasName":"zhang"}"`
在添加了 JSONAlias 修饰后，会是 `"{"alias":"zhang"}"`



# TOOD 
- [ x ] 支持 Reader 构造 Parser
- [ x ] 支持别名 