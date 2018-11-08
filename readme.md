# 使用方式

## JSON TO Object

### 泛型

```java
LinkedList<Integer> intList =  JSON.parse("[1,2,3,4]",new TypeReference<LinkedList<Integer>>(){});

/*如果是 interface 非实现类，要先指明类型，要不然会返回 JSONArray*/
JSONContext context = new JSONContext();
jsonContext.addImpl(List.class,ArrayList.class);
//这样的话，会返回 ArrayList
List<Integer> intList =  JSON.parse("[1,2,3,4]",new TypeReference<List<Integer>>(){},context);
```

### 非泛型


```java
Person person =  JSON.parse("{\"name\":\"张三\"}",Person.class);

//如果不指明对象的话，返回的会是 JSONValue,
//boolean 类型
Boolean bool =  JSON.parse("true").boolValue();

//数字类型，因为不知道你要的是那种数字类型，所以返回的是 Number 的抽象类
//可以通过 numberValue().doubleValue() 等方式获取
//过大的数字会自己转成 BigInt 或者是 BigDecimal
int num =  JSON.parse("1234").numberValue().intValue();

//String 类型
String str = JSON.parse("\"Hello\"").strValue()

//数组类型 JSONArray（继承 ArrayList），没多余的成员变量，只增加了几个方法。
JSONArray array = JSON.parse("[1,2,3,4]").arrayValue();

//对象用的 JSONObject(继承 HashMap)
 JSONObject jsonObject = JSON.parse("{\"name\":\"张三\"}").objectValue();
```

## 对象转成 JSON 字符串
```java
Person person = new Person();
person.setName("张三");

//这样就可以了
JSON.stringify(person);
```

## 一些注解

### JSONIgnore
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

对象中有时间对象的时候特别麻烦，很多时候你会想将 Timestamp 变成 long 类型、
DataTime 作为字符串进行传输的，而不是将对象变成 `{"year":2018,month:12...}` 这种东西

而在这个框架中，你只需将在 get 方法中给字段注解就可以了。

```java
@JSONSerialize(using = TimestampSerializer.class)
public Timestamp getBirthDate() {
    return birthDate;
}
```

而 TimestampSerializer 这个类是必须要实现我预留的接口才可以的。
```java
public class TimestampSerializer implements CustomSerializer<Timestamp,Long> {

    @Override
    public Long serializeValue(Timestamp input) {
        return input.getTime();
    }
}
```

测试用例是这样的
```java
@Test
public void testSerializer(){
 	Hero hero = new Hero();
    hero.setName("金庸");
    hero.setBirthDate(Timestamp.valueOf("1924-03-10 00:00:00"));
    hero.setDeathDate(Date.valueOf("2018-10-30"));

   	Assert.assertEquals("{\"name\":\"金庸\",\"birthDate\":-1445760000000,\"deathDate\":\"2018-10-30 00:00:00\"}",JSON.stringify(hero));
}
```

### JSONDeserialize

JSONDeserialize 和上面的需求是类似，只是 Deserialize 用与讲 JSON 字符串转成对象。 
用例如下
```java
String jinyongJSON = "{\"name\":\"金庸\",\"birthDate\":-1445760000000,\"deathDate\":\"2018-10-30 00:00:00\"}";

Hero hero = JSON.parse(jinyongJSON,Hero.class);
Assert.assertEquals("金庸",hero.getName());
Assert.assertEquals(Date.valueOf("2018-10-30"),hero.getDeathDate());
Assert.assertEquals(new Timestamp(-1445760000000L),hero.getBirthDate());
```




# TOOD 
- [ ] 支持 Reader 构造 Parser