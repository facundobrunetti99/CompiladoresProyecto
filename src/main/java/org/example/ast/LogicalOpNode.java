package org.example.ast;

public class LogicalOpNode extends ExpressionNode {
    private ExpressionNode left;
    private String operator; // "&&", "||", "!"
    private ExpressionNode right; // null para "!"

    // Constructor para operadores binarios (&&, ||)
    public LogicalOpNode(ExpressionNode left, String operator, ExpressionNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    // Constructor para operador unario (!)
    public LogicalOpNode(String operator, ExpressionNode operand) {
        this.left = operand;
        this.operator = operator;
        this.right = null;
    }

    public ExpressionNode getLeft() { return left; }
    public String getOperator() { return operator; }
    public ExpressionNode getRight() { return right; }
    public boolean isUnary() { return right == null && operator.equals("!"); }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        if (isUnary()) {
            return "LogicalOp[" + operator + left + "]";
        }
        return "LogicalOp[" + left + " " + operator + " " + right + "]";
    }
}