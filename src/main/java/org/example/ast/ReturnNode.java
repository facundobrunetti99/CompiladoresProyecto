package org.example.ast;

public class ReturnNode extends StatementNode {
    private ExpressionNode expression; // puede ser null para return sin expresión

    public ReturnNode(ExpressionNode expression) {
        this.expression = expression;
    }

    public ExpressionNode getExpression() { return expression; }
    public boolean hasExpression() { return expression != null; }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Return[" + (expression != null ? expression : "void") + "]";
    }
}