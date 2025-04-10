package com.jthro.lox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Semantically resolves every variable in the program before running it
 * This ensures for example that closure environments don't get overwritten and
 * other unwanted side effects from JIT resolution
 */
public class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    /**  This allows us to check for the case where a variable's initializer references itself.
     * false means declared, true means initialized.
     * If during an initialization a name is found but set to false, that means it is declared but not initialized.
     * .. i.e, we are inside its own initializer.
     */
    private FunctionType currentFunction = FunctionType.NONE;

    public Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    private enum FunctionType {
        NONE,
        FUNCTION,
        METHOD,
        INITIALIZER
    }

    private enum ClassType {
        NONE,
        CLASS
    }

    private ClassType currentClass = ClassType.NONE;

    /**
     * Semantically resolve all names in a list of statements
     */
    public void resolve(List<Stmt> statements) {
        for (Stmt statement : statements) {
            resolve(statement);
        }
    }

    private void resolve(Stmt stmt) {
        stmt.accept(this);
    }

    private void resolve(Expr expr) {
        expr.accept(this);
    }

    /**
     * Resolve a function's parameters and all names in its body inside a scope
     * @param function function to resolve
     * @param type function or method
     */
    private void resolveFunction(Stmt.Function function, FunctionType type) {
        FunctionType enclosingFunction  = currentFunction;
        currentFunction = type;

        beginScope();
        for (Token param : function.params) {
            declare(param);
            define(param);
        }
        resolve(function.body);
        endScope();
        currentFunction = enclosingFunction;
    }

    /**
     * Enter a new scope by pushing one onto the scope stack
     */
    private void beginScope() {
        scopes.push(new HashMap<String, Boolean>());
    }

    /**
     * Discard the current scope by popping it from the scope stack
     */
    private void endScope() {
        scopes.pop();
    }

    /**
     * Declare the existence of a name
     * Compiler error if the name already exists in this scope
     * If there are no scopes, return as it is a global
     * @param name name to declare
     */
    private void declare(Token name) {
        if (scopes.isEmpty()) return;

        Map<String, Boolean> scope = scopes.peek();
        if (scope.containsKey(name.lexeme)) {
            Lox.error(name, "Already a variable with this name in this scope.");
        }
        scope.put(name.lexeme, false);
    }

    /**
     * Flag that a name has an associated expression
     * @param name name to map
     */
    private void define(Token name) {
        if (scopes.isEmpty()) return;
        scopes.peek().put(name.lexeme, true);
    }

    /**
     * Resolve a name in a scope to its associated expression in the interpreter
     * @param expr expression belonging to the name
     * @param name name to resolve
     */
    private void resolveLocal(Expr expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.lexeme)) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }
    }

    /**
     * Resolve all names in a block statement in a scope
     * @param stmt block statement to resolve
     */
    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        beginScope();
        resolve(stmt.statements);
        endScope();
        return null;
    }

    /**
     * Resolve a class name
     */
     @Override
     public Void visitClassStmt(Stmt.Class stmt) {
         ClassType enclosingClass = currentClass;
         currentClass = ClassType.CLASS;

         declare(stmt.name);
         define(stmt.name);

         beginScope();
         scopes.peek().put("this", true);

         for (Stmt.Function method : stmt.methods) {
             FunctionType declaration = FunctionType.METHOD;
             if (method.name.lexeme.equals("init")) {
                 declaration = FunctionType.INITIALIZER;
             }
             resolveFunction(method, declaration);
         }

         endScope();
         currentClass = enclosingClass;
         return null;
     }

    /**
     * Resolve all names in an expression statement
     * @param stmt expression statement to resolve
     */
    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        resolve(stmt.expression);
        return null;
    }

    /**
     * Resolve all names in a function declaration
     * @param stmt function statement to resolve
     */
    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        declare(stmt.name);
        define(stmt.name);

        resolveFunction(stmt, FunctionType.FUNCTION);
        return null;
    }

    /**
     * Resolve all names in an if statement
     * Condition, then branch, else branch
     * @param stmt if statement to resolve
     */
    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        resolve(stmt.condition);
        resolve(stmt.thenBranch);
        if (stmt.elseBranch != null) {
            resolve(stmt.elseBranch);
        }
        return null;
    }

    /**
     * Resolve all names in a print statement
     * @param stmt print statement to resolve
     */
    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        resolve(stmt.expression);
        return null;
    }

    /**
     * Resolve all names in a return statement
     * If we are outside of a function, comiler error
     * @param stmt return statement to resolve
     */
    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        if (currentFunction == FunctionType.NONE) {
            Lox.error(stmt.keyword, "Cannot return from top-level code.");
        }
        if (stmt.value != null) {
            if (currentFunction == FunctionType.INITIALIZER) {
                Lox.error(stmt.keyword, "Cannot return a value from an initializer.");
            }
            resolve(stmt.value);
        }
        return null;
    }

    /**
     * Declare a variable statement, then define it if it has an initialiser
     * Otherwise, define it to nil
     * @param stmt variable statement to resolve
     */
    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        declare(stmt.name);
        if (stmt.initializer != null) {
            resolve(stmt.initializer);
        }
        define(stmt.name);
        return null;
    }

    /**
     * Resolve a while statement condition and body
     * @param stmt while statement to resolve
     */
    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        resolve(stmt.condition);
        resolve(stmt.body);
        return null;
    }

    /**
     * Resolve a variable name to its expression
     * @param expr variable expression to resolve
     */
    @Override
    public Void visitVariableExpr(Expr.Variable expr) {
        if (!scopes.isEmpty() && scopes.peek().get(expr.name.lexeme) == Boolean.FALSE) {
            Lox.error(expr.name, "Can't read local variable in its own initializer.");
        }

        resolveLocal(expr, expr.name);
        return null;
    }

    /**
     * Resolve an assignment expression to a name
     * @param expr assignment expression to resolve
     */
    @Override
    public Void visitAssignExpr(Expr.Assign expr) {
        resolve(expr.value);
        resolveLocal(expr, expr.name);
        return null;
    }

    /**
     * Resolve all names in a binary expression
     * @param expr binary expression to resolve
     */
    @Override
    public Void visitBinaryExpr(Expr.Binary expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    /**
     * Resolve all names in a function call expression
     * @param expr function call expression to resolve
     */
    @Override
    public Void visitCallExpr(Expr.Call expr) {
        resolve(expr.callee);
        for (Expr argument : expr.arguments) {
            resolve(argument);
        }
        return null;
    }

    /**
     * Resolve all names in a property get expression
     * @param expr property get expression to resolve
     */
    @Override
    public Void visitGetExpr(Expr.Get expr) {
        resolve(expr.object);
        return null;
    }

    /**
     * Resolve all names in a grouping expression
     * @param expr grouping expression to resolve
     */
    @Override
    public Void visitGroupingExpr(Expr.Grouping expr) {
        resolve(expr.expression);
        return null;
    }

    /**
     * Nothing to resolve in a literal expression, so do nothing
     */
    @Override
    public Void visitLiteralExpr(Expr.Literal expr) {
        return null;
    }

    /**
     * Resolve all names in a logical expression
     * @param expr logical expression to resolve
     */
    @Override
    public Void visitLogicalExpr(Expr.Logical expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    /**
     * Resolve all names in a class field set expression
     * @param field set expression to resolve
     */
    @Override
    public Void visitSetExpr(Expr.Set expr) {
        resolve(expr.value);
        resolve(expr.object);
        return null;
    }

    /**
     * Resolve the name "this"
     */
    @Override
    public Void visitThisExpr(Expr.This expr) {
        if (currentClass == ClassType.NONE) {
            Lox.error(expr.keyword, "Can't use 'this' outside of a class.");
            return null;
        }

        resolveLocal(expr, expr.keyword);
        return null;
    }

    /**
     * Resolve all names in a unary expression
     * @param expr unary expression to resolve
     */
    @Override
    public Void visitUnaryExpr(Expr.Unary expr) {
        resolve(expr.right);
        return null;
    }
}
