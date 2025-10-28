package org.example.codegen;

import java.util.HashMap;
import java.util.Map;

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
import org.example.semantic.symboltable.SymbolTable;

public class X86AssemblyGenerator implements ASTVisitor {

    private StringBuilder code;
    private StringBuilder simulation;
    private SymbolTable symbolTable;
    private Map<String, Integer> localVariables;
    private Map<String, Long> variableValues;
    private Map<String, Long> registers;
    private int stackOffset;
    private int labelCounter;
    private String returnType;
    private int stepCounter;
    private boolean executeSimulation;
    private long lastConditionResult;

    public X86AssemblyGenerator(SymbolTable symbolTable) {
        this.code = new StringBuilder();
        this.simulation = new StringBuilder();
        this.symbolTable = symbolTable;
        this.localVariables = new HashMap<>();
        this.variableValues = new HashMap<>();
        this.registers = new HashMap<>();
        this.stackOffset = 0;
        this.labelCounter = 0;
        this.stepCounter = 0;
        this.executeSimulation = true;
        this.lastConditionResult = 0;
        initializeRegisters();
    }

    private void initializeRegisters() {
        registers.put("rax", 0L);
        registers.put("rbx", 0L);
        registers.put("rcx", 0L);
        registers.put("rdx", 0L);
    }

    public String generateCode(ProgramNode program) {
        program.accept(this);
        StringBuilder output = new StringBuilder();
        output.append("# x86-64 Assembly Code con estructuras de control\n");
        output.append(".section .text\n");
        output.append(".global main\n\n");
        output.append(code.toString());
        output.append("\n");
        output.append(generateSimulationTrace());
        return output.toString();
    }

    private void addSimulationStep(String instruction, String description) {
        if (executeSimulation) {
            stepCounter++;
            simulation.append(String.format("Step %d: %-40s # %s\n", stepCounter, instruction, description));
        }
    }

    private void updateRegister(String reg, long value) {
        if (executeSimulation) {
            registers.put(reg, value);
        }
    }

    private long getRegister(String reg) {
        return registers.getOrDefault(reg, 0L);
    }

    @Override
    public void visit(ProgramNode node) {
        this.returnType = node.getReturnType();
        simulation.append("# SIMULACIÓN - Programa con tipo de retorno: ").append(returnType).append("\n");
        simulation.append("# " + "=".repeat(60) + "\n\n");

        node.getMainFunction().accept(this);
    }

    @Override
    public void visit(MainFunctionNode node) {
        code.append("main:\n");
        code.append("    pushq %rbp\n");
        code.append("    movq %rsp, %rbp\n");
        addSimulationStep("pushq %rbp", "Guardar frame pointer anterior");
        addSimulationStep("movq %rsp, %rbp", "Establecer nuevo frame pointer");

        int localVarCount = node.getDeclarations().size();

        if (localVarCount > 0) {
            int stackSpace = ((localVarCount * 8) + 15) & ~15;
            code.append("    subq $").append(stackSpace).append(", %rsp\n\n");
            addSimulationStep("subq $" + stackSpace + ", %rsp",
                    "Reservar " + stackSpace + " bytes para " + localVarCount + " variable(s)");
        }

        for (DeclarationNode decl : node.getDeclarations()) {
            decl.accept(this);
        }

        for (StatementNode stmt : node.getStatements()) {
            stmt.accept(this);
        }

        // Epílogo si no hubo return explícito
        code.append("\n    movq %rbp, %rsp\n");
        code.append("    popq %rbp\n");
        code.append("    ret\n");
    }

    @Override
    public void visit(VariableDeclarationNode node) {
        stackOffset += 8;
        localVariables.put(node.getIdentifier(), stackOffset);

        if (executeSimulation) {
            variableValues.put(node.getIdentifier(), 0L);
        }

        code.append("    movq $0, -").append(stackOffset).append("(%rbp)\n");

        addSimulationStep("movq $0, -" + stackOffset + "(%rbp)",
                "Inicializar variable '" + node.getIdentifier() + "' = 0");
    }

