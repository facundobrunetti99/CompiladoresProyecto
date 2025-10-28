package org.example.ast;

/**
 * Interfaz Visitor para recorrer el AST
 * Incluye soporte para estructuras de control
 */
public interface ASTVisitor {
    // Nodos básicos
    void visit(ProgramNode node);
    void visit(MainFunctionNode node);
    void visit(VariableDeclarationNode node);
    void visit(AssignmentNode node);
    void visit(ReturnNode node);

    // Expresiones
    void visit(BinaryOpNode node);
    void visit(ComparisonNode node);
    void visit(NumberNode node);
    void visit(BooleanNode node);
    void visit(VariableNode node);

    // Estructuras de control
    void visit(IfNode node);
    void visit(WhileNode node);
}