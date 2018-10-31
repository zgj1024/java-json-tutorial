package com.zhangguojian.json;

import com.zhangguojian.json.exception.InvalidCharacterException;
import org.junit.Assert;
import org.junit.Test;

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
    public void testGetNullToken() throws InvalidCharacterException {
        assertThat(new Lexer("null").getNextToken())
                .isEqualTo(Token.NULL);

        assertThatThrownBy(() -> new Lexer("nul").getNextToken())
                .isInstanceOf(InvalidCharacterException.class);


        assertThatThrownBy(() -> new Lexer("nullf").getNextToken())
                .isInstanceOf(InvalidCharacterException.class);
    }



    @Test
    public void testEOF() throws InvalidCharacterException {
        Lexer lexer = new Lexer("\r \t \n");
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
}