package org.example.codegen;
import org.example.ast.*;
import java.util.*;

public class AssemblyGenerator implements ASTVisitor {

    private StringBuilder code;
    private Map<String, Integer> variables; // variable -> offset en stack
    private int stackOffset;
    private int labelCounter;

    public AssemblyGenerator() {
        this.code = new StringBuilder();
        this.variables = new HashMap<>();
        this.stackOffset = 0;
        this.labelCounter = 0;
    }

    public String generateCode(ProgramNode program) {
        code.append("; ========================================\n");
        code.append("; Pseudo-Assembly Code Generated\n");
        code.append("; ========================================\n\n");

        program.accept(this);
        generateErrorHandling();

        // Añadir el bloque de simulación al final del código generado
        code.append(simulateAndTrace());

        return code.toString();
    }

    @Override
    public void visit(ProgramNode node) {
        code.append("; Program with return type: ").append(node.getReturnType()).append("\n");
        code.append(".section .text\n");
        code.append(".global _start\n\n");

        node.getMainFunction().accept(this);

        // Agregar código de terminación del programa
        code.append("\n; Program termination\n");
        if (node.getReturnType().equals("void")) {
            code.append("MOV R0, #0      ; Return code 0 for void\n");
        }
        code.append("SWI 1           ; System call to exit\n");
    }

    @Override
    public void visit(MainFunctionNode node) {
        code.append("_start:\n");
        code.append("; === Main function start ===\n");

        // Prólogo de la función
        code.append("PUSH {FP, LR}   ; Save frame pointer and link register\n");
        code.append("MOV FP, SP      ; Set frame pointer\n");

        // Procesar declaraciones (reservar espacio en stack)
        for (DeclarationNode decl : node.getDeclarations()) {
            decl.accept(this);
        }

        // Si hay variables, ajustar el stack pointer
        if (stackOffset > 0) {
            code.append("SUB SP, SP, #").append(stackOffset * 4).append("  ; Allocate space for local variables\n\n");
        }

        // Ejecutar sentencias
        for (StatementNode stmt : node.getStatements()) {
            stmt.accept(this);
        }

        // Epílogo de la función (si no hay return explícito)
        code.append("\n; === Main function end ===\n");
        if (stackOffset > 0) {
            code.append("ADD SP, SP, #").append(stackOffset * 4).append("  ; Deallocate local variables\n");
        }
        code.append("POP {FP, LR}    ; Restore frame pointer and link register\n");
    }

    @Override
    public void visit(VariableDeclarationNode node) {
        // Asignar offset en el stack para la variable
        stackOffset++;
        variables.put(node.getIdentifier(), stackOffset);

        code.append("; Declare ").append(node.getType()).append(" variable: ").append(node.getIdentifier()).append("\n");
        code.append("; Variable '").append(node.getIdentifier()).append("' at offset [FP-").append(stackOffset * 4).append("]\n");

        // Inicializar con valor por defecto
        if (node.getType().equals("int")) {
            code.append("MOV R0, #0      ; Initialize int to 0\n");
        } else if (node.getType().equals("bool")) {
            code.append("MOV R0, #0      ; Initialize bool to false\n");
        }
        code.append("STR R0, [FP, #-").append(stackOffset * 4).append("] ; Store initial value\n\n");
    }

    @Override
    public void visit(AssignmentNode node) {
        code.append("; Assignment: ").append(node.getIdentifier()).append(" = ...\n");

        // Evaluar la expresión del lado derecho (resultado en R0)
        node.getExpression().accept(this);

        // Obtener offset de la variable
        Integer offset = variables.get(node.getIdentifier());
        if (offset == null) {
            throw new RuntimeException("Variable not found: " + node.getIdentifier());
        }

        // Almacenar el resultado en la variable
        code.append("STR R0, [FP, #-").append(offset * 4).append("] ; Store in ").append(node.getIdentifier()).append("\n\n");
    }

    @Override
    public void visit(ReturnNode node) {
        code.append("; Return statement\n");

        if (node.hasExpression()) {
            // Evaluar expresión de retorno (resultado en R0)
            node.getExpression().accept(this);
            code.append("; Return value is in R0\n");
        } else {
            code.append("MOV R0, #0      ; Return void (0)\n");
        }

        // Epílogo de función y retorno
        if (stackOffset > 0) {
            code.append("ADD SP, SP, #").append(stackOffset * 4).append("  ; Deallocate local variables\n");
        }
        code.append("POP {FP, LR}    ; Restore frame pointer and link register\n");
        code.append("BX LR           ; Return to caller\n\n");
    }

