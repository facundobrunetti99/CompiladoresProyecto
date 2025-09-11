package org.example.ast;

public interface ASTVisitor {
    void visit(ProgramNode node);
    void visit(MainFunctionNode node);
    void visit(VariableDeclarationNode node);
    void visit(AssignmentNode node);
    void visit(ReturnNode node);
    void visit(BinaryOpNode node);
    void visit(NumberNode node);
    void visit(BooleanNode node);
    void visit(VariableNode node);
}