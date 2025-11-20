package org.example.codegen;

import java.util.*;
import org.example.ast.*;
import org.example.semantic.symboltable.SymbolTable;

public class X86AssemblyGenerator implements ASTVisitor {

    private StringBuilder code;
    private StringBuilder simulation;
    private SymbolTable symbolTable;
    private Map<String, Integer> localVariables;
    private Map<String, FunctionDeclarationNode> functions;
    private Map<String, Long> variableValues;
    private Map<String, Long> registers;
    private Stack<Map<String, Long>> callStack;
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
        this.functions = new HashMap<>();
        this.variableValues = new HashMap<>();
        this.registers = new HashMap<>();
        this.callStack = new Stack<>();
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
        output.append("# x86-64 Assembly code con  operadores lógicos\n");
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
        simulation.append("#simulacion - Programa con tipo de retorno: ").append(returnType).append("\n");
        simulation.append("# " + "=".repeat(60) + "\n\n");

        // Registrar todas las funciones primero
        for (FunctionDeclarationNode func : node.getFunctions()) {
            functions.put(func.getIdentifier(), func);
        }

        // Generar código para cada función
        for (FunctionDeclarationNode func : node.getFunctions()) {
            func.accept(this);
        }

        // Generar main
        node.getMainFunction().accept(this);
    }

    @Override
    public void visit(FunctionDeclarationNode node) {
        simulation.append("\n# --- FUNCIÓN: " + node.getIdentifier() + " ---\n");
        
        code.append(node.getIdentifier()).append(":\n");
        code.append("    pushq %rbp\n");
        code.append("    movq %rsp, %rbp\n");
        
        addSimulationStep("pushq %rbp", "Guardar frame pointer (función " + node.getIdentifier() + ")");
        addSimulationStep("movq %rsp, %rbp", "Establecer nuevo frame pointer");

        // Guardar estado de variables locales anterior
        Map<String, Integer> savedLocalVars = new HashMap<>(localVariables);
        int savedStackOffset = stackOffset;
        
        localVariables.clear();
        stackOffset = 0;

        // Procesar parámetros (están en la pila o registros según convención)
        int paramOffset = 16; // Después de rbp y return address
        for (int i = 0; i < node.getParameters().size(); i++) {
            ParameterNode param = node.getParameters().get(i);
            localVariables.put(param.getIdentifier(), paramOffset + (i * 8));
            
            if (executeSimulation) {
                // Para simulación, asumimos valores pasados
                variableValues.put(param.getIdentifier(), 0L);
            }
        }

        // Reservar espacio para variables locales
        int localVarCount = node.getLocalDeclarations().size();
        if (localVarCount > 0) {
            int stackSpace = ((localVarCount * 8) + 15) & ~15;
            code.append("    subq $").append(stackSpace).append(", %rsp\n\n");
            addSimulationStep("subq $" + stackSpace + ", %rsp",
                    "Reservar espacio para " + localVarCount + " variable(s) local(es)");
        }

        // Procesar declaraciones locales
        for (DeclarationNode decl : node.getLocalDeclarations()) {
            decl.accept(this);
        }

        // Procesar cuerpo de la función
        for (StatementNode stmt : node.getBody()) {
            stmt.accept(this);
        }

        // Epílogo de función
        code.append("\n    movq %rbp, %rsp\n");
        code.append("    popq %rbp\n");
        code.append("    ret\n\n");

        // Restaurar estado
        localVariables = savedLocalVars;
        stackOffset = savedStackOffset;
    }

    @Override
    public void visit(ParameterNode node) {
        // Los parámetros ya fueron manejados en FunctionDeclarationNode
    }

    @Override
    public void visit(FunctionCallNode node) {
        simulation.append("# --- LLAMADA A FUNCIÓN: " + node.getFunctionName() + " ---\n");

        // Evaluar argumentos de derecha a izquierda y ponerlos en la pila
        List<ExpressionNode> args = node.getArguments();
        List<Long> argValues = new ArrayList<>();
        
        for (int i = args.size() - 1; i >= 0; i--) {
            args.get(i).accept(this);
            long argValue = getRegister("rax");
            argValues.add(0, argValue);
            
            code.append("    pushq %rax\n");
            addSimulationStep("pushq %rax", "Guardar argumento " + (i+1) + " = " + argValue);
        }

        // Llamar a la función
        code.append("    call ").append(node.getFunctionName()).append("\n");
        addSimulationStep("call " + node.getFunctionName(), 
                         "Llamar función con " + args.size() + " argumento(s)");

        // Limpiar argumentos de la pila
        if (args.size() > 0) {
            int stackCleanup = args.size() * 8;
            code.append("    addq $").append(stackCleanup).append(", %rsp\n");
            addSimulationStep("addq $" + stackCleanup + ", %rsp", "Limpiar argumentos");
        }

        // Simular ejecución de función
        if (executeSimulation && functions.containsKey(node.getFunctionName())) {
            FunctionDeclarationNode func = functions.get(node.getFunctionName());
            
            // Guardar contexto actual
            callStack.push(new HashMap<>(variableValues));
            
            // Crear nuevo contexto con parámetros
            variableValues.clear();
            for (int i = 0; i < func.getParameters().size() && i < argValues.size(); i++) {
                ParameterNode param = func.getParameters().get(i);
                variableValues.put(param.getIdentifier(), argValues.get(i));
                simulation.append("         >> Parámetro " + param.getIdentifier() + 
                               " = " + argValues.get(i) + "\n");
            }
            
            // Ejecutar cuerpo (simplificado para simulación)
            long returnValue = simulateFunctionExecution(func);
            updateRegister("rax", returnValue);
            
            // Restaurar contexto
            variableValues = callStack.pop();
            
            simulation.append("         >> Retorno: " + returnValue + "\n\n");
        }

        if (executeSimulation) {
            simulation.append("         >> rax (resultado) = " + getRegister("rax") + "\n\n");
        }
    }

    private long simulateFunctionExecution(FunctionDeclarationNode func) {
        // Simulación simplificada: buscar return statement
        for (StatementNode stmt : func.getBody()) {
            if (stmt instanceof ReturnNode) {
                ReturnNode ret = (ReturnNode) stmt;
                if (ret.hasExpression()) {
                    ret.getExpression().accept(this);
                    return getRegister("rax");
                }
            }
        }
        return 0;
    }

    @Override
    public void visit(MainFunctionNode node) {
        simulation.append("\n# --- FUNCIÓN MAIN ---\n");
        
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

        node.getCondition().accept(this);
        lastConditionResult = getRegister("rax");

        code.append("    cmpq $0, %rax\n");
        if (node.hasElse()) {
            code.append("    je ").append(elseLabel).append("\n");
            addSimulationStep("je " + elseLabel, "Saltar a ELSE si condición es falsa");
        } else {
            code.append("    je ").append(endLabel).append("\n");
            addSimulationStep("je " + endLabel, "Saltar al final si condición es falsa");
        }

        if (lastConditionResult != 0) {
            simulation.append("# --- THEN BLOCK (Ejecutado) ---\n");
            executeSimulation = true;
            for (StatementNode stmt : node.getThenBlock()) {
                stmt.accept(this);
            }

            if (node.hasElse()) {
                code.append("    jmp ").append(endLabel).append("\n");
                addSimulationStep("jmp " + endLabel, "Saltar al final (skip else)");

                code.append(elseLabel).append(":\n");
                simulation.append("# --- ELSE BLOCK (NO ejecutado) ---\n");

                executeSimulation = false;
                for (StatementNode stmt : node.getElseBlock()) {
                    stmt.accept(this);
                }
                executeSimulation = true;
            }
        } else {
            simulation.append("# --- THEN BLOCK (NO ejecutado) ---\n");

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

        node.getCondition().accept(this);
        lastConditionResult = getRegister("rax");

        code.append("    cmpq $0, %rax\n");
        code.append("    je ").append(endLabel).append("\n");
        addSimulationStep("je " + endLabel, "Salir del loop si condición es falsa");

        int iterationCount = 0;
        int maxIterations = 1000;

        while (lastConditionResult != 0 && iterationCount < maxIterations) {
            if (iterationCount > 0) {
                simulation.append("# --- Iteración " + (iterationCount + 1) + " ---\n");
            }

            for (StatementNode stmt : node.getBody()) {
                stmt.accept(this);
            }

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
    public void visit(LogicalOpNode node) {
        if (node.isUnary()) {
            // Operador NOT (!)
            node.getLeft().accept(this);
            long operandValue = getRegister("rax");

            code.append("    cmpq $0, %rax\n");
            code.append("    sete %al\n");
            code.append("    movzbl %al, %eax\n");

            long result = (operandValue == 0) ? 1 : 0;
            updateRegister("rax", result);

            addSimulationStep("NOT operation", "!" + operandValue + " → " + result);
            if (executeSimulation) {
                simulation.append("         >> rax = ").append(result).append("\n\n");
            }
        } else {
            // Operadores binarios (&&, ||)
            node.getLeft().accept(this);
            long leftValue = getRegister("rax");

            String shortCircuitLabel = generateLabel();
            String endLabel = generateLabel();

            if (node.getOperator().equals("&&")) {
                // AND: si left es falso, skip right
                code.append("    cmpq $0, %rax\n");
                code.append("    je ").append(shortCircuitLabel).append("\n");
                addSimulationStep("je " + shortCircuitLabel, "Short-circuit AND si left es falso");

                if (leftValue != 0) {
                    node.getRight().accept(this);
                    long rightValue = getRegister("rax");
                    long result = (rightValue != 0) ? 1 : 0;
                    updateRegister("rax", result);
                    addSimulationStep("AND operation", leftValue + " && " + rightValue + " → " + result);
                } else {
                    updateRegister("rax", 0);
                    addSimulationStep("AND short-circuit", leftValue + " && ? → false");
                }

                code.append("    jmp ").append(endLabel).append("\n");
                code.append(shortCircuitLabel).append(":\n");
                code.append("    xorq %rax, %rax\n");
                code.append(endLabel).append(":\n");

            } else if (node.getOperator().equals("||")) {
                // OR: si left es verdadero, skip right
                code.append("    cmpq $0, %rax\n");
                code.append("    jne ").append(shortCircuitLabel).append("\n");
                addSimulationStep("jne " + shortCircuitLabel, "Short-circuit OR si left es verdadero");

                if (leftValue == 0) {
                    node.getRight().accept(this);
                    long rightValue = getRegister("rax");
                    long result = (rightValue != 0) ? 1 : 0;
                    updateRegister("rax", result);
                    addSimulationStep("OR operation", leftValue + " || " + rightValue + " → " + result);
                } else {
                    updateRegister("rax", 1);
                    addSimulationStep("OR short-circuit", leftValue + " || ? → true");
                }

                code.append("    jmp ").append(endLabel).append("\n");
                code.append(shortCircuitLabel).append(":\n");
                code.append("    movl $1, %eax\n");
                code.append(endLabel).append(":\n");
            }

            if (executeSimulation) {
                simulation.append("         >> rax = ").append(getRegister("rax")).append("\n\n");
            }
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
            addSimulationStep("movl $" + value + ", %eax", "Cargar constante " + value);
        } else {
            code.append("    movq $").append(value).append(", %rax\n");
            addSimulationStep("movq $" + value + ", %rax", "Cargar constante " + value);
        }

        updateRegister("rax", value);
    }

    @Override
    public void visit(BooleanNode node) {
        int value = node.getValue() ? 1 : 0;

        if (value == 0) {
            code.append("    xorq %rax, %rax\n");
            addSimulationStep("xorq %rax, %rax", "Cargar false (0)");
        } else {
            code.append("    movl $1, %eax\n");
            addSimulationStep("movl $1, %eax", "Cargar true (1)");
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
                    "Cargar variable '" + node.getIdentifier() + "' = " + value);
        }
    }

    @Override
    public void visit(ExpressionStatementNode node) {
        if (node.getExpression() != null) {
            node.getExpression().accept(this);
        }
    }

    private String generateLabel() {
        return ".L" + (labelCounter++);
    }

    private String generateSimulationTrace() {
        StringBuilder trace = new StringBuilder();
        trace.append("#\n# " + "=".repeat(60) + "\n");
        trace.append("# TRAZA DE Ejecucion\n");
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
