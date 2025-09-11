package org.example.ast;
public abstract class DeclarationNode extends ASTNode {
    protected String type;
    protected String identifier;

    public DeclarationNode(String type, String identifier) {
        this.type = type;
        this.identifier = identifier;
    }

    public String getType() { return type; }
    public String getIdentifier() { return identifier; }
}