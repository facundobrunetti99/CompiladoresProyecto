package org.example.ast;

public class BooleanNode extends ExpressionNode {
    private boolean value;

    public BooleanNode(boolean value) {
        this.value = value;
    }

    public boolean getValue() { return value; }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Boolean[" + value + "]";
    }
}