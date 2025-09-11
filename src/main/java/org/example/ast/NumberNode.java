package org.example.ast;

public class NumberNode extends ExpressionNode {
    private int value;

    public NumberNode(int value) {
        this.value = value;
    }

    public int getValue() { return value; }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Number[" + value + "]";
    }
}