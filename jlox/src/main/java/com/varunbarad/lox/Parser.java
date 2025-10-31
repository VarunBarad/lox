package com.varunbarad.lox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.varunbarad.lox.TokenType.*;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(statement());
        }

        return statements;
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private Token consume(TokenType type, String message) throws ParseError {
        if (check(type)) {
            return advance();
        }

        throw error(peek(), message);
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) {
            return false;
        } else {
            return peek().type == type;
        }
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private ParseError error(Token token, String message) {
        Lox.error(token, message);
        return new ParseError();
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) {
                return;
            }

            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN: {
                    return;
                }
            }

            advance();
        }
    }

    private Expr leftAssociativeBinaryOperator(Supplier<Expr> simplifier, TokenType... operators) {
        Expr expr = simplifier.get();

        while (match(operators)) {
            Token operator = previous();
            Expr right = simplifier.get();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr expressionSeries() throws ParseError {
        return leftAssociativeBinaryOperator(this::expression, COMMA);
    }

    private Expr expression() throws ParseError {
        return equality();
    }

    private Stmt statement() {
        if (match(PRINT)) {
            return printStatement();
        } else {
            return expressionStatement();
        }
    }

    private Stmt printStatement() {
        Expr value = expression();
        consume(SEMICOLON, "Expect ';' after value.");
        return new Stmt.Print(value);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after expression.");
        return new Stmt.Expression(expr);
    }

    private Expr equality() throws ParseError {
        /*
        Expr expr = comparison();

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
         */
        return leftAssociativeBinaryOperator(this::comparison, BANG_EQUAL, EQUAL_EQUAL);
    }

    private Expr comparison() throws ParseError {
        /*
        Expr expr = term();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
         */

        return leftAssociativeBinaryOperator(this::term, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL);
    }

    private Expr term() throws ParseError {
        return leftAssociativeBinaryOperator(this::factor, MINUS, PLUS);
    }

    private Expr factor() throws ParseError {
        return leftAssociativeBinaryOperator(this::unary, SLASH, STAR);
    }

    private Expr unary() throws ParseError {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return primary();
    }

    private Expr primary() throws ParseError {
        if (match(FALSE)) {
            return new Expr.Literal(false);
        } else if (match(TRUE)) {
            return new Expr.Literal(true);
        } else if (match(NIL)) {
            return new Expr.Literal(null);
        } else if (match(NUMBER, STRING)) {
            return new Expr.Literal(previous().literal);
        } else if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        } else {
            throw error(peek(), "Expect expression.");
        }
    }

    private static class ParseError extends RuntimeException {
    }
}
