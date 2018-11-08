# 解析 Number

解析数字又是一个头大的问题，有多头大。验证数字是否正确竟然会是 leetcode 第一个难题。。。
![](https://www.tuchuang001.com/images/2018/11/01/QQ20181101-110407.png)

你去看 JSON 关于数字的规则也是挺烦的。
![](https://www.json.org/number.gif)

用 EBNF 范式表示
```
number
    int frac exp

int
    digit
    onenine digits
    '-' digit
    '-' onenine digits

digits
    digit
    digit digits

digit
    '0'
    onenine

onenine
    '1' . '9'

frac
    ""
    '.' digits

exp
    ""
    'E' sign digits
    'e' sign digits

sign
    ""
    '+'
    '-'
```

有规则按照思路去写就会比较简单。解析数字的难点也在于 Lexer 。如果是自己想学习就尝试根据规则去写 Lexer。而不是直接抄代码。这才能检验自己是否学会了。

然后按照规则办事。

```java
/* num: int frac exp */
private Token scanNum() throws InvalidCharacterException {
	assert(c == '-' || isDigit());

	StringBuilder sb = new StringBuilder();
	scanInt(sb);
	scanFrac(sb);
	scanExp(sb);
	if(isSeparatorChar()){
		return new Token(TokenType.NUM,sb.toString());
	}
	throw new InvalidCharacterException("invalid num "+ sb.toString());
}

/* int: digit | onenine digits |  '-' digit | '-' onenine digits */
private void scanInt(StringBuilder sb){
	if(c == '-'){
		sb.append(c);
		nextChar();
	}
	if(c == '0'){
		sb.append(c);
		nextChar();
	}else {
		while(isDigit()){
			sb.append(c);
			nextChar();
		}
	}
}

/* frac: '' || '.' digits */
private void scanFrac(StringBuilder sb){
	if(c=='.'){
		sb.append(c);
		nextChar();
		while (isDigit()){
			sb.append(c);
			nextChar();
		}
	}
}

/*exp: "" | ("E"|'e') sign digits*/
private void scanExp(StringBuilder sb) throws InvalidCharacterException {
	if(c != 'e' && c != 'E')
		return;

	sb.append(c);
	nextChar();

	if(c == '+' || c=='-'){
		sb.append(c);
		nextChar();
	}

	//如果 E 后没有数字的话，是非法的数字，比如 10E
	if(!isDigit()){
		throw new InvalidCharacterException("invalid num "+sb.toString());
	}

	while (isDigit()){
		sb.append(c);
		nextChar();
	}
}
```

当然 getNextToken 会是这样的
```java
public Token getNextToken()
    //  ...
    case '-':
        return scanNum();
    default:
        if(isDigit()) return scanNum();
            throw new InvalidCharacterException("invalid character: " + c);
}
```
# Parser

测试会是这样的,除了考虑正常情况外，还要考虑 overflow 的问题。

```java
@Test
public void parseDouble() throws JSONException {
	assertThat(new Parser("3.14159E10").parse())
		.isEqualTo(3.14159E10);

	assertThatThrownBy(() -> new Parser("3E308").parse())
		.isInstanceOf(NumberParseException.class);
}
```

parser 也简单。
```java
private Object value() throws JSONException {
	//..
	case NUM:
		return Double.valueOf(token.text);;
	//..
}
```
