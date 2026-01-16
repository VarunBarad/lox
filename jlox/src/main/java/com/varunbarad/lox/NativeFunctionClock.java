package com.varunbarad.lox;

import java.util.List;

public class NativeFunctionClock implements LoxCallable {
    @Override
    public int arity() {
        return 0;
    }

    @Override
    public Object call(Token paren, Interpreter interpreter, List<Object> arguments) {
        return (double) System.currentTimeMillis() / 1000.0;
    }

    @Override
    public String toString() {
        return "<native fn clock>";
    }
}
