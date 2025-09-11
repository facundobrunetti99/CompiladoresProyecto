package org.example.ast;

public class VariableNode extends ExpressionNode {
    private String identifier;

    public VariableNode(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() { return identifier; }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Variable[" + identifier + "]";
    }
}