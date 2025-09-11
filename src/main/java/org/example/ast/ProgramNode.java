package org.example.ast;

/**
 * Nodo para el programa principal
 */
public class ProgramNode extends ASTNode {
    private String returnType;
    private MainFunctionNode mainFunction;

    public ProgramNode(String returnType, MainFunctionNode mainFunction) {
        this.returnType = returnType;
        this.mainFunction = mainFunction;
    }

    public String getReturnType() { return returnType; }
    public MainFunctionNode getMainFunction() { return mainFunction; }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Program[" + returnType + ", " + mainFunction + "]";
    }
}