    @Override
    public void visit(AssignmentNode node) {
        node.getExpression().accept(this);

        Integer offset = localVariables.get(node.getIdentifier());
        if (offset == null) {
            throw new RuntimeException("Variable no encontrada: " + node.getIdentifier());
        }

        code.append("    movq %rax, -").append(offset).append("(%rbp)\n");

        if (executeSimulation) {
            long value = getRegister("rax");
            variableValues.put(node.getIdentifier(), value);

            addSimulationStep("movq %rax, -" + offset + "(%rbp)",
                    "Almacenar " + value + " en variable '" + node.getIdentifier() + "'");

            simulation.append("         >> ").append(node.getIdentifier()).append(" = ").append(value).append("\n\n");
        }
    }

    @Override
    public void visit(IfNode node) {
        String elseLabel = generateLabel();
        String endLabel = generateLabel();

        simulation.append("# --- IF STATEMENT ---\n");

        // Evaluar condición
        node.getCondition().accept(this);
        lastConditionResult = getRegister("rax");

        // Generar código Assembly
        code.append("    cmpq $0, %rax\n");
        if (node.hasElse()) {
            code.append("    je ").append(elseLabel).append("\n");
            addSimulationStep("je " + elseLabel, "Saltar a ELSE si condición es falsa");
        } else {
            code.append("    je ").append(endLabel).append("\n");
            addSimulationStep("je " + endLabel, "Saltar al final si condición es falsa");
        }

        // Guardar estado antes de procesar bloques
        Map<String, Long> savedVarValues = new HashMap<>(variableValues);
        Map<String, Long> savedRegisters = new HashMap<>(registers);

        if (lastConditionResult != 0) {
            // Condición TRUE - ejecutar THEN en simulación
            simulation.append("# --- THEN BLOCK (Ejecutado) ---\n");
            executeSimulation = true;
            for (StatementNode stmt : node.getThenBlock()) {
                stmt.accept(this);
            }

            if (node.hasElse()) {
                code.append("    jmp ").append(endLabel).append("\n");
                addSimulationStep("jmp " + endLabel, "Saltar al final (skip else)");

                // Generar código ELSE pero NO ejecutar simulación
                code.append(elseLabel).append(":\n");
                simulation.append("# --- ELSE BLOCK (NO ejecutado) ---\n");

                // Restaurar estado y desactivar ejecución
                executeSimulation = false;
                for (StatementNode stmt : node.getElseBlock()) {
                    stmt.accept(this);
                }
                executeSimulation = true;
            }
        } else {
            // Condición FALSE - ejecutar ELSE en simulación
            simulation.append("# --- THEN BLOCK (NO ejecutado) ---\n");

            // Generar código THEN pero NO ejecutar simulación
            executeSimulation = false;
            for (StatementNode stmt : node.getThenBlock()) {
                stmt.accept(this);
            }
            executeSimulation = true;

            if (node.hasElse()) {
                code.append("    jmp ").append(endLabel).append("\n");
                code.append(elseLabel).append(":\n");

                simulation.append("# --- ELSE BLOCK (Ejecutado) ---\n");
                for (StatementNode stmt : node.getElseBlock()) {
                    stmt.accept(this);
                }
            }
        }

        code.append(endLabel).append(":\n");
        simulation.append("# --- END IF ---\n\n");
    }

    @Override
    public void visit(WhileNode node) {
        String loopLabel = generateLabel();
        String endLabel = generateLabel();

        simulation.append("# --- WHILE LOOP ---\n");

        code.append(loopLabel).append(":\n");

        // Evaluar condición inicial
        node.getCondition().accept(this);
        lastConditionResult = getRegister("rax");

        code.append("    cmpq $0, %rax\n");
        code.append("    je ").append(endLabel).append("\n");
        addSimulationStep("je " + endLabel, "Salir del loop si condición es falsa");

        // Simular iteraciones del while
        int iterationCount = 0;
        int maxIterations = 1000;

        while (lastConditionResult != 0 && iterationCount < maxIterations) {
            if (iterationCount > 0) {
                simulation.append("# --- Iteración " + (iterationCount + 1) + " ---\n");
            }

            for (StatementNode stmt : node.getBody()) {
                stmt.accept(this);
            }

            // Re-evaluar condición
            node.getCondition().accept(this);
            lastConditionResult = getRegister("rax");

            iterationCount++;
        }

        code.append("    jmp ").append(loopLabel).append("\n");
        addSimulationStep("jmp " + loopLabel, "Volver al inicio del loop");

        code.append(endLabel).append(":\n");
        simulation.append("# --- END WHILE (Total iteraciones: " + iterationCount + ") ---\n\n");
    }

