package org.example.ast;

public class VariableDeclarationNode extends DeclarationNode {
    public VariableDeclarationNode(String type, String identifier) {
        super(type, identifier);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "VarDecl[" + type + " " + identifier + "]";
    }
}