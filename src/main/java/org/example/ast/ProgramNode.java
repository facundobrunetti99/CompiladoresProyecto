package org.example.ast;

import java.util.List;

/**
 * Nodo raíz del AST - Programa con múltiples funciones
 */
public class ProgramNode extends ASTNode {
    private String returnType;
    private List<FunctionDeclarationNode> functions;
    private MainFunctionNode mainFunction;

    public ProgramNode(String returnType, 
                       List<FunctionDeclarationNode> functions,
                       MainFunctionNode mainFunction) {
        this.returnType = returnType;
        this.functions = functions;
        this.mainFunction = mainFunction;
    }

    public String getReturnType() { return returnType; }
    public List<FunctionDeclarationNode> getFunctions() { return functions; }
    public MainFunctionNode getMainFunction() { return mainFunction; }

    /**
     * Busca una función por nombre
     */
    public FunctionDeclarationNode findFunction(String name) {
        for (FunctionDeclarationNode func : functions) {
            if (func.getIdentifier().equals(name)) {
                return func;
            }
        }
        return null;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Program[" + returnType + ", " + functions.size() + " functions, main]";
    }
}