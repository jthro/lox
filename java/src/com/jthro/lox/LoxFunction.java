package com.jthro.lox;

import java.util.List;

public class LoxFunction implements LoxCallable {
    private final Stmt.Function declaration;
    private final Environment closure;

    /**
     * @param declaration function declaration that declares this function
     * @param closure environment surrounding the declaration
     */
    LoxFunction(Stmt.Function declaration, Environment closure) {
        this.declaration = declaration;
        this.closure = closure;
    }

    /**
     * Bind parameter names to arguments and run the function
     * @param interpreter interpreter to run the function
     * @param arguments list of arguments passed into the function
     */
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

    /**
     * @return the expected number of arguments for the function
     */
    @Override
    public int arity() {
        return declaration.params.size();
    }

    /**
     * Convert the function into a string
     */
    @Override
    public String toString() {
        return "<fn " + declaration.name.lexeme + ">";
    }

}
