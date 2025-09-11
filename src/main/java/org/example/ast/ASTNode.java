package org.example.ast;

public abstract class ASTNode {
    public abstract void accept(ASTVisitor visitor);
    
    @Override
    public abstract String toString();
}