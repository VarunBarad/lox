package com.varunbarad.lox;

import java.util.ArrayList;
import java.util.List;

public class AstPrinter implements Expr.Visitor<String> {
    public static void main(String[] args) {
        List<Expr> argumentsAdd = new ArrayList<>();
        argumentsAdd.add(new Expr.Literal(1));
        argumentsAdd.add(new Expr.Literal(2));
        Expr add = new Expr.Call(
                new Expr.Literal("add"),
                new Token(TokenType.LEFT_PAREN, "(", null, 1),
                argumentsAdd
        );
        List<Expr> argumentsMultiply = new ArrayList<>();
        argumentsMultiply.add(add);
        argumentsMultiply.add(new Expr.Literal(5));
        Expr multiply = new Expr.Call(
                new Expr.Literal("multiply"),
                new Token(TokenType.LEFT_PAREN, "(", null, 1),
                argumentsMultiply
        );

        AstPrinter printer = new AstPrinter();
        System.out.println(printer.print(multiply));
    }

    String print(Expr expr) {
        return expr.accept(this);
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }

    @Override
    public String visitAssignExpr(Expr.Assign expr) {
        return "(assign " + expr.value.accept(this) + " to " + expr.name.lexeme + ")";
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitCallExpr(Expr.Call expr) {
        StringBuilder builder = new StringBuilder();

        builder.append("(call ").append(expr.callee.accept(this));
        for (Expr argument : expr.arguments) {
            builder.append(" ");
            builder.append(argument.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null) {
            return "nil";
        } else {
            return expr.value.toString();
        }
    }

    @Override
    public String visitLogicalExpr(Expr.Logical expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        return expr.name.lexeme;
    }
}
