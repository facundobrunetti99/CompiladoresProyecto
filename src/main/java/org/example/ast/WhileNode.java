package org.example.ast;

import java.util.List;

/**
 * Nodo AST para la sentencia WHILE
 */
public class WhileNode extends StatementNode {
    private ExpressionNode condition;
    private List<StatementNode> body;

    public WhileNode(ExpressionNode condition, List<StatementNode> body) {
        this.condition = condition;
        this.body = body;
    }

    public ExpressionNode getCondition() { return condition; }
    public List<StatementNode> getBody() { return body; }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "While[condition=" + condition + ", body=" + body.size() + " stmts]";
    }
}