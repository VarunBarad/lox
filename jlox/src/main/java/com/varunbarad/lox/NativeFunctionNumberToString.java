package com.varunbarad.lox;

import java.util.List;

public class NativeFunctionNumberToString implements LoxCallable {
    @Override
    public int arity() {
        return 1;
    }

    @Override
    public Object call(Token paren, Interpreter interpreter, List<Object> arguments) {
        Object arg = arguments.getFirst();
        if (arg instanceof Double) {
            return Double.toString((double) arg);
        } else {
            throw new RuntimeError(paren, "Argument must be a number, received: " + arg.toString());
        }
    }

    @Override
    public String toString() {
        return "<native fn number_to_string>";
    }
}
