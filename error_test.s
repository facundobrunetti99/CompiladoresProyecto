# x86-64 Assembly Code con estructuras de control
.section .text
.global main

main:
    pushq %rbp
    movq %rsp, %rbp
    subq $32, %rsp

    movq $0, -8(%rbp)
    movq $0, -16(%rbp)
    movq $0, -24(%rbp)
    movl $5, %eax
    movq %rax, -8(%rbp)
    movl $2, %eax
    movq %rax, -24(%rbp)
    xorq %rax, %rax
    movq %rax, -16(%rbp)
    movq -16(%rbp), %rax
    pushq %rax
    movl $2, %eax
    movq %rax, %rbx
    popq %rax
    cmpq %rbx, %rax
    setl %al
    movzbl %al, %eax
    cmpq $0, %rax
    je .L1
    movq -8(%rbp), %rax
    pushq %rax
    movl $50, %eax
    movq %rax, %rbx
    popq %rax
    imulq %rbx, %rax
    movq %rax, -24(%rbp)
.L1:
    movq -24(%rbp), %rax
    movq %rbp, %rsp
    popq %rbp
    ret

    movq %rbp, %rsp
    popq %rbp
    ret

#
# ============================================================
# TRAZA DE EJECUCIÓN
# ============================================================
# SIMULACIÓN - Programa con tipo de retorno: int
# ============================================================

Step 1: pushq %rbp                               # Guardar frame pointer anterior
Step 2: movq %rsp, %rbp                          # Establecer nuevo frame pointer
Step 3: subq $32, %rsp                           # Reservar 32 bytes para 3 variable(s)
Step 4: movq $0, -8(%rbp)                        # Inicializar variable 'x' = 0
Step 5: movq $0, -16(%rbp)                       # Inicializar variable 'y' = 0
Step 6: movq $0, -24(%rbp)                       # Inicializar variable 'z' = 0
Step 7: movl $5, %eax                            # Cargar constante 5 en eax (32-bit)
Step 8: movq %rax, -8(%rbp)                      # Almacenar 5 en variable 'x'
         >> x = 5

Step 9: movl $2, %eax                            # Cargar constante 2 en eax (32-bit)
Step 10: movq %rax, -24(%rbp)                     # Almacenar 2 en variable 'z'
         >> z = 2

Step 11: xorq %rax, %rax                          # Cargar 0 en rax (optimizado)
Step 12: movq %rax, -16(%rbp)                     # Almacenar 0 en variable 'y'
         >> y = 0

# --- IF STATEMENT ---
Step 13: movq -16(%rbp), %rax                     # Cargar variable 'y' = 0 en rax
Step 14: pushq %rax                               # Guardar operando izquierdo (0)
Step 15: movl $2, %eax                            # Cargar constante 2 en eax (32-bit)
Step 16: movq %rax, %rbx                          # Operando derecho a rbx (2)
Step 17: popq %rax                                # Restaurar operando izquierdo (0)
Step 18: Comparación                              # 0 < 2 → true
         >> rax = 1

Step 19: je .L1                                   # Saltar al final si condición es falsa
# --- THEN BLOCK (Ejecutado) ---
Step 20: movq -8(%rbp), %rax                      # Cargar variable 'x' = 5 en rax
Step 21: pushq %rax                               # Guardar operando izquierdo (5)
Step 22: movl $50, %eax                           # Cargar constante 50 en eax (32-bit)
Step 23: movq %rax, %rbx                          # Mover operando derecho a rbx (50)
Step 24: popq %rax                                # Restaurar operando izquierdo (5)
Step 25: imulq %rbx, %rax                         # 5 * 50 = 250
         >> rax = 250

Step 26: movq %rax, -24(%rbp)                     # Almacenar 250 en variable 'z'
         >> z = 250

# --- END IF ---

Step 27: movq -24(%rbp), %rax                     # Cargar variable 'z' = 250 en rax
Step 28: # Return statement                       # Valor de retorno = 250
         >> RETURN VALUE: 250

# ============================================================
# ESTADO FINAL
# ============================================================
# Registros:
#   rax = 250
#   rbx = 50
#   rcx = 0
#   rdx = 0
#
# Variables:
#   x = 5
#   y = 0
#   z = 250
#
# RESULTADO FINAL (return value): 250
# ============================================================

