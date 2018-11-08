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

这部分 Lexer 比较简单，尝试自己写出来吧。

# parse
```java
/* array: '[' ']' | '[' elements '] */
private List<Object> parseArray() throws JSONException {
	match(LB);

	List<Object> array = new ArrayList<>();
	if(forward.tokenType == RB){
		match(RB);
		return array;
	}else {
		elements(array);
		match(RB);
	}
	return array;
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