    @Override
    public void visit(ComparisonNode node) {
        node.getLeft().accept(this);
        long leftValue = getRegister("rax");

        code.append("    pushq %rax\n");
        addSimulationStep("pushq %rax", "Guardar operando izquierdo (" + leftValue + ")");

        node.getRight().accept(this);
        long rightValue = getRegister("rax");

        code.append("    movq %rax, %rbx\n");
        code.append("    popq %rax\n");

        updateRegister("rbx", rightValue);
        updateRegister("rax", leftValue);

        addSimulationStep("movq %rax, %rbx", "Operando derecho a rbx (" + rightValue + ")");
        addSimulationStep("popq %rax", "Restaurar operando izquierdo (" + leftValue + ")");

        code.append("    cmpq %rbx, %rax\n");

        long result = 0;
        String operation = "";

        switch (node.getOperator()) {
            case "==":
                code.append("    sete %al\n");
                code.append("    movzbl %al, %eax\n");
                result = (leftValue == rightValue) ? 1 : 0;
                operation = leftValue + " == " + rightValue + " → " + (result == 1 ? "true" : "false");
                break;

            case "!=":
                code.append("    setne %al\n");
                code.append("    movzbl %al, %eax\n");
                result = (leftValue != rightValue) ? 1 : 0;
                operation = leftValue + " != " + rightValue + " → " + (result == 1 ? "true" : "false");
                break;

            case "<":
                code.append("    setl %al\n");
                code.append("    movzbl %al, %eax\n");
                result = (leftValue < rightValue) ? 1 : 0;
                operation = leftValue + " < " + rightValue + " → " + (result == 1 ? "true" : "false");
                break;

            case ">":
                code.append("    setg %al\n");
                code.append("    movzbl %al, %eax\n");
                result = (leftValue > rightValue) ? 1 : 0;
                operation = leftValue + " > " + rightValue + " → " + (result == 1 ? "true" : "false");
                break;

            case "<=":
                code.append("    setle %al\n");
                code.append("    movzbl %al, %eax\n");
                result = (leftValue <= rightValue) ? 1 : 0;
                operation = leftValue + " <= " + rightValue + " → " + (result == 1 ? "true" : "false");
                break;

            case ">=":
                code.append("    setge %al\n");
                code.append("    movzbl %al, %eax\n");
                result = (leftValue >= rightValue) ? 1 : 0;
                operation = leftValue + " >= " + rightValue + " → " + (result == 1 ? "true" : "false");
                break;
        }

        updateRegister("rax", result);
        addSimulationStep("Comparación", operation);

        if (executeSimulation) {
            simulation.append("         >> rax = ").append(result).append("\n\n");
        }
    }

    @Override
    public void visit(ReturnNode node) {
        if (node.hasExpression()) {
            node.getExpression().accept(this);
            long returnValue = getRegister("rax");

            addSimulationStep("# Return statement", "Valor de retorno = " + returnValue);

            if (executeSimulation) {
                simulation.append("         >> RETURN VALUE: ").append(returnValue).append("\n\n");
            }
        } else {
            code.append("    xorq %rax, %rax\n");
            updateRegister("rax", 0);
        }

        code.append("    movq %rbp, %rsp\n");
        code.append("    popq %rbp\n");
        code.append("    ret\n");
    }

