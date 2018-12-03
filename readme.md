# 解析 String

Boolean 类型和 null类型不同。String 类型的 Lexer 相对来讲会难一点。这是从 JSON 的 String 的定义决定的。
![](https://www.json.org/string.gif)

由图可以看到 String 类型里面的字符可以是
- 可以除`"`和`\`字符外的所有 unicode集合
- `"`和`\`会通过转义符用 `\"`,`\\` 表示
- tab 用 `\\t` 表示，换行用 `\\n` 表示等
- 处理 unicode的转义。用`\\u`后面有4位的16进制数就是unicode的转义了。比如,你要`JSON.parse("\"\\u4f60\\u597d\\u4e16\\u754c\"")`结果是“你好世界”。这规则真的有点头大。
<!--more-->

而用 EBNF 来描述会是这样的
```
string
    '"' characters '"'
characters
    ""
    character characters
character
    '0020' . '10ffff' - '"' - '\'
    '\' escape
escape
    '"'
    '\'
    '/'
    'b'
    'n'
    'r'
    't'
    'u' hex hex hex hex

hex
    digit
    'A' . 'F'
    'a' . 'f'
```

# Lexer

按测试驱动开发的那些概念，他们会告诉你要先测试再写实现。目的主要是在编码之前，用测试用例先让自己了解这个项目，想清楚如何设计，反正看需求看文档还要花点做笔记什么的，为何不用这些时间去写测试用例呢？觉得还是有一定的道理的。

所以我会先写下测试。

```java
@Test
public void testString() throws InvalidCharacterException {
    assertThat(new Lexer("\"\"").getNextToken().text)
        .isEqualTo("");

    assertThat(new Lexer("\"Hello world\"").getNextToken().text)
        .isEqualTo("Hello world");
    //字符串未完成
    assertThatThrownBy(() -> new Lexer("\"Hello world").getNextToken())
        .isInstanceOf(InvalidCharacterException.class);

    //不能有单独的 '\'
    assertThatThrownBy(() -> new Lexer("\"\\\"").getNextToken())
        .isInstanceOf(InvalidCharacterException.class);
    //而在字符串中不能有 '"' 在 Lexer 中很难做到。反而在 parse 中比较容易做。
    //assertThatThrownBy(() -> new Lexer("\"\"\"").getNextToken()).isInstanceO(InvalidCharacterException.class);

    //测试转义符
    assertThat(new Lexer("\"\\r\\n\\b\\f\\\\\\/Hello\\tworld\"").getNextToken().text)
        .isEqualTo("\r\n\b\f\\/Hello\tworld");

    //测试 uniode
    assertThat(new Lexer("\"\\u4f60\\u597d\\u4e16\\u754c\"").getNextToken().text)
        .isEqualTo("你好世界");

    //错误的 unicode 只有三位 Hex 码
    assertThatThrownBy(() -> new Lexer("\"\\u4f6\"").getNextToken())
        .isInstanceOf(InvalidCharacterException.class);

    //错误的转义
    assertThatThrownBy(() -> new Lexer("\"\\k\"").getNextToken())
        .isInstanceOf(InvalidCharacterException.class);
}
```

根据定义的会写下这样的代码

```java
private Token scanString() throws InvalidCharacterException {
    //char 中 '\"' 和 '"' 和一样的，\" 只是在字符串中才需要转义
    //因为这个函数是内部调用的，而且只有在c == '"' 才会调用的，
    //直接断言一下就行了，不用写过多的防御性编程。
    assert (c == '"');

    StringBuilder sb = new StringBuilder();
    sb.append(sb);
    nextChar();

    while (c != EOF) {
        if (c =='"') {//再次遇到 '"' 才表示字符串结束
            nextChar();
            return new Token(TokenType.STR, sb.toString());
        } else if (c == '\\') {
            nextChar();
            //处理转义
            ESCAPE(sb);
        } else {
            sb.append(c);
            nextChar();
        }
    }
    throw new InvalidCharacterException("invalid token: " + sb.toString());
}

private void ESCAPE(StringBuilder sb) throws InvalidCharacterException {
    switch (c) {
        case '\"':
            sb.append('\"');
            break;
        case 'r':
            sb.append('\r');
            break;
        case 'n':
            sb.append('\n');
            break;
        case 'f':
            sb.append('\f');
            break;
        case 'b':
            sb.append('\b');
            break;
        case 't':
            sb.append('\t');
            break;
        case '/':
            sb.append('/');
            break;
        case 'u':
            //处理 unicode 转义
            nextChar();
            int i = 0;
            StringBuilder unicode = new StringBuilder("");
            while (i < 4 && isHex()) {
                unicode.append(c);
                nextChar();
                i++;
            }

            if (i == 4) {
                String unicodeStr = unicode.toString();
                sb.append((char) Integer.parseInt(unicodeStr, 16));
                return;
            }
            throw new InvalidCharacterException("invalid token: " + unicode.toString());
        case '\\':
            sb.append('\\');
            break;
        default:
            throw new InvalidCharacterException("invalid token: " + sb.toString());
    }
    nextChar();
}

private boolean isHex() {
    return c >= '0' && c <= '9' || c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F';
}
```

说难也不难，跟着思路走就 ok 了。


# parser

之前提到在 Lexer 字符串中，其实挺难判断 `"Hello""` 是错误的 JSON 字符串的。但是在 Parse 中就比较容易了。(只解析字符串)，字符串后面会紧接着 EOF！。所以 getNextToken 之后再判断后面的 token 就ok了。

所以会写下这样的代码

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
        case STR:
            String value = token.text;
            if(input.getNextToken() != Token.EOF){
                throw new NoViableTokenException("Unexpected token is " + token.tokenType);
            }
            return value;
        default:
            throw new NoViableTokenException("Unexpected token is " + token.tokenType);
	}
}
```

但实际上不止 STR 类型需要这段代码，TRUE，NULL 类型也需要的。因为按原来的解析的话。输入"true true" 这样的字符串也能正确解析的（这不是测试驱动开发的问题，所有测试都有这种问题的，你想不到的用例自然测不到，也没什么好批判的），但实际情况是这种字符串是语法错误的。所以我会改成这样。

```java
Object parse() throws JSONException {
    Object value = value();
    Token token = lexer.getNextToken();
    if( token!= Token.EOF){
        throw new NoViableTokenException("Unexpected token is " + token.tokenType);
    }
    return value;
}

private Object value() throws JSONException {
    Token token = lexer.getNextToken();
    switch (token.tokenType){
        case NULL:
            return null;
        case TRUE:
            return Boolean.TRUE;
        case FALSE:
            return Boolean.FALSE;
        case STR:
            return token.text;
        default:
            throw new NoViableTokenException("Unexpected token is " + token.tokenType);
    }
}
```

测试用例也会变成这样。

```java
@Test
public void parseString() throws JSONException {
    assertThatThrownBy(() -> new Parser("\"\"\"").parse())
        .isInstanceOf(InvalidCharacterException.class);

    assertThat(new Parser("\"hello world\"").parse())
        .isEqualTo("hello world");
}
```

以上就是解析字符串的部分