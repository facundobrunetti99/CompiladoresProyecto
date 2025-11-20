package org.example.ast;

/**
 * Nodo AST para operaciones lógicas (&&, ||)
 */
public class LogicalOpNode extends ExpressionNode {
    private ExpressionNode left;
    private String operator; // "&&" or "||"
    private ExpressionNode right;

    public LogicalOpNode(ExpressionNode left, String operator, ExpressionNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public ExpressionNode getLeft() { return left; }
    public String getOperator() { return operator; }
    public ExpressionNode getRight() { return right; }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "LogicalOp[" + left + " " + operator + " " + right + "]";
    }
}