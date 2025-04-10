package com.jthro.lox;

import java.util.List;

/**
 * Interface for any Lox object that can be called like a function
 */
interface LoxCallable {
    /**
     * Number of arguments
     */
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);
}
