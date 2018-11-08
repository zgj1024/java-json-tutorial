# 解析 NULL、Boolean

![](https://www.json.org/value.gif)

在https://www.json.org/ 这网站中可以看到 JSON 标准，其中处理 true、false、null 类型比较简单。一般我做项目的还是喜欢从易到难的顺序去处理的。
所以这一部分的只是解析 NULL，和 Boolean 类型
虽说简单但写起代码来还是会有一些麻烦的。我们来一起来看看。

# NULL 类型

## Lexer

先写测试吧。要解析 NULL 测试要如何写呢？测试文件大概会是这样吧

LexerTest.java

```java
@Test
public void testGetNullToken()  {
    Lexer lexer = new Lexer("null");
	Assert.assertEquals(lexer.getNextToken().tokenType,TokenType.NULL);
}
```
容易写出这样的解析 null 代码。
```java
public Token getNextToken() throws InvalidCharacterException {
	while (c!=EOF){
		switch (c){
			case '\r': case '\n': case '\t': case ' ':ws();break;
            case 'n':
            	if (p+3<input.length() && input.charAt(p+1)=='u'
                    &&input.charAt(p+2)=='l'&&input.charAt(p+3)=='l'){
                	return new Token(TokenType.NULL,"null");
                }
                throw new InvalidCharacterException("invalid character: " + c);
            default:
                throw new InvalidCharacterException("invalid character: " + c);
        }
    }
    return  new Token(TokenType.EOF,"");
}
```

当遇到 `'\r'`,`\n`,`\t`,` `这些字符的时候直接跳过，ws（white space）方法是这样的。

```java
private void ws(){
    while (c == ' ' || c== '\t' || c == '\r' || c == '\n')
    	nextChar();
}
```

而`InvalidCharacterException`异常是自定义的，继承 JSONException。为了可以用 JSONException 统一的处理来自 JSON 解析的异常。之后会更多类别的异常，定义也是相类似的。

```java
public class InvalidCharacterException extends JSONException {
    public InvalidCharacterException(String message) {
        super(message);
    }
}
```

```java
public  abstract  class JSONException extends Exception {
    public JSONException(String message) {
        super(message);
    }
}
```

上面 Lexer 的代码也有点问题

1. 太多 `new`了，每次遇到 null 就要 new 一次 Token。这种写法是不合理的。而且每个 null Token 内容其实是一样的。所以可以用静态对象处理一下。 
    ```java
    public class Token {
        public final static Token TRUE = new Token(TokenType.TRUE,"true");
        public final static Token FALSE = new Token(TokenType.FALSE,"false");
        public final static Token NULL = new Token(TokenType.NULL,"null");
        public final static Token EOF = new Token(TokenType.EOF,"");
        //...
    }
    ```

    ```java
    public Token getNextToken() throws InvalidCharacterException {
        while (c!=EOF){
            switch (c){
                case '\r': case '\n': case '\t': case ' ':ws();break;
                case 'n':
                    if (p+3<input.length() && input.charAt(p+1)=='u'
    			&&input.charAt(p+2)=='l'&&input.charAt(p+3)=='l'){
                        return Token.NULL;
                    }
                    throw new InvalidCharacterException("invalid character: " + c);
                default:
                    throw new InvalidCharacterException("invalid character: " + c);
            }
        }
        return Token.EOF;
	}
    ```

2. 这样的解析的方法当然有问题了，明显不能通过这样的测试用例

  ```java
  @Test(expected = InvalidCharacterException.class)
  public void testGetNullToken() throws InvalidCharacterException {
     
  	Lexer lexer = new Lexer("null");
      Assert.assertEquals(lexer.getNextToken(),Token.NULL);
       
      lexer = new Lexer("nulla");
      lexer.getNextToken();
  }
  ```

   因为没有针对 null 后面的字符进行处理，null 后面其实可以是一些分隔符，`\r`、`\n` 之类的，但是非分隔符的字符就是非法的，参考一下阿里巴巴的 fastJson 的处理方式 [LexerBase](https://github.com/alibaba/fastjson/blob/master/src/main/java/com/alibaba/fastjson/parser/JSONLexerBase.java)

   ```java
  public final void scanNullOrNew() {
  	if (ch != 'n') {
      	throw new JSONException("error parse null or new");
      }
      next();
   
      if (ch == 'u') {
          next();
          if (ch != 'l') {
              throw new JSONException("error parse null");
          }
          next();
      
          if (ch != 'l') {
              throw new JSONException("error parse null");
          }
          next();
      
          if (ch == ' ' || ch == ',' || ch == '}' || ch == ']' || ch == '\n' || ch == '\r' || ch == '\t' || ch == EOI
           || ch == '\f' || ch == '\b') {
          	token = JSONToken.NULL;
          } else {
              throw new JSONException("scan null error");
          }
          return;
      }
      
      if (ch != 'e') {
          throw new JSONException("error parse new");
      }
      next();
      
      if (ch != 'w') {
          throw new JSONException("error parse new");
      }
      next();
      
      if (ch == ' ' || ch == ',' || ch == '}' || ch == ']' || ch == '\n' || ch == '\r' || ch == '\t' || ch == EOI || ch == '\f' || ch == '\b') {
      	token = JSONToken.NEW;
      } else {
          throw new JSONException("scan new error");
      }
  }
   ```

   。。。干工程就会有这样的脏活
   而它这里还处理 new 关键字的，而我的版本就没有 new 关键字。所以写成这样就好了
   ```java
  private Token scanNull() throws InvalidCharacterException {
      //因为在 getNextToken 中已经判断这个字符是'n' 
      //而且这个函数的域(scope)是 private 的，只有在这个类中才会调用
      //自己明白这里的 c 肯定是 n 就行了，不用做多余的判断
      //一般没有成本开销，因为知道 c 肯定是 n。基本会直接忽略的
      //这样写更多的是一种提示和简化吧
      assert c == 'n';
  	nextChar();
  	
  		if(c!='u')
  		throw new InvalidCharacterException("invalid character: n"+c);
  	nextChar();
  
       if(c!='l')
           throw new InvalidCharacterException("invalid character: nu"+c);
       nextChar();
  
       if(c!='l')
           throw new InvalidCharacterException("invalid character: nul"+c);
       nextChar();
  
       if (c == ' ' || c == ',' || c == '}' || c == ']' || c == '\n' || c == '\r' || c == '\t' || c == EOF
               || c == '\f' || c == '\b') {
           return Token.NULL;
       }
       throw new InvalidCharacterException("invalid character: null" + c);
  }
   ```

  ```java
  public Token getNextToken() throws InvalidCharacterException {
  	while (c!=EOF){
  		switch (c){
  			case '\r': case '\n': case '\t': case ' ':ws();break;
  			case 'n':
  				return scanNull();
  			default:
  				throw new InvalidCharacterException("invalid character: " + c);
  	}
  	return Token.EOF;
  }
  ```


  再运行一下测试，测试可以通过的。当然 scanNull 函数看起来还是会有点不太好看。在弄 Boolean 类型的时候会处理得更好看一点。

3. 测试的优化，我们的测试 Null 的时候
   ```java
   @Test(expected = InvalidCharacterException.class)
   public void testGetNullToken() throws InvalidCharacterException{
       Lexer lexer = new Lexer("null");
       Assert.assertEquals(lexer.getNextToken(), Token.NULL);
       
       lexer = new Lexer("nulla");
       lexer.getNextToken();        
   }
   ```
   但这种方式没有想象中的好。比如你想测试这种情况的时候

   ```java
   @Test(expected = InvalidCharacterException.class)
   public void testGetNullToken() throws InvalidCharacterException{
       //1
       Lexer lexer = new Lexer("null");
       Assert.assertEquals(lexer.getNextToken(), Token.NULL);
       //2
       lexer = new Lexer("nul");
       lexer.getNextToken();
       //3
       lexer = new Lexer("nulla");
       lexer.getNextToken();
   }
   ```
   实际上 `3` 那个点是测试不了了，因为跑`2`的时候回抛出异常就终结。。。。以前的做法会新创新一个函数（或者说是用例），或者是下面这样弄。

   ```java
    @Test
    public void testGetNullToken() throws InvalidCharacterException{
   
        Lexer lexer = new Lexer("null");
        Assert.assertEquals(lexer.getNextToken(), Token.NULL);
      
        try {
            lexer = new Lexer("nu");
            lexer.getNextToken();
        }catch (Exception e){
            Assert.assertEquals(e.getClass(),InvalidCharacterException.class);
       }
    
        try {
            lexer = new Lexer("nulla");
            lexer.getNextToken();
        }catch (Exception e){
   		Assert.assertEquals(e.getClass(),InvalidCharacterException.class);
   	}
   }
   ```

    说实话，觉得两种处理方式都挺丑的。而 Java8 之后，通过 [org.assertj.assertj-core](https://mvnrepository.com/artifact/org.assertj/assertj-core/3.11.1) 这个包之后就能写成这样了。

    ```java
   @Test
   public void testGetNullToken() throws InvalidCharacterException {
       assertThat(new Lexer("null").getNextToken())
           .isEqualTo(Token.NULL);
      
       assertThatThrownBy(() -> new Lexer("nul").getNextToken())
       	.isInstanceOf(InvalidCharacterException.class);
    
       assertThatThrownBy(() -> new Lexer("nullf").getNextToken())
           .isInstanceOf(InvalidCharacterException.class);
   }
    ```

## Parse

这里的语法分析比较简单。

```java
public class Parser {

    private Lexer lexer;

    public Parser(String input) {
        this.lexer = new Lexer(input);
    }

    Object parse() throws JSONException {
        Token token = lexer.getNextToken();
        switch (token.tokenType){
            case NULL:
                return null;            
            default:
                throw new NoViableTokenException("Unexpected token is " + token.tokenType);
        }
    }
}
```

测试用例也简单
```java
public class ParserTest {
    @Test
    public void parseNull() throws JSONException {
        assertThat(new Parser("null").parse())
                .isEqualTo(null);
    }
}
```

# Boolean 类型

## Lexer
解析 Boolean 类型也很简单，Boolean 类型有且只有 true 和 false 两种类型。可以套用null 的代码。

```java
private Token scanTrue() throws InvalidCharacterException {	
	assert c == 't';
	nextChar();

	if (c != 'r') {
		throw new InvalidCharacterException("invalid characters: t" +c);
	}
	nextChar();

	if (c != 'u') {
		throw new InvalidCharacterException("invalid characters: tru"+c);
	}
	nextChar();

	if (c != 'e') {
		throw new InvalidCharacterException("invalid characters: tr"+c);
	}
	nextChar();

	if (c == ' ' || c == ',' || c == '}' || c == ']' || c == '\n' || c == '\r' || c == '\t' || c == EOF  || c == '\f' || c == '\b' || c == ':' || c == '/') {
		return Token.TRUE;
	} else {
		throw new InvalidCharacterException("invalid characters: true"+c);
	}
}

private void scanFalse() throws Invalidcharacterexception{
	//...
}
		
public Token getNextToken() throws InvalidCharacterException {
	while (c!=EOF){
		switch (c){
			case '\r': case '\n': case '\t': case ' ':ws();break;
			case 'n':
				return scanNull();
			case 'f':
				return scanFalse();
			case 't':
				return scanTrue();
			default:
				throw new InvalidCharacterException("invalid character: " + c);
            }
	}
	return Token.EOF;
}
```

测试用例如下

```java
@Test
public void TestBoolean() throws InvalidCharacterException {
	assertThat(new Lexer("true").getNextToken()).isEqualTo(Token.TRUE);
	assertThat(new Lexer("false").getNextToken()).isEqualTo(Token.FALSE);

	assertThat(new Lexer("true\n").getNextToken()).isEqualTo(Token.TRUE);
	assertThat(new Lexer("false\r").getNextToken()).isEqualTo(Token.FALSE);

	assertThatThrownBy(() -> {
		new Lexer("tru").getNextToken();
	}).isInstanceOf(InvalidCharacterException.class);
	assertThatThrownBy(() -> {
		new Lexer("fals").getNextToken();
	}).isInstanceOf(InvalidCharacterException.class);

	assertThatThrownBy(() -> {
		new Lexer("trua").getNextToken();
	}).isInstanceOf(InvalidCharacterException.class);
	assertThatThrownBy(() -> {
		new Lexer("falsa").getNextToken();
	}).isInstanceOf(InvalidCharacterException.class);

	assertThatThrownBy(() -> {
		new Lexer("truek").getNextToken();
	}).isInstanceOf(InvalidCharacterException.class);
	assertThatThrownBy(() -> {
		new Lexer("falsek").getNextToken();
	}).isInstanceOf(InvalidCharacterException.class);
}
```

但可以看到这些重复代码确实有点恶心。看到《重构》那书的同学，是否嗅到了“坏代码的味道”呢？

首先
```
if (c == ' ' || c == ',' || c == '}' || c == ']' || c == '\n' || c == '\r' || c == '\t' || c == EOF|| c == '\f' || c == '\b') {
	return Token.TRUE;
}
```
这句逻辑是通用的，所以可以提取出来封装一个函数，为何 fastjson 没有提取？因为 fastjson 的`scanTrue`和`scanNull`中的逻辑是不同的，`scanTrue`中后面还能接 ':', '/'。我也不知道是什么特殊情况。而我的设计中是没有这部分的，所以直接提取。
所以我会提取这部分逻辑。如这样

```java
private boolean isSeparatorChar(){
	return c == ' ' || c == ',' || c == '}' || c == ']' || c == '\n' || c == '\r' || c == '\t' 
	|| c == EOF|| c == '\f' || c == '\b';
}
```

当然遍历这样的代码，自己也不太喜欢
```java
if (c != 'r') {
	throw new InvalidCharacterException("invalid characters: t" +c);
}
nextChar();
```

所以就会提取出这样的代码
```java
private Token scanText(String keyword,Token token) throws InvalidCharacterException {
	assert(keyword!=null);
	for(int i = 0 ; i < keyword.length() ;i++){
		if(c!=keyword.charAt(i)){
			throw new InvalidCharacterException("invalid character: "+ keyword.substring(0,i) + c);
        }
		nextChar();
	}
	if(isSeparatorChar()){
		return token;
	}
	throw new InvalidCharacterException("invalid character: "+ keyword + c);
}
```

于是，代码就能变成这样了。应该清晰了很多吧。

```java
private Token scanNull() throws InvalidCharacterException {
	return scanText("null", Token.NULL);
}

private Token scanTrue() throws InvalidCharacterException {
	return scanText("true", Token.TRUE);
}

private Token scanFalse() throws InvalidCharacterException {
	return scanText("false", Token.FALSE);
}
```

再跑一次测试用例试试？也是可以跑通的，这也是测试用例的好处。只用写一次测试的逻辑，之后随便你这么重构，重构成不成功看测试用例能否跑通就可以了，也不用人手工测试，也不用写一份专门的文档（专门的一个人）记这业务逻辑是什么。对公司而言其实节省了很多时间的，节省了人手。只是相对来讲对编程人员的要求比较高，这事实不能强求，培养也很难培养，告诉他道理他也接受不来，培养很难。

说回代码。假如想测试`scanText`这个函数要怎样做呢？因为`scanText`是用`private`域（scope） 的，所以在其他类中是不可见的。测试的难处也在此，挺纠结的地方就是要不要将它改成`public`就好测试了。。。我的建议是尽量不要的。

Lexer 的设计了只对外部暴露了`getNextToken`方法，这是符号开放-闭合原则和单一责任制的。我是不是因为这个函数难测试而违反原则的。要怎么做呢？利用反射。

比如我会在 LexerTest 中写这样一样的静态测试类
```java
private static Token scanText(String keyword, String input, Token token) throws Throwable {
	Lexer lexer = new Lexer(input);
	Method method = Lexer.class.getDeclaredMethod("scanText", String.class, Token.class);
	method.setAccessible(true);
	try {
		return (Token) method.invoke(lexer, keyword, token);
	} catch (InvocationTargetException e) {
		//这样做才能抛出 Invalidcharacterexception
		throw e.getTargetException();
	}
}
```

测试用例如下
```java
@Test
public void scanText() throws Throwable {
	assertThat(scanText("true", "true", Token.TRUE)).isEqualTo(Token.TRUE);

	assertThatThrownBy(() -> scanText("trued", "true", Token.TRUE))
		.isInstanceOf(InvalidCharacterException.class);

	assertThatThrownBy(() -> scanText("null", "n", Token.NULL))
		.isInstanceOf(InvalidCharacterException.class);
}
```

## Parse

基本类型的 Parse 都很简单
```java
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
```

测试用例也简单
```java
@Test
public void parseBoolean() throws JSONException {
	assertThat(new Parser("true").parse())
		.isEqualTo(Boolean.TRUE);
    assertThat(new Parser("false").parse())
    	.isEqualTo(Boolean.FALSE);
}
```
