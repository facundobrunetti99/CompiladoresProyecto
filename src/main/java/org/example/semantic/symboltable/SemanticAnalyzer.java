package org.example.semantic.symboltable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.ast.*;

public class SemanticAnalyzer implements ASTVisitor {

    private SymbolTable symbolTable;
    private List<String> errors;
    private boolean hasErrors;
    private Map<String, FunctionInfo> functions;
    private String currentFunctionReturnType;

    private static class FunctionInfo {
        String returnType;
        List<String> parameterTypes;
        
        FunctionInfo(String returnType, List<String> parameterTypes) {
            this.returnType = returnType;
            this.parameterTypes = parameterTypes;
        }
    }

    public SemanticAnalyzer(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.errors = new ArrayList<>();
        this.hasErrors = false;
        this.functions = new HashMap<>();
    }

    public boolean analyze(ProgramNode program) {
        errors.clear();
        hasErrors = false;
        functions.clear();

        if (program != null) {
            program.accept(this);
        }

        if (hasErrors) {
            System.err.println("\n errores semanticos:");
            System.err.println("=".repeat(70));
            for (String error : errors) {
                System.err.println(" " + error);
            }
            System.err.println("".repeat(70));
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

        // Primera pasada: registrar todas las funciones
        for (FunctionDeclarationNode func : node.getFunctions()) {
            String funcName = func.getIdentifier();
            
            if (functions.containsKey(funcName)) {
                addError("Función '" + funcName + "' ya declarada");
            } else {
                List<String> paramTypes = new ArrayList<>();
                for (ParameterNode param : func.getParameters()) {
                    paramTypes.add(param.getType());
                }
                functions.put(funcName, new FunctionInfo(func.getReturnType(), paramTypes));
            }
        }

        // Segunda pasada: analizar cada función
        for (FunctionDeclarationNode func : node.getFunctions()) {
            func.accept(this);
        }

        // Analizar main
        if (node.getMainFunction() != null) {
            currentFunctionReturnType = node.getReturnType();
            node.getMainFunction().accept(this);
        }

        symbolTable.exitScope();
    }

    @Override
    public void visit(FunctionDeclarationNode node) {
        currentFunctionReturnType = node.getReturnType();
        symbolTable.enterScope("func_" + node.getIdentifier());

        // Declarar paramietros
        for (ParameterNode param : node.getParameters()) {
            param.accept(this);
        }

        // Declarar variables locales
        for (DeclarationNode decl : node.getLocalDeclarations()) {
            decl.accept(this);
        }

        // Analizar cuerpo
        for (StatementNode stmt : node.getBody()) {
            stmt.accept(this);
        }

        symbolTable.exitScope();
    }

    @Override
    public void visit(ParameterNode node) {
        String paramName = node.getIdentifier();
        String paramType = node.getType();

        if (symbolTable.existsLocal(paramName)) {
            addError("Parámetro '" + paramName + "' ya declarado");
        } else {
            symbolTable.declare(paramName, paramType, -1, -1);
        }
    }

    @Override
    public void visit(FunctionCallNode node) {
        String funcName = node.getFunctionName();

        if (!functions.containsKey(funcName)) {
            addError("Función '" + funcName + "' no declarada");
            return;
        }

        FunctionInfo funcInfo = functions.get(funcName);
        
        // Verificar num de argumentos
        if (node.getArgumentCount() != funcInfo.parameterTypes.size()) {
            addError("Función '" + funcName + "' espera " + funcInfo.parameterTypes.size() + 
                    " argumentos, pero recibió " + node.getArgumentCount());
        }

        // Analizar argumentos
        for (ExpressionNode arg : node.getArguments()) {
            arg.accept(this);
        }
    }

    @Override
    public void visit(MainFunctionNode node) {
        symbolTable.enterScope("main");

        for (DeclarationNode decl : node.getDeclarations()) {
            decl.accept(this);
        }

        for (StatementNode stmt : node.getStatements()) {
            stmt.accept(this);
        }

        symbolTable.exitScope();
    }

    @Override
    public void visit(VariableDeclarationNode node) {
        String varName = node.getIdentifier();
        String varType = node.getType();

        if (symbolTable.existsLocal(varName)) {
            addError("Variable '" + varName + "' ya declarada en este scope");
        } else {
            boolean success = symbolTable.declare(varName, varType, -1, -1);
            if (!success) {
                addError("Error al declarar variable '" + varName + "'");
            }
        }
    }

    @Override
    public void visit(AssignmentNode node) {
        String varName = node.getIdentifier();

        if (!symbolTable.exists(varName)) {
            addError("Variable '" + varName + "' no declarada");
        }

        if (node.getExpression() != null) {
            node.getExpression().accept(this);
        }
    }

    @Override
    public void visit(IfNode node) {
        if (node.getCondition() != null) {
            node.getCondition().accept(this);
        }

        symbolTable.enterScope("if_then");
        for (StatementNode stmt : node.getThenBlock()) {
            stmt.accept(this);
        }
        symbolTable.exitScope();

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
        if (node.getCondition() != null) {
            node.getCondition().accept(this);
        }

        symbolTable.enterScope("while_body");
        for (StatementNode stmt : node.getBody()) {
            stmt.accept(this);
        }
        symbolTable.exitScope();
    }

    @Override
    public void visit(ComparisonNode node) {
        if (node.getLeft() != null) {
            node.getLeft().accept(this);
        }
        if (node.getRight() != null) {
            node.getRight().accept(this);
        }
    }

    @Override
    public void visit(LogicalOpNode node) {
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
        // Los num siempre son válidos
    }

    @Override
    public void visit(BooleanNode node) {
        // Los booleanos siempre son válidos
    }

    @Override
    public void visit(VariableNode node) {
        String varName = node.getIdentifier();

        if (!symbolTable.exists(varName)) {
            addError("Variable '" + varName + "' no declarada");
        }
    }

    @Override
    public void visit(ExpressionStatementNode node) {
        if (node.getExpression() != null) {
            node.getExpression().accept(this);
        }
    }
}