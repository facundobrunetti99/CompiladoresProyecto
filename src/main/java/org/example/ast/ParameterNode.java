package org.example.ast;

public class ParameterNode extends ASTNode {
    private String type;
    private String identifier;

    public ParameterNode(String type, String identifier) {
        this.type = type;
        this.identifier = identifier;
    }

    public String getType() { return type; }
    public String getIdentifier() { return identifier; }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Parameter[" + type + " " + identifier + "]";
    }
}