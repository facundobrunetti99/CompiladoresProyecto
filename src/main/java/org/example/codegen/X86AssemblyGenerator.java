package org.example.codegen;

import org.example.ast.*;
import java.util.*;

/**
 * Generador de Assembly x86-64 limpio y minimalista
 * Estilo similar a ejemplos educativos (como fibonacci)
 */
public class X86AssemblyGenerator implements ASTVisitor {

    private StringBuilder code;
    private StringBuilder simulation;
    private Map<String, Integer> variables;
    private Map<String, Integer> simulatedMemory;
    private Map<String, Long> registers;
    private int stackOffset;
    private int labelCounter;
    private String returnType;
    private int stepCounter;
    private boolean hasReturn = false;

    public X86AssemblyGenerator() {
        this.code = new StringBuilder();
        this.simulation = new StringBuilder();
        this.variables = new HashMap<>();
        this.simulatedMemory = new HashMap<>();
        this.registers = new HashMap<>();
        this.stackOffset = 0;
        this.labelCounter = 0;
        this.stepCounter = 0;
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
        output.append("# ========================================\n");
        output.append("# EXECUTABLE x86-64 Assembly Code\n");
        output.append("# ========================================\n\n");
        output.append(".section .text\n");
        output.append(".global main\n\n");
        output.append(code.toString());
        output.append("\n");
        output.append(generateSimulationTrace());
        
        return output.toString();
    }

    private void addSimulationStep(String instruction, String description) {
        stepCounter++;
        simulation.append(String.format("Step %d: %-40s # %s\n", stepCounter, instruction, description));
    }

    private void updateRegister(String reg, long value) {
        registers.put(reg, value);
    }

    private long getRegister(String reg) {
        return registers.getOrDefault(reg, 0L);
    }

    @Override
    public void visit(ProgramNode node) {
        this.returnType = node.getReturnType();
        simulation.append("# SIMULATION START - Program with return type: ").append(returnType).append("\n");
        simulation.append("# ========================================\n\n");
        
        node.getMainFunction().accept(this);
    }

    @Override
    public void visit(MainFunctionNode node) {
        code.append("main:\n");
        code.append("    pushq %rbp\n");
        code.append("    movq %rsp, %rbp\n");
        
        addSimulationStep("pushq %rbp", "Save old frame pointer");
        addSimulationStep("movq %rsp, %rbp", "Set new frame pointer");

        int localVarCount = node.getDeclarations().size();
        
        if (localVarCount > 0) {
            int stackSpace = ((localVarCount * 8) + 15) & ~15;
            code.append("    subq $").append(stackSpace).append(", %rsp\n\n");
            addSimulationStep("subq $" + stackSpace + ", %rsp", 
                "Allocate " + stackSpace + " bytes for " + localVarCount + " variable(s)");
        }

        for (DeclarationNode decl : node.getDeclarations()) {
            decl.accept(this);
        }

        for (StatementNode stmt : node.getStatements()) {
            stmt.accept(this);
        }
    }

    @Override
    public void visit(VariableDeclarationNode node) {
        stackOffset += 8;
        variables.put(node.getIdentifier(), stackOffset);
        simulatedMemory.put(node.getIdentifier(), 0);

        code.append("    movq $0, -").append(stackOffset).append("(%rbp)\n");
        
        addSimulationStep("movq $0, -" + stackOffset + "(%rbp)", 
            "Initialize variable '" + node.getIdentifier() + "' = 0");
    }

    @Override
    public void visit(AssignmentNode node) {
        node.getExpression().accept(this);

        Integer offset = variables.get(node.getIdentifier());
        if (offset == null) {
            throw new RuntimeException("Variable not found: " + node.getIdentifier());
        }

        code.append("    movq %rax, -").append(offset).append("(%rbp)\n");
        
        long value = getRegister("rax");
        simulatedMemory.put(node.getIdentifier(), (int)value);
        
        addSimulationStep("movq %rax, -" + offset + "(%rbp)", 
            "Store " + value + " in variable '" + node.getIdentifier() + "'");
        
        simulation.append("         >> ").append(node.getIdentifier()).append(" = ").append(value).append("\n\n");
    }

    @Override
    public void visit(ReturnNode node) {
        hasReturn = true;
        
        if (node.hasExpression()) {
            node.getExpression().accept(this);
            long returnValue = getRegister("rax");
            
            addSimulationStep("# Return statement", "Return value = " + returnValue);
            simulation.append("         >> RETURN VALUE: ").append(returnValue).append("\n\n");
        } else {
            code.append("    movq $0, %rax\n");
            updateRegister("rax", 0);
        }

        // Epílogo limpio
        code.append("    movq %rbp, %rsp\n");
        code.append("    popq %rbp\n");
        code.append("    ret\n");
        
        addSimulationStep("movq %rbp, %rsp", "Restore stack pointer");
        addSimulationStep("popq %rbp", "Restore frame pointer");
        addSimulationStep("ret", "Return with value " + getRegister("rax"));
    }

