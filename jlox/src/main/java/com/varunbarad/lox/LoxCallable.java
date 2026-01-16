package com.varunbarad.lox;

import java.util.List;

public interface LoxCallable {
    int arity();

    Object call(Token paren, Interpreter interpreter, List<Object> arguments);
}
