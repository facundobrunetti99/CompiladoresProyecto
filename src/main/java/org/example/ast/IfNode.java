package org.example.ast;

import java.util.List;

/**
 * Nodo AST para la sentencia IF-ELSE
 */
public class IfNode extends StatementNode {
    private ExpressionNode condition;
    private List<StatementNode> thenBlock;
    private List<StatementNode> elseBlock; // Puede ser null si no hay else

    public IfNode(ExpressionNode condition, List<StatementNode> thenBlock, List<StatementNode> elseBlock) {
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
    }

    public ExpressionNode getCondition() { return condition; }
    public List<StatementNode> getThenBlock() { return thenBlock; }
    public List<StatementNode> getElseBlock() { return elseBlock; }
    public boolean hasElse() { return elseBlock != null && !elseBlock.isEmpty(); }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        String result = "If[condition=" + condition + ", then=" + thenBlock.size() + " stmts";
        if (hasElse()) {
            result += ", else=" + elseBlock.size() + " stmts";
        }
        return result + "]";
    }
}