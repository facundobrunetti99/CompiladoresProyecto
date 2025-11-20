package org.example.ast;

public class BinaryOpNode extends ExpressionNode {
    private ExpressionNode left;
    private String operator; // "+", "-", "*", "/"
    private ExpressionNode right;

    public BinaryOpNode(ExpressionNode left, String operator, ExpressionNode right) {
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
        return "BinaryOp[" + left + " " + operator + " " + right + "]";
    }
}