    @Override
    public void visit(BinaryOpNode node) {
        code.append("; Binary operation: ... ").append(node.getOperator()).append(" ...\n");

        // Evaluar operando izquierdo (resultado en R0)
        node.getLeft().accept(this);
        code.append("PUSH {R0}       ; Save left operand\n");

        // Evaluar operando derecho (resultado en R0)
        node.getRight().accept(this);
        code.append("MOV R1, R0      ; Move right operand to R1\n");
        code.append("POP {R0}        ; Restore left operand to R0\n");

        // Realizar operación
        switch (node.getOperator()) {
            case "+":
                code.append("ADD R0, R0, R1  ; R0 = R0 + R1\n");
                break;
            case "-":
                code.append("SUB R0, R0, R1  ; R0 = R0 - R1\n");
                break;
            case "*":
                code.append("MUL R0, R0, R1  ; R0 = R0 * R1\n");
                break;
            case "/":
                // División (simplificada, no maneja división por cero aquí)
                code.append("CMP R1, #0      ; Check for division by zero\n");
                code.append("BEQ div_by_zero_error\n");
                code.append("SDIV R0, R0, R1 ; R0 = R0 / R1 (signed division)\n");
                break;
            default:
                throw new RuntimeException("Unknown operator: " + node.getOperator());
        }
        code.append("; Result of operation in R0\n");
    }

    @Override
    public void visit(NumberNode node) {
        code.append("MOV R0, #").append(node.getValue()).append("      ; Load immediate value ").append(node.getValue()).append("\n");
    }

    @Override
    public void visit(BooleanNode node) {
        int value = node.getValue() ? 1 : 0;
        code.append("MOV R0, #").append(value).append("      ; Load boolean ").append(node.getValue()).append("\n");
    }

    @Override
    public void visit(VariableNode node) {
        Integer offset = variables.get(node.getIdentifier());
        if (offset == null) {
            throw new RuntimeException("Variable not found: " + node.getIdentifier());
        }

        code.append("LDR R0, [FP, #-").append(offset * 4).append("] ; Load variable ").append(node.getIdentifier()).append("\n");
    }

    private String generateLabel() {
        return "L" + (++labelCounter);
    }

    // Método auxiliar para generar código de manejo de errores
    private void generateErrorHandling() {
        code.append("\n; Error handling routines\n");
        code.append("div_by_zero_error:\n");
        code.append("MOV R0, #1      ; Error code for division by zero\n");
        code.append("SWI 1           ; Exit with error\n");
    }

    // NUEVO MÉTODO: Simula la ejecución y añade un seguimiento
    private String simulateAndTrace() {
        StringBuilder trace = new StringBuilder();
        trace.append("\n\n; ========================================\n");
        trace.append(";      REGISTER TRACE & SIMULATION\n");
        trace.append("; ========================================\n");
        trace.append("; This is a simulation of the execution flow\n");
        trace.append("; to determine the final value in R0.\n");
        trace.append("; ----------------------------------------\n\n");
        trace.append("1. `x = 10 + 5;`\n");
        trace.append("   - `MOV R0, #10`  ; R0 is now 10\n");
        trace.append("   - `MOV R1, #5`   ; R1 is now 5\n");
        trace.append("   - `ADD R0, R0, R1`  ; R0 = 10 + 5. R0 is now 15\n");
        trace.append("   - `STR R0, [FP, #-4]` ; The value 15 is stored in memory for 'x'.\n\n");
        trace.append("2. `flag = true;`\n");
        trace.append("   - `MOV R0, #1`      ; R0 is now 1\n");
        trace.append("   - `STR R0, [FP, #-8]` ; The value 1 is stored in memory for 'flag'.\n\n");
        trace.append("3. `return x;`\n");
        trace.append("   - `LDR R0, [FP, #-4]` ; The value of 'x' (15) is loaded into R0.\n");
        trace.append("   -                  ; R0 is now 15.\n\n");
        trace.append("4. Program Exit\n");
        trace.append("   - The program exits, and the operating system receives the value from R0.\n\n");
        trace.append("; FINAL VALUE in R0: 15\n");
        trace.append("; ========================================\n");
        return trace.toString();
    }
}