    @Override
    public void visit(BinaryOpNode node) {
        node.getLeft().accept(this);
        long leftValue = getRegister("rax");

        code.append("    pushq %rax\n");
        addSimulationStep("pushq %rax", "Guardar operando izquierdo (" + leftValue + ")");

        node.getRight().accept(this);
        long rightValue = getRegister("rax");

        code.append("    movq %rax, %rbx\n");
        code.append("    popq %rax\n");

        updateRegister("rbx", rightValue);
        updateRegister("rax", leftValue);

        addSimulationStep("movq %rax, %rbx", "Mover operando derecho a rbx (" + rightValue + ")");
        addSimulationStep("popq %rax", "Restaurar operando izquierdo (" + leftValue + ")");

        long result = 0;
        String operation = "";

        switch (node.getOperator()) {
            case "+":
                code.append("    addq %rbx, %rax\n");
                result = leftValue + rightValue;
                operation = leftValue + " + " + rightValue + " = " + result;
                addSimulationStep("addq %rbx, %rax", operation);
                break;

            case "-":
                code.append("    subq %rbx, %rax\n");
                result = leftValue - rightValue;
                operation = leftValue + " - " + rightValue + " = " + result;
                addSimulationStep("subq %rbx, %rax", operation);
                break;

            case "*":
                code.append("    imulq %rbx, %rax\n");
                result = leftValue * rightValue;
                operation = leftValue + " * " + rightValue + " = " + result;
                addSimulationStep("imulq %rbx, %rax", operation);
                break;

            case "/":
                if (rightValue == 0) {
                    throw new RuntimeException("División por cero");
                }
                code.append("    cqto\n");
                code.append("    idivq %rbx\n");
                result = leftValue / rightValue;
                operation = leftValue + " / " + rightValue + " = " + result;
                addSimulationStep("idivq %rbx", operation);
                break;
        }

        updateRegister("rax", result);

        if (executeSimulation) {
            simulation.append("         >> rax = ").append(result).append("\n\n");
        }
    }

    @Override
    public void visit(NumberNode node) {
        long value = node.getValue();

        if (value == 0) {
            code.append("    xorq %rax, %rax\n");
            addSimulationStep("xorq %rax, %rax", "Cargar 0 en rax (optimizado)");
        } else if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
            code.append("    movl $").append(value).append(", %eax\n");
            addSimulationStep("movl $" + value + ", %eax", "Cargar constante " + value + " en eax (32-bit)");
        } else {
            code.append("    movq $").append(value).append(", %rax\n");
            addSimulationStep("movq $" + value + ", %rax", "Cargar constante " + value + " en rax (64-bit)");
        }

        updateRegister("rax", value);
    }

    @Override
    public void visit(BooleanNode node) {
        int value = node.getValue() ? 1 : 0;

        if (value == 0) {
            code.append("    xorq %rax, %rax\n");
            addSimulationStep("xorq %rax, %rax", "Cargar false (0) en rax");
        } else {
            code.append("    movl $1, %eax\n");
            addSimulationStep("movl $1, %eax", "Cargar true (1) en eax");
        }

        updateRegister("rax", value);
    }

    @Override
    public void visit(VariableNode node) {
        Integer offset = localVariables.get(node.getIdentifier());
        if (offset == null) {
            throw new RuntimeException("Variable no encontrada: " + node.getIdentifier());
        }

        code.append("    movq -").append(offset).append("(%rbp), %rax\n");

        if (executeSimulation) {
            long value = variableValues.getOrDefault(node.getIdentifier(), 0L);
            updateRegister("rax", value);

            addSimulationStep("movq -" + offset + "(%rbp), %rax",
                    "Cargar variable '" + node.getIdentifier() + "' = " + value + " en rax");
        }
    }

    private String generateLabel() {
        return ".L" + (labelCounter++);
    }

    private String generateSimulationTrace() {
        StringBuilder trace = new StringBuilder();
        trace.append("#\n# " + "=".repeat(60) + "\n");
        trace.append("# TRAZA DE EJECUCIÓN\n");
        trace.append("# " + "=".repeat(60) + "\n");
        trace.append(simulation.toString());
        trace.append("# " + "=".repeat(60) + "\n");
        trace.append("# ESTADO FINAL\n");
        trace.append("# " + "=".repeat(60) + "\n");
        trace.append("# Registros:\n");
        trace.append("#   rax = ").append(getRegister("rax")).append("\n");
        trace.append("#   rbx = ").append(getRegister("rbx")).append("\n");
        trace.append("#   rcx = ").append(getRegister("rcx")).append("\n");
        trace.append("#   rdx = ").append(getRegister("rdx")).append("\n");
        trace.append("#\n# Variables:\n");
        for (Map.Entry<String, Long> entry : variableValues.entrySet()) {
            trace.append("#   ").append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
        }
        trace.append("#\n");
        trace.append("# RESULTADO FINAL (return value): ").append(getRegister("rax")).append("\n");
        trace.append("# " + "=".repeat(60) + "\n");
        return trace.toString();
    }
}
