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
    movq $20, %rax
    movq %rax, -8(%rbp)
    movq $5, %rax
    movq %rax, -16(%rbp)
    movq -8(%rbp), %rax
    pushq %rax
    movq -16(%rbp), %rax
    movq %rax, %rbx
    popq %rax
    cmpq %rbx, %rax
    setg %al
    movzbq %al, %rax
    cmpq $0, %rax
    je .L0
    movq -8(%rbp), %rax
    pushq %rax
    movq $50, %rax
    movq %rax, %rbx
    popq %rax
    imulq %rbx, %rax
    movq %rax, -24(%rbp)
    jmp .L1
.L0:
    movq -16(%rbp), %rax
    pushq %rax
    movq $10, %rax
    movq %rax, %rbx
    popq %rax
    imulq %rbx, %rax
    movq %rax, -24(%rbp)
.L1:

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
Step 7: movq $20, %rax                           # Cargar constante 20 en rax
Step 8: movq %rax, -8(%rbp)                      # Almacenar 20 en variable 'x'
         >> x = 20

Step 9: movq $5, %rax                            # Cargar constante 5 en rax
Step 10: movq %rax, -16(%rbp)                     # Almacenar 5 en variable 'y'
         >> y = 5

# --- IF STATEMENT ---
Step 11: movq -8(%rbp), %rax                      # Cargar variable 'x' = 20 en rax
Step 12: pushq %rax                               # Guardar operando izquierdo (20)
Step 13: movq -16(%rbp), %rax                     # Cargar variable 'y' = 5 en rax
Step 14: movq %rax, %rbx                          # Operando derecho a rbx (5)
Step 15: popq %rax                                # Restaurar operando izquierdo (20)
Step 16: Comparación                              # 20 > 5 → true
         >> rax = 1

Step 17: je .L0                                   # Saltar a ELSE si condición es falsa
# --- THEN BLOCK (Ejecutado) ---
Step 18: movq -8(%rbp), %rax                      # Cargar variable 'x' = 20 en rax
Step 19: pushq %rax                               # Guardar operando izquierdo (20)
Step 20: movq $50, %rax                           # Cargar constante 50 en rax
Step 21: movq %rax, %rbx                          # Mover operando derecho a rbx (50)
Step 22: popq %rax                                # Restaurar operando izquierdo (20)
Step 23: imulq %rbx, %rax                         # 20 * 50 = 1000
         >> rax = 1000

Step 24: movq %rax, -24(%rbp)                     # Almacenar 1000 en variable 'z'
         >> z = 1000

Step 25: jmp .L1                                  # Saltar al final (skip else)
# --- ELSE BLOCK (NO ejecutado) ---
# --- END IF ---

# ============================================================
# ESTADO FINAL
# ============================================================
# Registros:
#   rax = 1000
#   rbx = 50
#   rcx = 0
#   rdx = 0
#
# Variables:
#   x = 20
#   y = 5
#   z = 1000
#
# RESULTADO FINAL (return value): 1000
# ============================================================

