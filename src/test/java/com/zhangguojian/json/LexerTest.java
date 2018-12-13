package com.zhangguojian.json;

import com.zhangguojian.json.exception.InvalidCharacterException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LexerTest {

    private static Token scanText(String keyword, String input, Token token) throws Throwable {
        Lexer lexer = new Lexer(input);
        Method method = Lexer.class.getDeclaredMethod("scanText", String.class, Token.class);
        method.setAccessible(true);
        try {
            return (Token) method.invoke(lexer, keyword, token);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    @Test
    public void testNullInput(){
        String nullStr = null;
        assertThatThrownBy(() -> new Lexer(nullStr))
                .isInstanceOf(NullPointerException.class);

        StringReader reader = null;
        assertThatThrownBy(() -> new Lexer(reader))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testGetNullToken() throws InvalidCharacterException, IOException {
        assertThat(new Lexer("null").getNextToken())
                .isEqualTo(Token.NULL);

        assertThatThrownBy(() -> new Lexer("nul").getNextToken())
                .isInstanceOf(InvalidCharacterException.class);


        assertThatThrownBy(() -> new Lexer("nullf").getNextToken())
                .isInstanceOf(InvalidCharacterException.class);
    }

    @Test
    public void testEOF() throws InvalidCharacterException, IOException {
        Lexer lexer = new Lexer("\r \t \n ");
        Assert.assertEquals(lexer.getNextToken(), Token.EOF);
    }

    @Test
    public void TestInvalidCharacter() {
        assertThatThrownBy(() -> new Lexer("ko").getNextToken()).isInstanceOf(InvalidCharacterException.class);
    }

    @Test
    public void scanText() throws Throwable {
        assertThat(scanText("true", "true", Token.TRUE)).isEqualTo(Token.TRUE);

        assertThatThrownBy(() -> scanText("trued", "true", Token.TRUE))
                .isInstanceOf(InvalidCharacterException.class);

        assertThatThrownBy(() -> scanText("null", "n", Token.NULL))
                .isInstanceOf(InvalidCharacterException.class);
    }

    @Test
    public void testBoolean() throws InvalidCharacterException, IOException {
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

    @Test
    public void testString() throws InvalidCharacterException, IOException {
        assertThat(new Lexer("\"\"").getNextToken().text)
                .isEqualTo("");

        assertThat(new Lexer("\"Hello world\"").getNextToken().text).isEqualTo("Hello world");

        //测试转义符
        assertThat(new Lexer("\"\\/\\t\\n\\f\\b\\n\\r\\\\\"").getNextToken().text)
                .isEqualTo("/\t\n\f\b\n\r\\");

        //测试 uniode
        assertThat(new Lexer("\"\\u4f60\\u597d\\u4e16\\u754c\"").getNextToken().text)
                .isEqualTo("你好世界");

        //测试 UTF-8 BOOM
        assertThat(new Lexer("\"\\ufeffHello world\"").getNextToken().text).isEqualTo("Hello world");

        //字符串未完成
        assertThatThrownBy(() -> new Lexer("\"Hello world").getNextToken())
                .isInstanceOf(InvalidCharacterException.class);

        assertThatThrownBy(() -> new Lexer("\"").getNextToken())
                .isInstanceOf(InvalidCharacterException.class);

        //unicode 未完成
        assertThatThrownBy(() -> new Lexer("\"\\u123\"").getNextToken())
                .isInstanceOf(InvalidCharacterException.class);

        //不能有单独的 '\'
        assertThatThrownBy(() -> new Lexer("\"\\\"").getNextToken())
                .isInstanceOf(InvalidCharacterException.class);
        //而在字符串中不能有 '"' 在 Lexer 中很难做到。反而在 parse 中比较容易做。
        //assertThatThrownBy(() -> new Lexer("\"\"\"").getNextToken()).isInstanceOf(InvalidCharacterException.class);

        //测试不存在的转义符
        assertThatThrownBy(() -> new Lexer("\"\\k\"").getNextToken())
                .isInstanceOf(InvalidCharacterException.class);
    }

    @Test
    public void testNum() throws InvalidCharacterException, IOException {
        assertThat(new Lexer("0.14159").getNextToken().text).isEqualTo("0.14159");
        assertThat(new Lexer("12345").getNextToken().text).isEqualTo("12345");
        assertThat(new Lexer("-12345").getNextToken().text).isEqualTo("-12345");
        assertThat(new Lexer("-12E-10").getNextToken().text).isEqualTo("-12E-10");
        assertThat(new Lexer("2e10").getNextToken().text).isEqualTo("2e10");
        assertThat(new Lexer(" -90e3   ").getNextToken().text).isEqualTo("-90e3");

        assertThatThrownBy(() -> new Lexer("--6").getNextToken())
                .isInstanceOf(InvalidCharacterException.class);
        assertThatThrownBy(() -> new Lexer("e3").getNextToken())
                .isInstanceOf(InvalidCharacterException.class);
        assertThatThrownBy(() -> new Lexer("-12E").getNextToken())
                .isInstanceOf(InvalidCharacterException.class);
        assertThatThrownBy(() -> new Lexer("95a54e53").getNextToken())
                .isInstanceOf(InvalidCharacterException.class);
        assertThatThrownBy(() -> new Lexer("-12E1.12").getNextToken())
                .isInstanceOf(InvalidCharacterException.class);
    }

    @Test
    public void testArray() throws InvalidCharacterException, IOException {
        Lexer lexer = new Lexer("[1,2]");
        assertThat(lexer.getNextToken().tokenType).isEqualTo(TokenType.BEGIN_ARRAY);

        assertThat(lexer.getNextToken().tokenType).isEqualTo(TokenType.NUM);
        assertThat(lexer.getNextToken().tokenType).isEqualTo(TokenType.COMMA);

        assertThat(lexer.getNextToken().tokenType).isEqualTo(TokenType.NUM);

        assertThat(lexer.getNextToken().tokenType).isEqualTo(TokenType.END_ARRAY);
    }

    @Test
    public void testObject() throws InvalidCharacterException, IOException {
        Lexer lexer = new Lexer("{\"name\":\"John Smith\",\"age\":15 }");

        assertThat(lexer.getNextToken().tokenType).isEqualTo(TokenType.BEGIN_OBJ);
        assertThat(lexer.getNextToken().tokenType).isEqualTo(TokenType.STR);
        assertThat(lexer.getNextToken().tokenType).isEqualTo(TokenType.COLON);
        assertThat(lexer.getNextToken().tokenType).isEqualTo(TokenType.STR);
        assertThat(lexer.getNextToken().tokenType).isEqualTo(TokenType.COMMA);
        assertThat(lexer.getNextToken().tokenType).isEqualTo(TokenType.STR);
        assertThat(lexer.getNextToken().tokenType).isEqualTo(TokenType.COLON);
        assertThat(lexer.getNextToken().tokenType).isEqualTo(TokenType.NUM);
        assertThat(lexer.getNextToken().tokenType).isEqualTo(TokenType.END_OBJ);

    }

    @Test
    public void testLocationString() throws InvalidCharacterException, IOException {
        Lexer lexer = new Lexer("\r\t\"123123\"\n\"123\"\n\n");
        lexer.getNextToken();
        assertThat(lexer.location()).isEqualTo(" at line 1 column 10 ");
        lexer.getNextToken();
        assertThat(lexer.location()).isEqualTo(" at line 2 column 6 ");
    }
}