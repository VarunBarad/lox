package com.varunbarad.lox;

import java.util.List;

public class LoxClass implements LoxCallable {
    final String name;

    LoxClass(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Object call(Token paren, Interpreter interpreter, List<Object> arguments) {
        LoxInstance instance = new LoxInstance(this);
        return instance;
    }

    @Override
    public int arity() {
        return 0;
    }
}
