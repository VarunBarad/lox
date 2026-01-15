package com.varunbarad.lox;

import java.util.ArrayList;
import java.util.List;

public class RpnPrinter implements Expr.Visitor<String> {
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

        RpnPrinter printer = new RpnPrinter();
        System.out.println(printer.print(multiply));
    }

    String print(Expr expr) {
        return expr.accept(this);
    }

    private String notate(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(");
        for (Expr expr : exprs) {
            builder.append(expr.accept(this));
            builder.append(" ");
        }
        builder.append(name);
        builder.append(")");

        return builder.toString();
    }

    @Override
    public String visitAssignExpr(Expr.Assign expr) {
        return "(" + expr.value.accept(this) + " " + expr.name.lexeme + " =)";
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return notate(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitCallExpr(Expr.Call expr) {
        StringBuilder builder = new StringBuilder();

        builder.append("(");
        for (Expr argument : expr.arguments) {
            builder.append(argument.accept(this));
            builder.append(" ");
        }
        builder.append(expr.callee.accept(this));
        builder.append(" ");
        builder.append(expr.arguments.size());
        builder.append(" call)");

        return builder.toString();
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return expr.expression.accept(this);
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
        return notate(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return notate(expr.operator.lexeme, expr.right);
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        return expr.name.lexeme;
    }
}
