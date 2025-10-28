package org.example.ast;

/**
 * Nodo AST para operaciones de comparación (==, !=, <, >, <=, >=)
 */
public class ComparisonNode extends ExpressionNode {
    private ExpressionNode left;
    private String operator;
    private ExpressionNode right;

    public ComparisonNode(ExpressionNode left, String operator, ExpressionNode right) {
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
        return "Compare[" + left + " " + operator + " " + right + "]";
    }
}