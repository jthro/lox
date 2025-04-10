package com.jthro.lox;

/**
* Lox Token
*/
public class Token {
    final TokenType type;
    final String lexeme;
    /**
    * null if token is non-literal
    */
    final Object literal;
    /**
    * debugging metadata
    */
    final int line;

    Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal;
    }

}
