package com.jthro.lox;

import java.util.List;

public class LoxFunction implements LoxCallable {
    private final String name;
    private final Expr.Lambda declaration;
    private final Environment closure;

    LoxFunction(String name, Expr.Lambda declaration, Environment closure) {
        this.name = name;
        this.declaration = declaration;
        this.closure = closure;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(closure);
        for (int i = 0; i < declaration.params.size(); i++) {
            environment.define(declaration.params.get(i).lexeme, arguments.get(i));
        }

        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch (Return returnValue) {
            return returnValue.value;
        }
        return null;
    }

    @Override
    public int arity() {
        return declaration.params.size();
    }

    @Override
    public String toString() {
        return "<fn " + name + ">";
    }

}