    @Override
    public void visit(BinaryOpNode node) {
        node.getLeft().accept(this);
        long leftValue = getRegister("rax");
        
        code.append("    pushq %rax\n");
        addSimulationStep("pushq %rax", "Save left operand (" + leftValue + ")");

        node.getRight().accept(this);
        long rightValue = getRegister("rax");
        
        code.append("    movq %rax, %rbx\n");
        code.append("    popq %rax\n");
        
        updateRegister("rbx", rightValue);
        updateRegister("rax", leftValue);
        
        addSimulationStep("movq %rax, %rbx", "Move right operand to rbx (" + rightValue + ")");
        addSimulationStep("popq %rax", "Restore left operand to rax (" + leftValue + ")");

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
                    throw new RuntimeException("Division by zero");
                }
                
                // División usando loop (compatible con emuladores básicos)
                String divLabel = generateLabel();
                
                code.append("    movq $0, %rcx\n");
                code.append(divLabel).append("_loop:\n");
                code.append("    cmpq %rbx, %rax\n");
                code.append("    jl ").append(divLabel).append("_end\n");
                code.append("    subq %rbx, %rax\n");
                code.append("    addq $1, %rcx\n");
                code.append("    jmp ").append(divLabel).append("_loop\n");
                code.append(divLabel).append("_end:\n");
                code.append("    movq %rcx, %rax\n");
                
                result = leftValue / rightValue;
                operation = leftValue + " / " + rightValue + " = " + result;
                addSimulationStep("Division loop", operation);
                break;
        }
        
        updateRegister("rax", result);
        simulation.append("         >> rax = ").append(result).append("\n\n");
    }

    @Override
    public void visit(NumberNode node) {
        code.append("    movq $").append(node.getValue()).append(", %rax\n");
        
        updateRegister("rax", node.getValue());
        addSimulationStep("movq $" + node.getValue() + ", %rax", 
            "Load constant " + node.getValue() + " into rax");
        simulation.append("         >> rax = ").append(node.getValue()).append("\n\n");
    }

    @Override
    public void visit(BooleanNode node) {
        int value = node.getValue() ? 1 : 0;
        code.append("    movq $").append(value).append(", %rax\n");
        
        updateRegister("rax", value);
        addSimulationStep("movq $" + value + ", %rax", 
            "Load boolean " + node.getValue() + " (" + value + ") into rax");
    }

    @Override
    public void visit(VariableNode node) {
        Integer offset = variables.get(node.getIdentifier());
        if (offset == null) {
            throw new RuntimeException("Variable not found: " + node.getIdentifier());
        }

        code.append("    movq -").append(offset).append("(%rbp), %rax\n");
        
        int value = simulatedMemory.getOrDefault(node.getIdentifier(), 0);
        updateRegister("rax", value);
        
        addSimulationStep("movq -" + offset + "(%rbp), %rax", 
            "Load variable '" + node.getIdentifier() + "' (" + value + ") into rax");
        simulation.append("         >> rax = ").append(value).append("\n\n");
    }

    private String generateLabel() {
        return ".L" + (labelCounter++);
    }

    private String generateSimulationTrace() {
        StringBuilder trace = new StringBuilder();
        
        trace.append("# ========================================\n");
        trace.append("#     EXECUTION TRACE & SIMULATION\n");
        trace.append("# ========================================\n\n");
        
        trace.append(simulation.toString());
        
        trace.append("\n# ========================================\n");
        trace.append("#           FINAL STATE\n");
        trace.append("# ========================================\n");
        trace.append("# Registers:\n");
        trace.append("#   rax = ").append(getRegister("rax")).append("\n");
        trace.append("#   rbx = ").append(getRegister("rbx")).append("\n");
        trace.append("#   rcx = ").append(getRegister("rcx")).append("\n");
        trace.append("#   rdx = ").append(getRegister("rdx")).append("\n");
        trace.append("#\n# Variables:\n");
        
        for (Map.Entry<String, Integer> entry : simulatedMemory.entrySet()) {
            trace.append("#   ").append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
        }
        
        trace.append("#\n");
        trace.append("# ========================================\n");
        trace.append("#   FINAL RESULT (return value): ").append(getRegister("rax")).append("\n");
        trace.append("# ========================================\n");
        
        return trace.toString();
    }
}