package org.example;

import org.example.ast.*;
import java.util.*;


public class ASTtreePrinter implements ASTVisitor {
    
    private StringBuilder output;
    private int indentLevel;
    private Map<String, Integer> variables;  // Tabla de símbolos con valores
    
    public ASTtreePrinter() {
        this.output = new StringBuilder();
        this.indentLevel = 0;
        this.variables = new HashMap<>();
    }
    
    public String printTree(ProgramNode program) {
        output.append("═══════════════════════════════════════════════\n");
        output.append("  ABSTRACT SYNTAX TREE (AST) WITH VALUES\n");
        output.append("═══════════════════════════════════════════════\n\n");
        
        program.accept(this);
        
        output.append("\n═══════════════════════════════════════════════\n");
        output.append("  SYMBOL TABLE (Final Values)\n");
        output.append("═══════════════════════════════════════════════\n");
        for (Map.Entry<String, Integer> entry : variables.entrySet()) {
            output.append("  ").append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
        }
        output.append("═══════════════════════════════════════════════\n");
        
        return output.toString();
    }
    
    private void printIndent() {
        for (int i = 0; i < indentLevel; i++) {
            if (i == indentLevel - 1) {
                output.append("├── ");
            } else {
                output.append("│   ");
            }
        }
    }
    
    private void printLastIndent() {
        for (int i = 0; i < indentLevel; i++) {
            if (i == indentLevel - 1) {
                output.append("└── ");
            } else {
                output.append("│   ");
            }
        }
    }
    
    @Override
    public void visit(ProgramNode node) {
        output.append("PROGRAM\n");
        output.append("│   Return Type: ").append(node.getReturnType()).append("\n");
        indentLevel++;
        node.getMainFunction().accept(this);
        indentLevel--;
    }
    
    @Override
    public void visit(MainFunctionNode node) {
        printIndent();
        output.append("MAIN FUNCTION\n");
        indentLevel++;
        
        // Declaraciones
        if (!node.getDeclarations().isEmpty()) {
            printIndent();
            output.append("DECLARATIONS\n");
            indentLevel++;
            for (DeclarationNode decl : node.getDeclarations()) {
                decl.accept(this);
            }
            indentLevel--;
        }
        
        // Statements
        if (!node.getStatements().isEmpty()) {
            printIndent();
            output.append("STATEMENTS\n");
            indentLevel++;
            for (int i = 0; i < node.getStatements().size(); i++) {
                StatementNode stmt = node.getStatements().get(i);
                stmt.accept(this);
            }
            indentLevel--;
        }
        
        indentLevel--;
    }
    
    @Override
    public void visit(VariableDeclarationNode node) {
        printIndent();
        output.append("VAR DECLARATION: ")
              .append(node.getType())
              .append(" ")
              .append(node.getIdentifier())
              .append("\n");
        
        // Inicializar variable en 0
        variables.put(node.getIdentifier(), 0);
    }
    
    @Override
    public void visit(AssignmentNode node) {
        printIndent();
        output.append("ASSIGNMENT: ").append(node.getIdentifier()).append(" = ");
        
        // Evaluar la expresión
        int value = evaluateExpression(node.getExpression());
        variables.put(node.getIdentifier(), value);
        
        output.append(value).append("\n");
        
        indentLevel++;
        printIndent();
        output.append("Expression:\n");
        indentLevel++;
        node.getExpression().accept(this);
        indentLevel--;
        indentLevel--;
    }
    
    @Override
    public void visit(ReturnNode node) {
        printIndent();
        output.append("RETURN");
        
        if (node.hasExpression()) {
            int value = evaluateExpression(node.getExpression());
            output.append(": ").append(value).append("\n");
            
            indentLevel++;
            printIndent();
            output.append("Expression:\n");
            indentLevel++;
            node.getExpression().accept(this);
            indentLevel--;
            indentLevel--;
        } else {
            output.append(" (void)\n");
        }
    }
    
    @Override
    public void visit(BinaryOpNode node) {
        int leftValue = evaluateExpression(node.getLeft());
        int rightValue = evaluateExpression(node.getRight());
        int result = 0;
        
        switch (node.getOperator()) {
            case "+": result = leftValue + rightValue; break;
            case "-": result = leftValue - rightValue; break;
            case "*": result = leftValue * rightValue; break;
            case "/": result = rightValue != 0 ? leftValue / rightValue : 0; break;
        }
        
        printIndent();
        output.append("BINARY OP: ")
              .append(leftValue)
              .append(" ")
              .append(node.getOperator())
              .append(" ")
              .append(rightValue)
              .append(" = ")
              .append(result)
              .append("\n");
        
        indentLevel++;
        
        printIndent();
        output.append("Left:\n");
        indentLevel++;
        node.getLeft().accept(this);
        indentLevel--;
        
        printIndent();
        output.append("Right:\n");
        indentLevel++;
        node.getRight().accept(this);
        indentLevel--;
        
        indentLevel--;
    }
    
    @Override
    public void visit(NumberNode node) {
        printIndent();
        output.append("NUMBER: ").append(node.getValue()).append("\n");
    }
    
    @Override
    public void visit(BooleanNode node) {
        printIndent();
        output.append("BOOLEAN: ").append(node.getValue()).append("\n");
    }
    
    @Override
    public void visit(VariableNode node) {
        Integer value = variables.get(node.getIdentifier());
        printIndent();
        output.append("VARIABLE: ")
              .append(node.getIdentifier())
              .append(" = ")
              .append(value != null ? value : "undefined")
              .append("\n");
    }
    
    /**
     * Evalúa una expresión y devuelve su valor
     */
    private int evaluateExpression(ExpressionNode expr) {
        if (expr instanceof NumberNode) {
            return ((NumberNode) expr).getValue();
        } 
        else if (expr instanceof BooleanNode) {
            return ((BooleanNode) expr).getValue() ? 1 : 0;
        }
        else if (expr instanceof VariableNode) {
            String varName = ((VariableNode) expr).getIdentifier();
            Integer value = variables.get(varName);
            return value != null ? value : 0;
        }
        else if (expr instanceof BinaryOpNode) {
            BinaryOpNode binOp = (BinaryOpNode) expr;
            int left = evaluateExpression(binOp.getLeft());
            int right = evaluateExpression(binOp.getRight());
            
            switch (binOp.getOperator()) {
                case "+": return left + right;
                case "-": return left - right;
                case "*": return left * right;
                case "/": return right != 0 ? left / right : 0;
                default: return 0;
            }
        }
        
        return 0;
    }
}