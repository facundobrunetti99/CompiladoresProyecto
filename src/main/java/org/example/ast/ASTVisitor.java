package org.example.ast;

public interface ASTVisitor {
    void visit(ProgramNode node);
    void visit(FunctionDeclarationNode node);
    void visit(ParameterNode node);
    void visit(MainFunctionNode node);
    void visit(VariableDeclarationNode node);
    void visit(AssignmentNode node);
    void visit(IfNode node);
    void visit(WhileNode node);
    void visit(ReturnNode node);
    void visit(BinaryOpNode node);
    void visit(ComparisonNode node);
    void visit(LogicalOpNode node);
    void visit(NumberNode node);
    void visit(BooleanNode node);
    void visit(VariableNode node);
    void visit(FunctionCallNode node);
    void visit(ExpressionStatementNode node);
}