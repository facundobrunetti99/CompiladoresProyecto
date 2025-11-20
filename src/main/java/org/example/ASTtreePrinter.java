package org.example;

import java.util.List;

import org.example.ast.ASTNode;
import org.example.ast.ASTVisitor;
import org.example.ast.AssignmentNode;
import org.example.ast.BinaryOpNode;
import org.example.ast.BooleanNode;
import org.example.ast.ComparisonNode;
import org.example.ast.DeclarationNode;
import org.example.ast.IfNode;
import org.example.ast.LogicalOpNode;
import org.example.ast.MainFunctionNode;
import org.example.ast.NumberNode;
import org.example.ast.ProgramNode;
import org.example.ast.ReturnNode;
import org.example.ast.StatementNode;
import org.example.ast.VariableDeclarationNode;
import org.example.ast.VariableNode;
import org.example.ast.WhileNode;

/**
 * Clase para imprimir el AST como un árbol visual en consola
 * Soporta: variables, asignaciones, operaciones, if, else, while, comparaciones, operadores lógicos
 */
public class ASTtreePrinter implements ASTVisitor {

    private StringBuilder output;
    private boolean[] isLastChild;
    private int depth;

    // Constantes para los caracteres del árbol
    private static final String TREE_BRANCH = "├── ";
    private static final String TREE_LAST_BRANCH = "└── ";
    private static final String TREE_VERTICAL = "│   ";
    private static final String TREE_SPACE = "    ";

    public ASTtreePrinter() {
        this.output = new StringBuilder();
        this.isLastChild = new boolean[50]; // Máximo 50 niveles de profundidad
        this.depth = 0;
    }

    /**
     * Método principal para imprimir el árbol
     */
    public String printTree(ASTNode root) {
        output.setLength(0);
        depth = 0;

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
        StringBuilder indent = new StringBuilder();

        for (int i = 0; i < depth - 1; i++) {
            if (isLastChild[i]) {
                indent.append(TREE_SPACE);
            } else {
                indent.append(TREE_VERTICAL);
            }
        }

        if (depth > 0) {
            if (isLast) {
                indent.append(TREE_LAST_BRANCH);
            } else {
                indent.append(TREE_BRANCH);
            }
        }

        output.append(indent).append("📄 ").append(nodeType);
        if (details != null && !details.isEmpty()) {
            output.append(" ➤ ").append(details);
        }
        output.append("\n");

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

        List<DeclarationNode> declarations = node.getDeclarations();
        List<StatementNode> statements = node.getStatements();

        int totalChildren = declarations.size() + statements.size();
        int currentChild = 0;

        for (DeclarationNode decl : declarations) {
            currentChild++;
            boolean isLast = (currentChild == totalChildren);
            isLastChild[depth] = isLast;
            decl.accept(this);
        }

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
    public void visit(IfNode node) {
        printWithIndent("IF_STATEMENT", null, isLastChild[depth]);

        depth++;

        // Condición
        printWithIndent("CONDITION", null, false);
        depth++;
        isLastChild[depth] = true;
        node.getCondition().accept(this);
        depth--;

        // Bloque THEN
        printWithIndent("THEN_BLOCK", null, !node.hasElse());
        depth++;
        List<StatementNode> thenBlock = node.getThenBlock();
        for (int i = 0; i < thenBlock.size(); i++) {
            isLastChild[depth] = (i == thenBlock.size() - 1);
            thenBlock.get(i).accept(this);
        }
        depth--;

        // Bloque ELSE (si existe)
        if (node.hasElse()) {
            printWithIndent("ELSE_BLOCK", null, true);
            depth++;
            List<StatementNode> elseBlock = node.getElseBlock();
            for (int i = 0; i < elseBlock.size(); i++) {
                isLastChild[depth] = (i == elseBlock.size() - 1);
                elseBlock.get(i).accept(this);
            }
            depth--;
        }

        depth--;
    }

    @Override
    public void visit(WhileNode node) {
        printWithIndent("WHILE_LOOP", null, isLastChild[depth]);

        depth++;

        // Condición
        printWithIndent("CONDITION", null, false);
        depth++;
        isLastChild[depth] = true;
        node.getCondition().accept(this);
        depth--;

        // Cuerpo del loop
        printWithIndent("BODY", null, true);
        depth++;
        List<StatementNode> body = node.getBody();
        for (int i = 0; i < body.size(); i++) {
            isLastChild[depth] = (i == body.size() - 1);
            body.get(i).accept(this);
        }
        depth--;

        depth--;
    }

    @Override
    public void visit(LogicalOpNode node) {
        String details = "Operator: " + node.getOperator();
        printWithIndent("LOGICAL_OPERATION", details, isLastChild[depth]);

        depth++;

        // Operando izquierdo
        printWithIndent("LEFT_OPERAND", null, false);
        depth++;
        isLastChild[depth] = true;
        node.getLeft().accept(this);
        depth--;

        // Operando derecho
        printWithIndent("RIGHT_OPERAND", null, true);
        depth++;
        isLastChild[depth] = true;
        node.getRight().accept(this);
        depth--;

        depth--;
    }

    @Override
    public void visit(ComparisonNode node) {
        String details = "Operator: " + node.getOperator();
        printWithIndent("COMPARISON", details, isLastChild[depth]);

        depth++;

        // Operando izquierdo
        printWithIndent("LEFT_OPERAND", null, false);
        depth++;
        isLastChild[depth] = true;
        node.getLeft().accept(this);
        depth--;

        // Operando derecho
        printWithIndent("RIGHT_OPERAND", null, true);
        depth++;
        isLastChild[depth] = true;
        node.getRight().accept(this);
        depth--;

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
        printWithIndent("LEFT_OPERAND", null, false);
        depth++;
        isLastChild[depth] = true;
        node.getLeft().accept(this);
        depth--;

        // Operando derecho
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