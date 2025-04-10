package com.jthro.lox;

import java.util.HashMap;
import java.util.Map;

/**
 * Lox variable environment class
 */
public class Environment {
    /**
     * Environment enclosing this environment
     */
    final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();

    public Environment() {
        enclosing = null;
    }

    /**
     * Allows the chaining of environments
     */
    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    /**
     * Search for a name in the current and all enclosing scopes
     * @param name name to search for
     * @return object associated with the name
     */
    public Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        if (enclosing != null)
            return enclosing.get(name);

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    /**
     * Bind a name to a value in the current environment
     * If it is not in the current environment, proceed up the enclosing chain
     * Because of shadowing rules, only the innermost name should be redefined
     */
    void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name, "Undefined Variable '" + name.lexeme + "'.");
    }

    /**
     * Bind value to name in current scope
     */
    void define(String name, Object value) {
        values.put(name, value);
    }

    /**
     * Get the nth enclosing environment around this one
     * @param distance how far up the scope chain to climb
     */
    public Environment ancestor(int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }
        return environment;
    }

    /**
     * Get the value matching a name in the nth enclosing scope
     * @param distance how far up the scope chain to climb
     */
    Object getAt(int distance, String name) {
        return ancestor(distance).values.get(name);
    }

    /**
     * Assign a value to a name in the nth enclosing scope
     * @param distance how far up the scope chain to climb
     * @param name name to assign to
     * @param value value to assign
     */
    void assignAt(int distance, Token name, Object value) {
        ancestor(distance).values.put(name.lexeme, value);
    }
}
