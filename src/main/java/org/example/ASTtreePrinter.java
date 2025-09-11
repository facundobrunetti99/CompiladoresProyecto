package org.example;

import org.example.ast.*;
import java.util.*;

/**
 * Clase para imprimir el AST como un árbol visual en consola
 */
public class ASTtreePrinter implements ASTVisitor {
    
    private StringBuilder output;
    private String currentIndent;
    private boolean[] isLastChild;
    private int depth;
    
    // Constantes para los caracteres del árbol
    private static final String TREE_BRANCH = "├── ";
    private static final String TREE_LAST_BRANCH = "└── ";
    private static final String TREE_VERTICAL = "│   ";
    private static final String TREE_SPACE = "    ";
    
    public ASTtreePrinter() {
        this.output = new StringBuilder();
        this.currentIndent = "";
        this.isLastChild = new boolean[50]; // Máximo 50 niveles de profundidad
        this.depth = 0;
    }
    
    /**
     * Método principal para imprimir el árbol
     */
    public String printTree(ASTNode root) {
        output.setLength(0); // Limpiar output previo
        depth = 0;
        currentIndent = "";
        
        output.append("ABSTRACT SYNTAX TREE (AST)\n");
        output.append("═════════════════════════════════════\n");
        
        if (root != null) {
            root.accept(this);
        } else {
            output.append("AST is null\n");
        }
        
        output.append("═════════════════════════════════════\n");
        return output.toString();
    }
    
    /**
     * Método auxiliar para manejar la indentación
     */
    private void printWithIndent(String nodeType, String details, boolean isLast) {
        // Construir la indentación
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < depth - 1; i++) {
            if (isLastChild[i]) {
                indent.append(TREE_SPACE);
            } else {
                indent.append(TREE_VERTICAL);
            }
        }
        
        // Agregar el branch apropiado
        if (depth > 0) {
            if (isLast) {
                indent.append(TREE_LAST_BRANCH);
            } else {
                indent.append(TREE_BRANCH);
            }
        }
        
        // Imprimir el nodo
        output.append(indent).append("📄 ").append(nodeType);
        if (details != null && !details.isEmpty()) {
            output.append(" ➤ ").append(details);
        }
        output.append("\n");
        
        // Actualizar estado para hijos
        if (depth < isLastChild.length) {
            isLastChild[depth] = isLast;
        }
    }
    
    @Override
    public void visit(ProgramNode node) {
        printWithIndent("PROGRAM", "Return Type: " + node.getReturnType(), true);
        
        depth++;
        if (node.getMainFunction() != null) {
            node.getMainFunction().accept(this);
        }
        depth--;
    }
    
    @Override
    public void visit(MainFunctionNode node) {
        printWithIndent("MAIN_FUNCTION", null, true);
        
        depth++;
        
        // Imprimir declaraciones
        List<DeclarationNode> declarations = node.getDeclarations();
        List<StatementNode> statements = node.getStatements();
        
        int totalChildren = declarations.size() + statements.size();
        int currentChild = 0;
        
        // Procesar declaraciones
        for (DeclarationNode decl : declarations) {
            currentChild++;
            boolean isLast = (currentChild == totalChildren);
            isLastChild[depth] = isLast;
            decl.accept(this);
        }
        
        // Procesar sentencias
        for (StatementNode stmt : statements) {
            currentChild++;
            boolean isLast = (currentChild == totalChildren);
            isLastChild[depth] = isLast;
            stmt.accept(this);
        }
        
        depth--;
    }
    
    @Override
    public void visit(VariableDeclarationNode node) {
        String details = "Type: " + node.getType() + ", Identifier: " + node.getIdentifier();
        printWithIndent("VARIABLE_DECLARATION", details, isLastChild[depth]);
    }
    
    @Override
    public void visit(AssignmentNode node) {
        String details = "Variable: " + node.getIdentifier();
        printWithIndent("ASSIGNMENT", details, isLastChild[depth]);
        
        depth++;
        if (node.getExpression() != null) {
            isLastChild[depth] = true;
            node.getExpression().accept(this);
        }
        depth--;
    }
    
    @Override
    public void visit(ReturnNode node) {
        String details = node.hasExpression() ? "with expression" : "void";
        printWithIndent("RETURN", details, isLastChild[depth]);
        
        if (node.hasExpression()) {
            depth++;
            isLastChild[depth] = true;
            node.getExpression().accept(this);
            depth--;
        }
    }
    
    @Override
    public void visit(BinaryOpNode node) {
        String details = "Operator: " + node.getOperator();
        printWithIndent("BINARY_OPERATION", details, isLastChild[depth]);
        
        depth++;
        
        // Operando izquierdo
        isLastChild[depth] = false;
        printWithIndent("LEFT_OPERAND", null, false);
        depth++;
        isLastChild[depth] = true;
        node.getLeft().accept(this);
        depth--;
        
        // Operando derecho
        isLastChild[depth] = true;
        printWithIndent("RIGHT_OPERAND", null, true);
        depth++;
        isLastChild[depth] = true;
        node.getRight().accept(this);
        depth--;
        
        depth--;
    }
    
    @Override
    public void visit(NumberNode node) {
        String details = "Value: " + node.getValue();
        printWithIndent("NUMBER", details, isLastChild[depth]);
    }
    
    @Override
    public void visit(BooleanNode node) {
        String details = "Value: " + node.getValue();
        printWithIndent("BOOLEAN", details, isLastChild[depth]);
    }
    
    @Override
    public void visit(VariableNode node) {
        String details = "Identifier: " + node.getIdentifier();
        printWithIndent("VARIABLE", details, isLastChild[depth]);
    }
}