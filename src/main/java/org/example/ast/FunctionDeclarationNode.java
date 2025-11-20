package org.example.ast;

import java.util.List;

public class FunctionDeclarationNode extends ASTNode {
    private String returnType;
    private String identifier;
    private List<ParameterNode> parameters;
    private List<DeclarationNode> localDeclarations;
    private List<StatementNode> body;

    public FunctionDeclarationNode(String returnType, String identifier, 
                                   List<ParameterNode> parameters,
                                   List<DeclarationNode> localDeclarations,
                                   List<StatementNode> body) {
        this.returnType = returnType;
        this.identifier = identifier;
        this.parameters = parameters;
        this.localDeclarations = localDeclarations;
        this.body = body;
    }

    public String getReturnType() { return returnType; }
    public String getIdentifier() { return identifier; }
    public List<ParameterNode> getParameters() { return parameters; }
    public List<DeclarationNode> getLocalDeclarations() { return localDeclarations; }
    public List<StatementNode> getBody() { return body; }
    public int getParameterCount() { return parameters.size(); }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "FunctionDecl[" + returnType + " " + identifier + 
               "(" + parameters.size() + " params)]";
    }
}