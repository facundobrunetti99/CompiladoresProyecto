package org.example.ast;
import java.util.List;

public class MainFunctionNode extends ASTNode {
    private List<DeclarationNode> declarations;
    private List<StatementNode> statements;

    public MainFunctionNode(List<DeclarationNode> declarations, List<StatementNode> statements) {
        this.declarations = declarations;
        this.statements = statements;
    }

    public List<DeclarationNode> getDeclarations() { return declarations; }
    public List<StatementNode> getStatements() { return statements; }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "MainFunction[decls=" + declarations.size() + ", stmts=" + statements.size() + "]";
    }
}