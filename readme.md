# 解析对象
解析对象和解析数组有点类似。如何你理解了数组的解析，这部分的内容也很容易理解。

规则图是这样的。应该是比较容易理解的。
![](https://www.json.org/object.gif)

# Lexer

lexer 部分明显要增添 `'{'(LP)`,`'}'(RP)`,`':'(COLON)` 这几个 Token类型了。而 Lexer 的测试用例如下。


```java
@Test
public void testObject() throws InvalidCharacterException {
    Lexer lexer = new Lexer("{\"name\":\"John Smith\",\"age\":15 }");
    
    assertThat(lexer.getNextToken().tokenType).isEqualTo(TokenType.LP);
    assertThat(lexer.getNextToken().tokenType).isEqualTo(TokenType.STR);
    assertThat(lexer.getNextToken().tokenType).isEqualTo(TokenType.COLON);
    assertThat(lexer.getNextToken().tokenType).isEqualTo(TokenType.STR);
    assertThat(lexer.getNextToken().tokenType).isEqualTo(TokenType.COMMA);
    assertThat(lexer.getNextToken().tokenType).isEqualTo(TokenType.STR);
    assertThat(lexer.getNextToken().tokenType).isEqualTo(TokenType.COLON);
    assertThat(lexer.getNextToken().tokenType).isEqualTo(TokenType.NUM);

    assertThat(lexer.getNextToken().tokenType).isEqualTo(TokenType.RP);
}
```

这部分 Lexer 比较简单，参考上一章很容易写出来的，自己尝试一下吧。

# parser

parser 的部分和上一章也是类似的。有了上一章的基础，这章就比较简单了。
其中，解析对象用映射成 Map。
而规则如下

```
object: '{' '}' | '{' members '}'
members : member , members
member: string ':' element
``` 


根据规则很容易写出下面的代码

```java
/* object: '{' '}' | '{' members '}' */
private Map<String,Object> parseObj() throws JSONException {
    match(LP);
    if (forward.tokenType == RP) {
        match(RP);
        return new HashMap<>();
    } else {
        Map<String,Object> objMap = new HashMap<>();
        members(objMap);
        match(RP);
        return objMap;
    }
}

/* members : member , members */
private void members(Map<String,Object> objMap) throws JSONException {
    member(objMap);
    while (forward.tokenType == COMMA) {
        match(COMMA);
        members(objMap);
    }
}

/* member: string ':' element */
private void member(Map<String,Object> map) throws JSONException {
    String key = forward.text;
    match(STR);
    match(COLON);
    map.put(key, value());
}
```

# 最后

到现在为止，一个简单的 JSON 解析已经完成了。但仍有狠多很多的改进空间。

1. 一般来讲解析出来的数据会用 `final` 去修饰，也就是说解析出来的数组或对象应该是不能被更改的，这能免去很多烦恼。也这种直接返回 List，Map 的方式是不妥的，所以最好是将之封装成 JSONArray，JSONObject 这样的对象，然后里面放个 `final List` 
2. 这里的解析是遍历 string ，但实际常用的场景是面向字节流的。很多 JSON 的解析的框架也是面向 Reader 接口的，这样可以无须将流变成字符串再解析，而是一边读字节流一边解析，发现有问题就直接中断请求了，这样性能会好多了。所以之后尝试这样做。
3. 泛型的改进，以前的版本还满足用 Object 代表一切，但这样类型就不安全的。而泛型基本能解决这样的问题，比如 gson，jackson 都是支持泛型的。老实说支持泛型还挺烦的，还挺多坑的。。。