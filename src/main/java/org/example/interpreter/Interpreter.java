package org.example.interpreter;

import org.example.ast.*;
import java.util.*;

/**
 * Intérprete que ejecuta el AST directamente
 * Evalúa expresiones y mantiene el estado de las variables
 */
public class Interpreter implements ASTVisitor {
    
    private Map<String, Object> variables;  // Tabla de símbolos con valores
    private Object returnValue;              // Valor de retorno del programa
    private boolean hasReturned;             // Flag para detectar return
    
    public Interpreter() {
        this.variables = new HashMap<>();
        this.returnValue = null;
        this.hasReturned = false;
    }
    
    /**
     * Ejecuta el programa completo y devuelve el resultado
     */
    public Object interpret(ProgramNode program) {
        System.out.println("\n=== INICIANDO INTERPRETACIÓN DE PROGRAMA ===");
        
        program.accept(this);
        
        System.out.println("\n=== INTERPRETACIÓN FINALIZADA ===");
        System.out.println("Programa terminó con valor de retorno: " + returnValue);
        
        return returnValue;
    }
    
    @Override
    public void visit(ProgramNode node) {
        System.out.println("Ejecutando programa con tipo de retorno: " + node.getReturnType());
        node.getMainFunction().accept(this);
    }
    
    @Override
    public void visit(MainFunctionNode node) {
        System.out.println("Ejecutando función main");
        
        // Procesar declaraciones
        for (DeclarationNode decl : node.getDeclarations()) {
            decl.accept(this);
        }
        
        // Ejecutar statements hasta encontrar return
        for (StatementNode stmt : node.getStatements()) {
            if (hasReturned) break;  // Ya se ejecutó un return
            stmt.accept(this);
        }
    }
    
    @Override
    public void visit(VariableDeclarationNode node) {
        // Inicializar variable con valor por defecto
        Object defaultValue;
        if (node.getType().equals("int")) {
            defaultValue = 0;
        } else if (node.getType().equals("bool")) {
            defaultValue = false;
        } else {
            defaultValue = null;
        }
        
        variables.put(node.getIdentifier(), defaultValue);
        System.out.println("Declarando variable: " + node.getIdentifier() + " de tipo " + node.getType());
    }
    
    @Override
    public void visit(AssignmentNode node) {
        System.out.println("Ejecutando asignación a: " + node.getIdentifier());
        
        // Evaluar la expresión
        Object value = evaluateExpression(node.getExpression());
        
        // Asignar el valor a la variable
        variables.put(node.getIdentifier(), value);
        System.out.println("Variable " + node.getIdentifier() + " = " + value);
    }
    
    @Override
    public void visit(ReturnNode node) {
        System.out.println("Ejecutando return" + (node.hasExpression() ? " con expresión" : " sin expresión"));
        
        if (node.hasExpression()) {
            returnValue = evaluateExpression(node.getExpression());
            System.out.println("Valor de retorno: " + returnValue);
        } else {
            returnValue = null;
        }
        
        hasReturned = true;
    }
    
    @Override
    public void visit(BinaryOpNode node) {
        // Este método no se usa directamente, la evaluación se hace en evaluateExpression
    }
    
    @Override
    public void visit(NumberNode node) {
        // Este método no se usa directamente, la evaluación se hace en evaluateExpression
    }
    
    @Override
    public void visit(BooleanNode node) {
        // Este método no se usa directamente, la evaluación se hace en evaluateExpression
    }
    
    @Override
    public void visit(VariableNode node) {
        // Este método no se usa directamente, la evaluación se hace en evaluateExpression
    }
    
    /**
     * Evalúa una expresión y devuelve su valor
     */
    private Object evaluateExpression(ExpressionNode expr) {
        if (expr instanceof NumberNode) {
            int value = ((NumberNode) expr).getValue();
            System.out.println("Número: " + value);
            return value;
        } 
        else if (expr instanceof BooleanNode) {
            boolean value = ((BooleanNode) expr).getValue();
            System.out.println("Booleano: " + value);
            return value;
        }
        else if (expr instanceof VariableNode) {
            String varName = ((VariableNode) expr).getIdentifier();
            Object value = variables.get(varName);
            
            if (value == null) {
                throw new RuntimeException("Variable no definida: " + varName);
            }
            
            System.out.println("Variable " + varName + " = " + value);
            return value;
        }
        else if (expr instanceof BinaryOpNode) {
            BinaryOpNode binOp = (BinaryOpNode) expr;
            
            // Evaluar operandos
            Object leftObj = evaluateExpression(binOp.getLeft());
            Object rightObj = evaluateExpression(binOp.getRight());
            
            // Convertir a enteros para operaciones
            if (!(leftObj instanceof Integer) || !(rightObj instanceof Integer)) {
                throw new RuntimeException("Operación requiere valores enteros");
            }
            
            int left = (Integer) leftObj;
            int right = (Integer) rightObj;
            int result;
            
            // Ejecutar operación
            switch (binOp.getOperator()) {
                case "+":
                    result = left + right;
                    System.out.println("Operación: " + left + " + " + right + " = " + result);
                    return result;
                    
                case "-":
                    result = left - right;
                    System.out.println("Operación: " + left + " - " + right + " = " + result);
                    return result;
                    
                case "*":
                    result = left * right;
                    System.out.println("Operación: " + left + " * " + right + " = " + result);
                    return result;
                    
                case "/":
                    if (right == 0) {
                        throw new RuntimeException("Error: División por cero");
                    }
                    result = left / right;
                    System.out.println("Operación: " + left + " / " + right + " = " + result);
                    return result;
                    
                default:
                    throw new RuntimeException("Operador desconocido: " + binOp.getOperator());
            }
        }
        
        throw new RuntimeException("Tipo de expresión no soportado: " + expr.getClass().getName());
    }
    
    /**
     * Obtiene el valor actual de una variable (para debugging)
     */
    public Object getVariable(String name) {
        return variables.get(name);
    }
    
    /**
     * Obtiene todas las variables (para debugging)
     */
    public Map<String, Object> getAllVariables() {
        return new HashMap<>(variables);
    }
}