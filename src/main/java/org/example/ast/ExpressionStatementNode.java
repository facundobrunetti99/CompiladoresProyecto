package org.example.ast;

public class ExpressionStatementNode extends StatementNode {
    private ExpressionNode expression;

    public ExpressionStatementNode(ExpressionNode expression) {
        this.expression = expression;
    }

    public ExpressionNode getExpression() { return expression; }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ExpressionStatement[" + expression + "]";
    }
}