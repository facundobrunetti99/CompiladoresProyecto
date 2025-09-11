package org.example.interpetrer; // ¡Corregido el nombre del paquete!
import org.example.ast.*;
import java.util.*;

public class Interpreter implements ASTVisitor {

    // Tabla de símbolos para almacenar valores de variables
    private Map<String, Object> variables;
    private Object lastExpressionValue;
    private Object returnValue;
    private boolean hasReturned;

    public Interpreter() {
        this.variables = new HashMap<>();
        this.hasReturned = false;
    }

    // Método genérico que puede recibir cualquier ASTNode
    public Object interpret(ASTNode node) {
        System.out.println("\n=== INICIANDO INTERPRETACIÓN ===");
        
        if (node instanceof ProgramNode) {
            return interpret((ProgramNode) node);
        } else {
            // Para cualquier otro tipo de nodo
            node.accept(this);
            System.out.println("=== INTERPRETACIÓN FINALIZADA ===\n");
            return lastExpressionValue;
        }
    }

    // Método específico para ProgramNode
    public Object interpret(ProgramNode program) {
        System.out.println("\n=== INICIANDO INTERPRETACIÓN DE PROGRAMA ===");
        program.accept(this);
        System.out.println("=== INTERPRETACIÓN FINALIZADA ===\n");
        return returnValue;
    }

    @Override
    public void visit(ProgramNode node) {
        System.out.println("Ejecutando programa con tipo de retorno: " + node.getReturnType());
        node.getMainFunction().accept(this);

        // Verificar que el tipo de retorno sea correcto
        if (node.getReturnType().equals("void") && returnValue != null) {
            throw new RuntimeException("Error: función void no puede retornar un valor");
        } else if (!node.getReturnType().equals("void") && returnValue == null) {
            throw new RuntimeException("Error: función " + node.getReturnType() + " debe retornar un valor");
        }

        if (returnValue != null) {
            System.out.println("Programa terminó con valor de retorno: " + returnValue);
        } else {
            System.out.println("Programa terminó sin valor de retorno");
        }
    }

    @Override
    public void visit(MainFunctionNode node) {
        System.out.println("Ejecutando función main");

        // Procesar declaraciones
        for (DeclarationNode decl : node.getDeclarations()) {
            decl.accept(this);
        }

        // Ejecutar sentencias
        for (StatementNode stmt : node.getStatements()) {
            if (hasReturned) break; // Si ya se ejecutó return, parar
            stmt.accept(this);
        }
    }

    @Override
    public void visit(VariableDeclarationNode node) {
        System.out.println("Declarando variable: " + node.getIdentifier() + " de tipo " + node.getType());

        // Inicializar con valor por defecto
        if (node.getType().equals("int")) {
            variables.put(node.getIdentifier(), 0);
        } else if (node.getType().equals("bool")) {
            variables.put(node.getIdentifier(), false);
        }
    }

    @Override
    public void visit(AssignmentNode node) {
        System.out.println("Ejecutando asignación a: " + node.getIdentifier());

        // Evaluar la expresión del lado derecho
        node.getExpression().accept(this);
        Object value = lastExpressionValue;

        // Asignar el valor a la variable
        variables.put(node.getIdentifier(), value);
        System.out.println("Variable " + node.getIdentifier() + " = " + value);
    }

    @Override
    public void visit(ReturnNode node) {
        if (node.hasExpression()) {
            System.out.println("Ejecutando return con expresión");
            node.getExpression().accept(this);
            returnValue = lastExpressionValue;
        } else {
            System.out.println("Ejecutando return sin expresión");
            returnValue = null;
        }
        hasReturned = true;
    }

    @Override
    public void visit(BinaryOpNode node) {
        // Evaluar operandos
        node.getLeft().accept(this);
        Object leftValue = lastExpressionValue;

        node.getRight().accept(this);
        Object rightValue = lastExpressionValue;

        // Realizar operación
        Object result = performBinaryOperation(leftValue, node.getOperator(), rightValue);
        lastExpressionValue = result;

        System.out.println("Operación: " + leftValue + " " + node.getOperator() + " " + rightValue + " = " + result);
    }

    private Object performBinaryOperation(Object left, String operator, Object right) {
        // Verificar que ambos operandos sean números para operaciones aritméticas
        if (operator.equals("+") || operator.equals("-") || operator.equals("*") || operator.equals("/")) {
            if (!(left instanceof Integer) || !(right instanceof Integer)) {
                throw new RuntimeException("Error: operación aritmética requiere operandos enteros");
            }

            int l = (Integer) left;
            int r = (Integer) right;

            switch (operator) {
                case "+": return l + r;
                case "-": return l - r;
                case "*": return l * r;
                case "/":
                    if (r == 0) {
                        throw new RuntimeException("Error: división por cero");
                    }
                    return l / r;
                default:
                    throw new RuntimeException("Operador desconocido: " + operator);
            }
        }

        throw new RuntimeException("Operador no implementado: " + operator);
    }

    @Override
    public void visit(NumberNode node) {
        lastExpressionValue = node.getValue();
        System.out.println("Número: " + node.getValue());
    }

    @Override
    public void visit(BooleanNode node) {
        lastExpressionValue = node.getValue();
        System.out.println("Booleano: " + node.getValue());
    }

    @Override
    public void visit(VariableNode node) {
        String varName = node.getIdentifier();
        if (!variables.containsKey(varName)) {
            throw new RuntimeException("Error: variable '" + varName + "' no está declarada");
        }

        lastExpressionValue = variables.get(varName);
        System.out.println("Variable " + varName + " = " + lastExpressionValue);
    }

    // Método para obtener el estado de las variables (útil para debugging)
    public Map<String, Object> getVariables() {
        return new HashMap<>(variables);
    }
}