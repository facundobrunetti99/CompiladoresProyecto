package org.example.ast;

import java.util.List;

public class FunctionCallNode extends ExpressionNode {
    private String functionName;
    private List<ExpressionNode> arguments;

    public FunctionCallNode(String functionName, List<ExpressionNode> arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
    }

    public String getFunctionName() { return functionName; }
    public List<ExpressionNode> getArguments() { return arguments; }
    public int getArgumentCount() { return arguments.size(); }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "FunctionCall[" + functionName + "(" + arguments.size() + " args)]";
    }
}