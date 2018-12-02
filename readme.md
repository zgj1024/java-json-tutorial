[JSON （JavaScript Object Notation）](https://zh.wikipedia.org/wiki/JSON)，是一种容易阅读的纯文本格式，常作为客户端与服务器间的中间语言进行通讯交流。前端的朋友，对`JSON.parse`和`JSON.stringify`这两个函数最熟悉不过了。如果要你去实现这两个函或这样做呢？而这篇的目的之一就是希望让大家大致了解这过程。另外一个目的是让大家了解一些编译原理的知识，以及尝试让大家体会 TDD（Test Driven Design）的好与不足。
<!--more-->

# 设计 api

我们先设计下 api 吧。api 也很简单，就设计成这样

```java
public class JSON {
    public static Object parse(String input){
        //TODO
        return null;
    }

    public static String stringify(Object obj){
        //TODO
        return "";
    }
}
```

最终结果的测试用例大概是这样的
```java
public class JSONTest {

    @Test
    public void testParse() throws IOException {
        //读取JSON文件
        StringBuilder sb = new StringBuilder();
        Files.lines(Paths.get("src/test/data/juejin-me.json"), StandardCharsets.UTF_8).forEach(sb::append);
        String jsonStr = sb.toString();

        JSONObject obj = JSON.parse(jsonStr);
        JSONObject d = obj.get("d");
        Assert.assertEquals(d.get("username"), "挖坑英雄小王");
    }
```


# 执行流程

执行的流程和一般的解释器的前端是相同的。你可以看我这篇文章[简单的四则运算（二）迷你解释器](https://juejin.im/post/5bd7a5a351882520916fa674)了解一下

流程图可以看龙书的配图（忽略符号表吧，这里不需要符号表）

![](https://www.tuchuang001.com/images/2018/10/14/QQ20181014-094101.png)

大致流程也说明一下。 

- Lexer（词法分析）：将源码（JSON）的每个字符，分割成一个一个记号（Token），比如，`{"name": "张三"}`，会分割成：`{`、`name`、`:`、`张三`、`}` 这些 Token。

- Parse（语法分析）：通过 Lexer 的 `getNextToken()` 函数获取 Token，然后会进行语法的校验，并且直接转成 java 的内部表示。比如 `{"name": "张三"}` 就会转成 java 中的 Map 。

# 文件模板

所以，通过上面的描述，易知描述的数据结构。

## TokenType（描述记号的类型）

```java
public enum TokenType {
    EOF,NULL,TRUE,FALSE,NUM,STR,COMMA,LP,RP,COLON;//当然还有更多的
}
```

其中
- COMMA 表示 ',' 
- LP表示 {
- COLON :
- ... 等等



## Token（最小的词法单元、记号）

```java
public class Token {
    public TokenType tokenType;
    public String text;

    public Token(TokenType tokenType, String text) {
        this.tokenType = tokenType;
        this.text = text;
    }
}
```

比如 `{"name": "张三"}` 分割的 Token 会是 `'{'-LP`、`'name'-STR`、`:-COLON`、`张三-STR`、`'}-RP'` 这几个Token。

## Lexer（词法分析）
将输入的字符串转成一个个Token。并有一个`getNextToken`的方法，提供给 Parser 进行 Parse(语法分析)。

所以定义是简单的
```java
public class Lexer {

    String input;
    
    public Lexer(String input){
        this.input = input;        
    }
    
    public Token getNextToken(){
        //TODO
    }
}
```

又因为 `Lexer` 的另一个职责是将字符串转成一个个的 Token，所以肯定需要遍历一个字符串。当然你可以一个 for 循环，将 Token 保存在一个数组中，然后每次 `getNextToken` 只是改变数组的索引。但这样弄的话，还没解释完成就会占用过多了内存了。每次要用 `getNextToken` 才开始分析，那就挺好的。

所以还需要一个很容易想到，添加一个索引 p，和 char 变量指向输入字符串的位置和对应的字符。

这里还有一个辅助函数 `nextChar`，让索引 p 向前走一步，char c 指向下一个字符。p 到达字符串的末尾的时候就返回 EOF(end of file)
```java
public class Lexer {

    String input;
    private int p = -1;
    private char c;
    
    private char EOF = (char) -1;

    public Lexer(String input){
        this.input = input;        
        nextChar();
    }
    
    public Token getNextToken(){
        //TODO
    }

    //下一个字符
    private void nextChar(){
        p++;
        if ( p < input.length()){
            c = input.charAt(p);
        }else {
            c= EOF;
        }
    }
}
```

## parse（语法分析）
语法分析，模板大概就是这样的。从 Lexer 中不断地获取 Token。现在的情况不大复杂，之后还有用 match 函数去简化，在之后的文章中还会继续探索。

```java
public class Parser {

    private Lexer input;

    public Parser(Lexer input) {
        this.input = input;
    }

    Object parse() throws JSONException {
        Token token = input.getNextToken();
        switch (token.tokenType){
            case NULL:
                return null;
            case TRUE:
                return Boolean.TRUE;
            case FALSE:
                return Boolean.FALSE;
            default:
                throw new NoViableTokenException("Unexpected token is " + token.tokenType);
        }
    }
}
```

