package com.jthro.lox;

import java.util.List;

public class LoxFunction implements LoxCallable {
    private final Stmt.Function declaration;
    private final Environment closure;
    private final boolean isInitializer;

    /**
     * @param declaration function declaration that declares this function
     * @param closure environment surrounding the declaration
     * @param isInitializer whether the function is a class initializer
     */
    LoxFunction(Stmt.Function declaration, Environment closure, boolean isInitializer) {
        this.declaration = declaration;
        this.closure = closure;
        this.isInitializer = isInitializer;
    }

    /**
     * Bind a method to an instance to refer to as 'this'
     * @param instance class instance this method belongs to
     */
    LoxFunction bind(LoxInstance instance) {
        Environment environment = new Environment(closure);
        environment.define("this", instance);
        return new LoxFunction(declaration, environment, isInitializer);
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
            if (isInitializer) return closure.getAt(0, "this");
            return returnValue.value;
        }

        if (isInitializer) return closure.getAt(0, "this");
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
