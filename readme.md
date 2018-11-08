# 解析数组

这部分的要点会是些编译原理中 Parse 的东西吧。JSON数组的规则如下。
![](https://www.json.org/array.gif)

数组的格式是
- '[' 开始 (LB 左中括号)
- ']'（RB 右中括号）结束
- 值之间使用 ','（COMMA,逗号）分隔。

这里比较解析起来有点麻烦的是，数组结束时候，']' 中括号前是不能有 ',' 号的，就说 '[1,]' 这样的格式是不正确的。

# Lexer

之前也说过 Lexer 的作用是分割一个个词法单元（Token） 的。比如：`[1,2]`分割的词法单元会是`[-LB`,`1-NUM`,`,-COMMA`,`2-NUM`,`]-RB`。所以可以用这个例子写测试用例。

```java
@Test
public void testArray() throws InvalidCharacterException {
	Lexer lexer = new Lexer("[1,2]");
	assertThat(lexer.getNextToken().tokenType).isEqualTo(TokenType.LB);

	assertThat(lexer.getNextToken().tokenType).isEqualTo(TokenType.NUM);
	assertThat(lexer.getNextToken().tokenType).isEqualTo(TokenType.COMMA);

	assertThat(lexer.getNextToken().tokenType).isEqualTo(TokenType.NUM);

	assertThat(lexer.getNextToken().tokenType).isEqualTo(TokenType.RB);
}
```

这里 Lexer 会比之前的简单多了。

```java
public Token getNextToken(){
	//...
	case '[':
		nextChar();
		return Token.LB;
	case ']':
		nextChar();
		return Token.RB;
	case ',':
		nextChar();
		return Token.COMMA;
	//...
}
```
当然你也需要定义 TokenType 和在 Token 中增添几种类型
```java
public final static Token COMMA = new Token(TokenType.COMMA,",");
public final static Token LB = new Token(TokenType.LB,"[");
public final static Token RB = new Token(TokenType.RB,"]");
```

# parse

这里的 parse 会复杂一点。想想如何 parse 成功你希望的结果会是如何？
大概会是这样吧。

```java
@Test
public void parseArray() throws JSONException {
	//数组为空
	assertThat(new Parser(new Lexer("[]")).parse())
		.isEqualTo(List.nil());
	//[1,2,3,4]
	assertThat(new Parser(new Lexer("[1,2,3,4]")).parse())
		.isEqualTo(List.of(1.0,2.0,3.0,4.0));
	//递归的情况 [[1,2],[3,4],[]]
	assertThat(new Parser(new Lexer("[[1,2],[3,4],[]]")).parse())
		.isEqualTo(List.of(List.of(1.0,2.0),List.of(3.0,4.0),List.nil()));

	//更深层的递归 [[1,2,[3]],[3,4],[]]
	assertThat(new Parser(new Lexer("[[1,2,[3]],[3,4],[]]")).parse())
		.isEqualTo(List.of(List.of(1.0,2.0,List.of(3.0))
		                ,List.of(3.0,4.0)
                        ,List.nil()));

	//不同类型的数组 [true,false,null,"Hello",3.1415E10]
	assertThat(new Parser(new Lexer("[true,false,null,\"Hello\",3.1415E10]")).parse())
		.isEqualTo(List.of(Boolean.TRUE,Boolean.FALSE,null,"Hello",3.1415E10));

	//解析失败的情况
	assertThatThrownBy(() -> new Parser(new Lexer("[,]")).parse())
		.isInstanceOf(NoViableTokenException.class);

	assertThatThrownBy(() -> new Parser(new Lexer("[1,2,]")).parse())
		.isInstanceOf(NoViableTokenException.class);
}
```

如果按照规则来嘛，很容易写成这样的。可能幻想这样的代码会跑通测试用例

```java
private Object value() throws JSONException {
	Token token = lexer.getNextToken();

	switch (token.tokenType) {
	//...
		case LB:
			return parseArray();
	}
	//..
}

public List<Object> parseArray() throws JSONException {

    //前一个Token 是 LB
    Token token = lexer.getNextToken();
    if(token.tokenType == RB){
        return new ArrayList<>();
    }

    List<Object> objectList = new ArrayList<>();
    objectList.add(value());

    token = lexer.getNextToken();
    while (token.tokenType == COMMA){
        lexer.getNextToken();
        objectList.add(value());
        token = lexer.getNextToken();
    }

    if(token.tokenType!= RB){
        throw new NoViableTokenException("expected token is "+ RB
                + "but actual is " +  token.tokenType);
    }
    return  objectList;
}
```

这里的代码或者是原本的逻辑的问题多多的。因为原来的 `value` 函数中是这样的。进行判断的
```java
private Object value() throws JSONException {
	Token token = lexer.getNextToken();
	switch (token.tokenType) {
	//...
}
```

而 `parseArray`函数为了判断空数组的情况。就会先用 lexer.getNextToken 拿字符串的后一个 Token

```java
public List<Object> parseArray() throws JSONException {
    //前一个Token 是 LB
    Token token = lexer.getNextToken();
    if(token.tokenType == RB){
        return new ArrayList<>();
    }
	//...
}
```

如果是空数组还好办，直接返回结果了。如何不是空数组比如是 `[1,2]` ,`parseArray`先来一个 nextToken，此时 Token 会是`2-NUM`，如果不是。运行`value`函数的时候又 nextToken，value 函数中的数字就变成`,-COMMA`，就不能正确解析。

所以很明显这里需要一个类变量存放 token 进行判断。所以会设计一个变量叫`Token forward`。在执行`value`函数的时候就不用再去`getNextToken`了，直接用`forward`进行比较就可以了。

这样一变，很多地方都要改，但这也是必须的。
```java
public Parser(Lexer lexer) throws InvalidCharacterException {
	this.lexer = lexer;
	this.forward = lexer.getNextToken();
}

Object parse() throws JSONException {
	Object value = value();
	forward = lexer.getNextToken();
	if (forward.tokenType != EOF) {
		throw new NoViableTokenException("Unexpected token is " + forward.tokenType);
	}
	return value;
}

private Object value() throws JSONException {
	switch (forward.tokenType) {
	//...
	}
	default:
		throw new NoViableTokenException("Unexpected token is " + forward.tokenType);
	}
}

private List<Object> parseArray() throws JSONException {
	//前一个Token 是 LB
	forward = lexer.getNextToken();
	if(forward.tokenType == RB){
		return new ArrayList<>();
	}

	List<Object> objectList = new ArrayList<>();
	objectList.add(value());

	forward = lexer.getNextToken();
	while (forward.tokenType == COMMA){
		forward = lexer.getNextToken();
		objectList.add(value());
		// 之前 value 函数都是直接返回值的，
		// 比如解析数字 [1,2,3]，解析完 2 后，
		// 要将 token变成 ',' 才能继续解析
		forward = lexer.getNextToken();
	}

	if(forward.tokenType!= RB){
		throw new NoViableTokenException("expected token is "+ forward.tokenType
                    + "but actual is " + forward.tokenType);
	}
	return  objectList;
}
```

到此，所有的测试用例都能跑通。

下面再重构下代码。
有没有发现，有好多 `forward = lexer.getNextToken()` 和 `if(forward.tokenType!=RB) throws Expection`这样的代码。
这样的代码看起来也不太好看。

所以可以用这个 `match` 函数代替。

```java
private void match(TokenType tokenType) throws JSONException {
	if(forward.tokenType == tokenType){
		this.forward = lexer.getNextToken();
	}else {
		throw new NoViableTokenException("expected token is "+ tokenType
                    + "but actual is " + forward.tokenType);
	}
}
```

就可以写成这样了。
```java
Object parse() throws JSONException {
	Object value = value();
	match(EOF);
	return value;
}

private Object value() throws JSONException {
	switch (forward.tokenType) {
		case NULL:
			match(NULL);
			return null;
		case TRUE:
			match(TRUE);
			return Boolean.TRUE;
		case FALSE:
			match(FALSE);
			return Boolean.FALSE;
		case STR:
			String strValue = forward.text;
			match(STR);
			return strValue;
		case NUM:
			Double numValue = Double.valueOf(forward.text);
			if (Double.isInfinite(numValue)) {
				throw new NumberParseException("nums: " + forward.text + " is too big or too small");
			}
			match(NUM);
			return numValue;
		case LB:
			return parseArray();
		default:
			throw new NoViableTokenException("Unexpected token is " + forward.tokenType);
	}
}

private List<Object> parseArray() throws JSONException {
	match(LB);
	if(forward.tokenType == RB){
		match(RB);
		return Collections.emptyList();
	}

	List<Object> objectList = new ArrayList<>();
	objectList.add(value());

	while (forward.tokenType == COMMA){
		match(COMMA);
		objectList.add(value());
	}

	match(RB);
	return  objectList;
 }
```
当然为了看上去更好看一点，建议 parseArray 函数可以写成这样。
```java
/* array: '[' ']' | '[' elements '] */
 private List<Object> parseArray() throws JSONException {
    match(LB);

    if(forward.tokenType == RB){
        match(RB);
        return Collections.emptyList();
    }else {
        List<Object> array = new ArrayList<>();
        elements(array);
        match(RB);
        return array;
    }
}

/* elements: element (',' element)* */
private void elements(List<Object> array) throws JSONException {
	element(array);
	while (forward.tokenType == COMMA){
		match(COMMA);
		elements(array);
	}
}

/* element: value */
private void element(List<Object> array) throws JSONException {
	array.add(value());
}
```
