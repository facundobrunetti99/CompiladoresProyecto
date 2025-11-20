# x86-64 Assembly code con  operadores lógicos
.section .text
.global main

main:
    pushq %rbp
    movq %rsp, %rbp
    subq $32, %rsp

    movq $0, -8(%rbp)
    movq $0, -16(%rbp)
    movq $0, -24(%rbp)
    movq $0, -32(%rbp)
    movl $10, %eax
    movq %rax, -8(%rbp)
    movl $15, %eax
    movq %rax, -24(%rbp)
    movl $5, %eax
    movq %rax, -16(%rbp)
    movq -24(%rbp), %rax
    movq %rbp, %rsp
    popq %rbp
    ret

    movq %rbp, %rsp
    popq %rbp
    ret

#
# ============================================================
# TRAZA DE Ejecucion
# ============================================================
#simulacion - Programa con tipo de retorno: int
# ============================================================


# --- FUNCIÓN MAIN ---
Step 1: pushq %rbp                               # Guardar frame pointer anterior
Step 2: movq %rsp, %rbp                          # Establecer nuevo frame pointer
Step 3: subq $32, %rsp                           # Reservar 32 bytes para 4 variable(s)
Step 4: movq $0, -8(%rbp)                        # Inicializar variable 'x' = 0
Step 5: movq $0, -16(%rbp)                       # Inicializar variable 'y' = 0
Step 6: movq $0, -24(%rbp)                       # Inicializar variable 'z' = 0
Step 7: movq $0, -32(%rbp)                       # Inicializar variable 'result' = 0
Step 8: movl $10, %eax                           # Cargar constante 10
Step 9: movq %rax, -8(%rbp)                      # Almacenar 10 en variable 'x'
         >> x = 10

Step 10: movl $15, %eax                           # Cargar constante 15
Step 11: movq %rax, -24(%rbp)                     # Almacenar 15 en variable 'z'
         >> z = 15

Step 12: movl $5, %eax                            # Cargar constante 5
Step 13: movq %rax, -16(%rbp)                     # Almacenar 5 en variable 'y'
         >> y = 5

Step 14: movq -24(%rbp), %rax                     # Cargar variable 'z' = 15
Step 15: # Return statement                       # Valor de retorno = 15
         >> RETURN VALUE: 15

# ============================================================
# ESTADO FINAL
# ============================================================
# Registros:
#   rax = 15
#   rbx = 0
#   rcx = 0
#   rdx = 0
#
# Variables:
#   result = 0
#   x = 10
#   y = 5
#   z = 15
#
# RESULTADO FINAL (return value): 15
# ============================================================

