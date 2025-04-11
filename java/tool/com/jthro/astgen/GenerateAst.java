package tool.com.jthro.astgen;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

/**
 * Codegen class for the Expr and Stmt classes
 * Builds the abstract syntax tree for the lox language from format strings
 */
public class GenerateAst {

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      System.err.println("Usage: generate_ast <output directory>");
      System.exit(64);
    }

    String outputDir = args[0];
    defineAst(outputDir, "Expr", Arrays.asList(
        "Assign   : Token name, Expr value",
        "Binary   : Expr left, Token operator, Expr right",
        "Call     : Expr callee, Token paren, List<Expr> arguments",
        "Get      : Expr object, Token name",
        "Grouping : Expr expression",
        "Literal  : Object value",
        "Logical  : Expr left, Token operator, Expr right",
        "Set      : Expr object, Token name, Expr value",
        "Super    : Token keyword, Token method",
        "This     : Token keyword",
        "Unary    : Token operator, Expr right",
        "Variable : Token name"));

    defineAst(outputDir, "Stmt", Arrays.asList(
        "Block      : List<Stmt> statements",
        "Class      : Token name, Expr.Variable superclass, List<Stmt.Function> methods",
        "Expression : Expr expression",
        "Function   : Token name, List<Token> params," +
            " List<Stmt> body",
        "If         : Expr condition, Stmt thenBranch," +
            " Stmt elseBranch",
        "Print      : Expr expression",
        "Return     : Token keyword, Expr value",
        "Var        : Token name, Expr initializer",
        "While      : Expr condition, Stmt body"));
  }

  /**
   * Generate source code for an abstract syntax tree superclass
   *
   * @param outputDir        output directory for the source file
   * @param baseName         name of the superclass
   * @param types            list of all types in the superclass
   * @param classDescription javadoc description of the superclass
   */
  private static void defineAst(
      String outputDir, String baseName, List<String> types)
      throws IOException {
    String path = outputDir + "/" + baseName + ".java";
    PrintWriter writer = new PrintWriter(path, "UTF-8");

    writer.println("package com.jthro.lox;");
    writer.println();
    writer.println("import java.util.List;");
    writer.println();
    genJavadoc(writer, "Lox " + baseName + " visitor pattern", 0);
    writer.println("abstract class " + baseName + " {");
    writer.println();

    defineVisitor(writer, baseName, types);

    for (String type : types) {
      String className = type.split(":")[0].trim();
      String fields = type.split(":")[1].trim();
      defineType(writer, baseName, className, fields);
    }

    writer.println();
    writer.println("    abstract <R> R accept(Visitor<R> visitor);");

    writer.println("}");
    writer.close();
  }

  /**
   * Generate source code for a visitor interface
   *
   * @param writer   writer to the destination of the source code
   * @param baseName name of the superclass this belongs to
   * @param types    types the visitor must visit
   */
  private static void defineVisitor(
      PrintWriter writer, String baseName, List<String> types) {

    genJavadoc(writer, "Visitor interface for Lox " + baseName, 1);
    writer.println("    interface Visitor<R> {");

    for (String type : types) {
      String typeName = type.split(":")[0].trim();
      writer.println("        R visit" + typeName + baseName + "(" +
          typeName + " " + baseName.toLowerCase() + ");");
    }

    writer.println("    }");
    writer.println();
  }

  /**
   * Generate source code for a type class
   *
   * @param writer    to the destination of the source code
   * @param baseName  name of the superclass this belongs to
   * @param className name of the type
   * @param fieldList list of fields for the type class
   */
  private static void defineType(
      PrintWriter writer, String baseName,
      String className, String fieldList) {

    genJavadoc(writer, "Visitable type lox " + className, 1);
    writer.println("    static class " + className + " extends " +
        baseName + " {");

    // Constructor.
    writer.println("        " + className + "(" + fieldList + ") {");

    // Store parameters in fields.
    String[] fields = fieldList.split(", ");
    for (String field : fields) {
      String name = field.split(" ")[1];
      writer.println("            this." + name + " = " + name + ";");
    }

    writer.println("        }");

    // Fields.
    writer.println();
    for (String field : fields) {
      writer.println("        final " + field + ";");
    }

    writer.println();
    writer.println("        @Override");
    writer.println("        <R> R accept(Visitor<R> visitor) {");
    writer.println("            return visitor.visit" +
        className + baseName + "(this);");
    writer.println("        }");

    writer.println("    }");
    writer.println();
  }

  /**
   * Helper to generate javadoc comments
   *
   * @param writer      writer to the destination of the source code
   * @param doc         documentation to write
   * @param indentLevel indentation level
   */
  private static void genJavadoc(PrintWriter writer, String doc, int indentLevel) {
    String indent = " ".repeat(indentLevel * 4);
    writer.println(indent + "/** ");
    writer.println(indent + "* " + doc);
    writer.println(indent + "*/");
  }

}
