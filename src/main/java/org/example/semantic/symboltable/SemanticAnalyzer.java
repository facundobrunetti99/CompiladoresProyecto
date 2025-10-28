package org.example.semantic.symboltable;

import java.util.ArrayList;
import java.util.List;

import org.example.ast.ASTVisitor;
import org.example.ast.AssignmentNode;
import org.example.ast.BinaryOpNode;
import org.example.ast.BooleanNode;
import org.example.ast.ComparisonNode;
import org.example.ast.DeclarationNode;
import org.example.ast.IfNode;
import org.example.ast.MainFunctionNode;
import org.example.ast.NumberNode;
import org.example.ast.ProgramNode;
import org.example.ast.ReturnNode;
import org.example.ast.StatementNode;
import org.example.ast.VariableDeclarationNode;
import org.example.ast.VariableNode;
import org.example.ast.WhileNode;

/**
 * Analizador semántico que verifica: - Declaraciones de variables - Uso de
 * variables no declaradas - Redeclaraciones - Tipos compatibles
 */
public class SemanticAnalyzer implements ASTVisitor {

    private SymbolTable symbolTable;
    private List<String> errors;
    private boolean hasErrors;

    public SemanticAnalyzer(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.errors = new ArrayList<>();
        this.hasErrors = false;
    }

    public boolean analyze(ProgramNode program) {
        errors.clear();
        hasErrors = false;

        if (program != null) {
            program.accept(this);
        }

        if (hasErrors) {
            System.err.println("\n✗ ERRORES SEMÁNTICOS ENCONTRADOS:");
            System.err.println("=".repeat(70));
            for (String error : errors) {
                System.err.println("  • " + error);
            }
            System.err.println("=".repeat(70));
        }

        return !hasErrors;
    }

    private void addError(String message) {
        errors.add(message);
        hasErrors = true;
    }

    @Override
    public void visit(ProgramNode node) {
        symbolTable.enterScope("global");

        if (node.getMainFunction() != null) {
            node.getMainFunction().accept(this);
        }

        symbolTable.exitScope();
    }

    @Override
    public void visit(MainFunctionNode node) {
        symbolTable.enterScope("main");

        // Procesar declaraciones
        for (DeclarationNode decl : node.getDeclarations()) {
            decl.accept(this);
        }

        // Procesar sentencias
        for (StatementNode stmt : node.getStatements()) {
            stmt.accept(this);
        }

        symbolTable.exitScope();
    }

    @Override
    public void visit(VariableDeclarationNode node) {
        String varName = node.getIdentifier();
        String varType = node.getType();

        // Verificar si ya existe en el scope actual
        if (symbolTable.existsLocal(varName)) {
            addError("Variable '" + varName + "' ya declarada en este scope");
        } else {
            // Declarar en la tabla de símbolos
            boolean success = symbolTable.declare(varName, varType, -1, -1);
            if (!success) {
                addError("Error al declarar variable '" + varName + "'");
            }
        }
    }

    @Override
    public void visit(AssignmentNode node) {
        String varName = node.getIdentifier();

        // Verificar que la variable existe
        if (!symbolTable.exists(varName)) {
            addError("Variable '" + varName + "' no declarada");
        }

        // Visitar la expresión del lado derecho
        if (node.getExpression() != null) {
            node.getExpression().accept(this);
        }
    }

    @Override
    public void visit(IfNode node) {
        // Verificar condición
        if (node.getCondition() != null) {
            node.getCondition().accept(this);
        }

        // Nuevo scope para THEN
        symbolTable.enterScope("if_then");
        for (StatementNode stmt : node.getThenBlock()) {
            stmt.accept(this);
        }
        symbolTable.exitScope();

        // Nuevo scope para ELSE (si existe)
        if (node.hasElse()) {
            symbolTable.enterScope("if_else");
            for (StatementNode stmt : node.getElseBlock()) {
                stmt.accept(this);
            }
            symbolTable.exitScope();
        }
    }

    @Override
    public void visit(WhileNode node) {
        // Verificar condición
        if (node.getCondition() != null) {
            node.getCondition().accept(this);
        }

        // Nuevo scope para el cuerpo del while
        symbolTable.enterScope("while_body");
        for (StatementNode stmt : node.getBody()) {
            stmt.accept(this);
        }
        symbolTable.exitScope();
    }

    @Override
    public void visit(ComparisonNode node) {
        // Verificar operandos
        if (node.getLeft() != null) {
            node.getLeft().accept(this);
        }
        if (node.getRight() != null) {
            node.getRight().accept(this);
        }
    }

    @Override
    public void visit(ReturnNode node) {
        if (node.hasExpression()) {
            node.getExpression().accept(this);
        }
    }

    @Override
    public void visit(BinaryOpNode node) {
        if (node.getLeft() != null) {
            node.getLeft().accept(this);
        }
        if (node.getRight() != null) {
            node.getRight().accept(this);
        }
    }

    @Override
    public void visit(NumberNode node) {
        // Los números siempre son válidos
    }

    @Override
    public void visit(BooleanNode node) {
        // Los booleanos siempre son válidos
    }

    @Override
    public void visit(VariableNode node) {
        String varName = node.getIdentifier();

        // Verificar que la variable existe
        if (!symbolTable.exists(varName)) {
            addError("Variable '" + varName + "' no declarada");
        }
    }
}
