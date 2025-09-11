package org.example.ast;

public class AssignmentNode extends StatementNode {
    private String identifier;
    private ExpressionNode expression;

    public AssignmentNode(String identifier, ExpressionNode expression) {
        this.identifier = identifier;
        this.expression = expression;
    }

    public String getIdentifier() { return identifier; }
    public ExpressionNode getExpression() { return expression; }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Assignment[" + identifier + " = " + expression + "]";
    }
}