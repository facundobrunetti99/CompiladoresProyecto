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
    movl $20, %eax
    movq %rax, -8(%rbp)
    movl $5, %eax
    movq %rax, -16(%rbp)
    movl $40, %eax
    movq %rax, -24(%rbp)
    movq -8(%rbp), %rax
    pushq %rax
    movl $10, %eax
    movq %rax, %rbx
    popq %rax
    cmpq %rbx, %rax
    setg %al
    movzbl %al, %eax
    cmpq $0, %rax
    jne .L2
    jmp .L3
.L2:
    movl $1, %eax
.L3:
    cmpq $0, %rax
    je .L1
    movl $60, %eax
    movq %rax, -24(%rbp)
.L1:
    movq -8(%rbp), %rax
    pushq %rax
    movl $20, %eax
    movq %rax, %rbx
    popq %rax
    cmpq %rbx, %rax
    sete %al
    movzbl %al, %eax
    cmpq $0, %rax
    je .L6
    movq -16(%rbp), %rax
    pushq %rax
    movl $5, %eax
    movq %rax, %rbx
    popq %rax
    cmpq %rbx, %rax
    setne %al
    movzbl %al, %eax
    jmp .L7
.L6:
    xorq %rax, %rax
.L7:
    cmpq $0, %rax
    je .L5
    movl $100, %eax
    movq %rax, -24(%rbp)
.L5:
.L8:
    movq -8(%rbp), %rax
    pushq %rax
    movl $30, %eax
    movq %rax, %rbx
    popq %rax
    cmpq %rbx, %rax
    setl %al
    movzbl %al, %eax
    cmpq $0, %rax
    je .L9
    movq -8(%rbp), %rax
    pushq %rax
    movl $1, %eax
    movq %rax, %rbx
    popq %rax
    addq %rbx, %rax
    movq %rax, -8(%rbp)
    movq -24(%rbp), %rax
    pushq %rax
    movq -8(%rbp), %rax
    movq %rax, %rbx
    popq %rax
    addq %rbx, %rax
    movq %rax, -24(%rbp)
    movq -8(%rbp), %rax
    pushq %rax
    movl $30, %eax
    movq %rax, %rbx
    popq %rax
    cmpq %rbx, %rax
    setl %al
    movzbl %al, %eax
    movq -8(%rbp), %rax
    pushq %rax
    movl $1, %eax
    movq %rax, %rbx
    popq %rax
    addq %rbx, %rax
    movq %rax, -8(%rbp)
    movq -24(%rbp), %rax
    pushq %rax
    movq -8(%rbp), %rax
    movq %rax, %rbx
    popq %rax
    addq %rbx, %rax
    movq %rax, -24(%rbp)
    movq -8(%rbp), %rax
    pushq %rax
    movl $30, %eax
    movq %rax, %rbx
    popq %rax
    cmpq %rbx, %rax
    setl %al
    movzbl %al, %eax
    movq -8(%rbp), %rax
    pushq %rax
    movl $1, %eax
    movq %rax, %rbx
    popq %rax
    addq %rbx, %rax
    movq %rax, -8(%rbp)
    movq -24(%rbp), %rax
    pushq %rax
    movq -8(%rbp), %rax
    movq %rax, %rbx
    popq %rax
    addq %rbx, %rax
    movq %rax, -24(%rbp)
    movq -8(%rbp), %rax
    pushq %rax
    movl $30, %eax
    movq %rax, %rbx
    popq %rax
    cmpq %rbx, %rax
    setl %al
    movzbl %al, %eax
    movq -8(%rbp), %rax
    pushq %rax
    movl $1, %eax
    movq %rax, %rbx
    popq %rax
    addq %rbx, %rax
    movq %rax, -8(%rbp)
    movq -24(%rbp), %rax
    pushq %rax
    movq -8(%rbp), %rax
    movq %rax, %rbx
    popq %rax
    addq %rbx, %rax
    movq %rax, -24(%rbp)
    movq -8(%rbp), %rax
    pushq %rax
    movl $30, %eax
    movq %rax, %rbx
    popq %rax
    cmpq %rbx, %rax
    setl %al
    movzbl %al, %eax
    movq -8(%rbp), %rax
    pushq %rax
    movl $1, %eax
    movq %rax, %rbx
    popq %rax
    addq %rbx, %rax
    movq %rax, -8(%rbp)
    movq -24(%rbp), %rax
    pushq %rax
    movq -8(%rbp), %rax
    movq %rax, %rbx
    popq %rax
    addq %rbx, %rax
    movq %rax, -24(%rbp)
    movq -8(%rbp), %rax
    pushq %rax
    movl $30, %eax
    movq %rax, %rbx
    popq %rax
    cmpq %rbx, %rax
    setl %al
    movzbl %al, %eax
    movq -8(%rbp), %rax
    pushq %rax
    movl $1, %eax
    movq %rax, %rbx
    popq %rax
    addq %rbx, %rax
    movq %rax, -8(%rbp)
    movq -24(%rbp), %rax
    pushq %rax
    movq -8(%rbp), %rax
    movq %rax, %rbx
    popq %rax
    addq %rbx, %rax
    movq %rax, -24(%rbp)
    movq -8(%rbp), %rax
    pushq %rax
    movl $30, %eax
    movq %rax, %rbx
    popq %rax
    cmpq %rbx, %rax
    setl %al
    movzbl %al, %eax
    movq -8(%rbp), %rax
    pushq %rax
    movl $1, %eax
    movq %rax, %rbx
    popq %rax
    addq %rbx, %rax
    movq %rax, -8(%rbp)
    movq -24(%rbp), %rax
    pushq %rax
    movq -8(%rbp), %rax
    movq %rax, %rbx
    popq %rax
    addq %rbx, %rax
    movq %rax, -24(%rbp)
    movq -8(%rbp), %rax
    pushq %rax
    movl $30, %eax
    movq %rax, %rbx
    popq %rax
    cmpq %rbx, %rax
    setl %al
    movzbl %al, %eax
    movq -8(%rbp), %rax
    pushq %rax
    movl $1, %eax
    movq %rax, %rbx
    popq %rax
    addq %rbx, %rax
    movq %rax, -8(%rbp)
    movq -24(%rbp), %rax
    pushq %rax
    movq -8(%rbp), %rax
    movq %rax, %rbx
    popq %rax
    addq %rbx, %rax
    movq %rax, -24(%rbp)
    movq -8(%rbp), %rax
    pushq %rax
    movl $30, %eax
    movq %rax, %rbx
    popq %rax
    cmpq %rbx, %rax
    setl %al
    movzbl %al, %eax
    movq -8(%rbp), %rax
    pushq %rax
    movl $1, %eax
    movq %rax, %rbx
    popq %rax
    addq %rbx, %rax
    movq %rax, -8(%rbp)
    movq -24(%rbp), %rax
    pushq %rax
    movq -8(%rbp), %rax
    movq %rax, %rbx
    popq %rax
    addq %rbx, %rax
    movq %rax, -24(%rbp)
    movq -8(%rbp), %rax
    pushq %rax
    movl $30, %eax
    movq %rax, %rbx
    popq %rax
    cmpq %rbx, %rax
    setl %al
    movzbl %al, %eax
    movq -8(%rbp), %rax
    pushq %rax
    movl $1, %eax
    movq %rax, %rbx
    popq %rax
    addq %rbx, %rax
    movq %rax, -8(%rbp)
    movq -24(%rbp), %rax
    pushq %rax
    movq -8(%rbp), %rax
    movq %rax, %rbx
    popq %rax
    addq %rbx, %rax
    movq %rax, -24(%rbp)
    movq -8(%rbp), %rax
    pushq %rax
    movl $30, %eax
    movq %rax, %rbx
    popq %rax
    cmpq %rbx, %rax
    setl %al
    movzbl %al, %eax
    jmp .L8
.L9:
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
Step 3: subq $32, %rsp                           # Reservar 32 bytes para 3 variable(s)
Step 4: movq $0, -8(%rbp)                        # Inicializar variable 'x' = 0
Step 5: movq $0, -16(%rbp)                       # Inicializar variable 'y' = 0
Step 6: movq $0, -24(%rbp)                       # Inicializar variable 'z' = 0
Step 7: movl $20, %eax                           # Cargar constante 20
Step 8: movq %rax, -8(%rbp)                      # Almacenar 20 en variable 'x'
         >> x = 20

Step 9: movl $5, %eax                            # Cargar constante 5
Step 10: movq %rax, -16(%rbp)                     # Almacenar 5 en variable 'y'
         >> y = 5

Step 11: movl $40, %eax                           # Cargar constante 40
Step 12: movq %rax, -24(%rbp)                     # Almacenar 40 en variable 'z'
         >> z = 40

# --- IF STATEMENT ---
Step 13: movq -8(%rbp), %rax                      # Cargar variable 'x' = 20
Step 14: pushq %rax                               # Guardar operando izquierdo (20)
Step 15: movl $10, %eax                           # Cargar constante 10
Step 16: movq %rax, %rbx                          # Operando derecho a rbx (10)
Step 17: popq %rax                                # Restaurar operando izquierdo (20)
Step 18: Comparación                              # 20 > 10 → true
         >> rax = 1

Step 19: jne .L2                                  # Short-circuit OR si left es verdadero
Step 20: OR short-circuit                         # 1 || ? → true
         >> rax = 1

Step 21: je .L1                                   # Saltar al final si condición es falsa
# --- THEN BLOCK (Ejecutado) ---
Step 22: movl $60, %eax                           # Cargar constante 60
Step 23: movq %rax, -24(%rbp)                     # Almacenar 60 en variable 'z'
         >> z = 60

# --- END IF ---

# --- IF STATEMENT ---
Step 24: movq -8(%rbp), %rax                      # Cargar variable 'x' = 20
Step 25: pushq %rax                               # Guardar operando izquierdo (20)
Step 26: movl $20, %eax                           # Cargar constante 20
Step 27: movq %rax, %rbx                          # Operando derecho a rbx (20)
Step 28: popq %rax                                # Restaurar operando izquierdo (20)
Step 29: Comparación                              # 20 == 20 → true
         >> rax = 1

Step 30: je .L6                                   # Short-circuit AND si left es falso
Step 31: movq -16(%rbp), %rax                     # Cargar variable 'y' = 5
Step 32: pushq %rax                               # Guardar operando izquierdo (5)
Step 33: movl $5, %eax                            # Cargar constante 5
Step 34: movq %rax, %rbx                          # Operando derecho a rbx (5)
Step 35: popq %rax                                # Restaurar operando izquierdo (5)
Step 36: Comparación                              # 5 != 5 → false
         >> rax = 0

Step 37: AND operation                            # 1 && 0 → 0
         >> rax = 0

Step 38: je .L5                                   # Saltar al final si condición es falsa
# --- THEN BLOCK (NO ejecutado) ---
# --- END IF ---

# --- WHILE LOOP ---
Step 39: movq -8(%rbp), %rax                      # Cargar variable 'x' = 20
Step 40: pushq %rax                               # Guardar operando izquierdo (20)
Step 41: movl $30, %eax                           # Cargar constante 30
Step 42: movq %rax, %rbx                          # Operando derecho a rbx (30)
Step 43: popq %rax                                # Restaurar operando izquierdo (20)
Step 44: Comparación                              # 20 < 30 → true
         >> rax = 1

Step 45: je .L9                                   # Salir del loop si condición es falsa
Step 46: movq -8(%rbp), %rax                      # Cargar variable 'x' = 20
Step 47: pushq %rax                               # Guardar operando izquierdo (20)
Step 48: movl $1, %eax                            # Cargar constante 1
Step 49: movq %rax, %rbx                          # Mover operando derecho a rbx (1)
Step 50: popq %rax                                # Restaurar operando izquierdo (20)
Step 51: addq %rbx, %rax                          # 20 + 1 = 21
         >> rax = 21

Step 52: movq %rax, -8(%rbp)                      # Almacenar 21 en variable 'x'
         >> x = 21

Step 53: movq -24(%rbp), %rax                     # Cargar variable 'z' = 60
Step 54: pushq %rax                               # Guardar operando izquierdo (60)
Step 55: movq -8(%rbp), %rax                      # Cargar variable 'x' = 21
Step 56: movq %rax, %rbx                          # Mover operando derecho a rbx (21)
Step 57: popq %rax                                # Restaurar operando izquierdo (60)
Step 58: addq %rbx, %rax                          # 60 + 21 = 81
         >> rax = 81

Step 59: movq %rax, -24(%rbp)                     # Almacenar 81 en variable 'z'
         >> z = 81

Step 60: movq -8(%rbp), %rax                      # Cargar variable 'x' = 21
Step 61: pushq %rax                               # Guardar operando izquierdo (21)
Step 62: movl $30, %eax                           # Cargar constante 30
Step 63: movq %rax, %rbx                          # Operando derecho a rbx (30)
Step 64: popq %rax                                # Restaurar operando izquierdo (21)
Step 65: Comparación                              # 21 < 30 → true
         >> rax = 1

# --- Iteración 2 ---
Step 66: movq -8(%rbp), %rax                      # Cargar variable 'x' = 21
Step 67: pushq %rax                               # Guardar operando izquierdo (21)
Step 68: movl $1, %eax                            # Cargar constante 1
Step 69: movq %rax, %rbx                          # Mover operando derecho a rbx (1)
Step 70: popq %rax                                # Restaurar operando izquierdo (21)
Step 71: addq %rbx, %rax                          # 21 + 1 = 22
         >> rax = 22

Step 72: movq %rax, -8(%rbp)                      # Almacenar 22 en variable 'x'
         >> x = 22

Step 73: movq -24(%rbp), %rax                     # Cargar variable 'z' = 81
Step 74: pushq %rax                               # Guardar operando izquierdo (81)
Step 75: movq -8(%rbp), %rax                      # Cargar variable 'x' = 22
Step 76: movq %rax, %rbx                          # Mover operando derecho a rbx (22)
Step 77: popq %rax                                # Restaurar operando izquierdo (81)
Step 78: addq %rbx, %rax                          # 81 + 22 = 103
         >> rax = 103

Step 79: movq %rax, -24(%rbp)                     # Almacenar 103 en variable 'z'
         >> z = 103

Step 80: movq -8(%rbp), %rax                      # Cargar variable 'x' = 22
Step 81: pushq %rax                               # Guardar operando izquierdo (22)
Step 82: movl $30, %eax                           # Cargar constante 30
Step 83: movq %rax, %rbx                          # Operando derecho a rbx (30)
Step 84: popq %rax                                # Restaurar operando izquierdo (22)
Step 85: Comparación                              # 22 < 30 → true
         >> rax = 1

# --- Iteración 3 ---
Step 86: movq -8(%rbp), %rax                      # Cargar variable 'x' = 22
Step 87: pushq %rax                               # Guardar operando izquierdo (22)
Step 88: movl $1, %eax                            # Cargar constante 1
Step 89: movq %rax, %rbx                          # Mover operando derecho a rbx (1)
Step 90: popq %rax                                # Restaurar operando izquierdo (22)
Step 91: addq %rbx, %rax                          # 22 + 1 = 23
         >> rax = 23

Step 92: movq %rax, -8(%rbp)                      # Almacenar 23 en variable 'x'
         >> x = 23

Step 93: movq -24(%rbp), %rax                     # Cargar variable 'z' = 103
Step 94: pushq %rax                               # Guardar operando izquierdo (103)
Step 95: movq -8(%rbp), %rax                      # Cargar variable 'x' = 23
Step 96: movq %rax, %rbx                          # Mover operando derecho a rbx (23)
Step 97: popq %rax                                # Restaurar operando izquierdo (103)
Step 98: addq %rbx, %rax                          # 103 + 23 = 126
         >> rax = 126

Step 99: movq %rax, -24(%rbp)                     # Almacenar 126 en variable 'z'
         >> z = 126

Step 100: movq -8(%rbp), %rax                      # Cargar variable 'x' = 23
Step 101: pushq %rax                               # Guardar operando izquierdo (23)
Step 102: movl $30, %eax                           # Cargar constante 30
Step 103: movq %rax, %rbx                          # Operando derecho a rbx (30)
Step 104: popq %rax                                # Restaurar operando izquierdo (23)
Step 105: Comparación                              # 23 < 30 → true
         >> rax = 1

# --- Iteración 4 ---
Step 106: movq -8(%rbp), %rax                      # Cargar variable 'x' = 23
Step 107: pushq %rax                               # Guardar operando izquierdo (23)
Step 108: movl $1, %eax                            # Cargar constante 1
Step 109: movq %rax, %rbx                          # Mover operando derecho a rbx (1)
Step 110: popq %rax                                # Restaurar operando izquierdo (23)
Step 111: addq %rbx, %rax                          # 23 + 1 = 24
         >> rax = 24

Step 112: movq %rax, -8(%rbp)                      # Almacenar 24 en variable 'x'
         >> x = 24

Step 113: movq -24(%rbp), %rax                     # Cargar variable 'z' = 126
Step 114: pushq %rax                               # Guardar operando izquierdo (126)
Step 115: movq -8(%rbp), %rax                      # Cargar variable 'x' = 24
Step 116: movq %rax, %rbx                          # Mover operando derecho a rbx (24)
Step 117: popq %rax                                # Restaurar operando izquierdo (126)
Step 118: addq %rbx, %rax                          # 126 + 24 = 150
         >> rax = 150

Step 119: movq %rax, -24(%rbp)                     # Almacenar 150 en variable 'z'
         >> z = 150

Step 120: movq -8(%rbp), %rax                      # Cargar variable 'x' = 24
Step 121: pushq %rax                               # Guardar operando izquierdo (24)
Step 122: movl $30, %eax                           # Cargar constante 30
Step 123: movq %rax, %rbx                          # Operando derecho a rbx (30)
Step 124: popq %rax                                # Restaurar operando izquierdo (24)
Step 125: Comparación                              # 24 < 30 → true
         >> rax = 1

# --- Iteración 5 ---
Step 126: movq -8(%rbp), %rax                      # Cargar variable 'x' = 24
Step 127: pushq %rax                               # Guardar operando izquierdo (24)
Step 128: movl $1, %eax                            # Cargar constante 1
Step 129: movq %rax, %rbx                          # Mover operando derecho a rbx (1)
Step 130: popq %rax                                # Restaurar operando izquierdo (24)
Step 131: addq %rbx, %rax                          # 24 + 1 = 25
         >> rax = 25

Step 132: movq %rax, -8(%rbp)                      # Almacenar 25 en variable 'x'
         >> x = 25

Step 133: movq -24(%rbp), %rax                     # Cargar variable 'z' = 150
Step 134: pushq %rax                               # Guardar operando izquierdo (150)
Step 135: movq -8(%rbp), %rax                      # Cargar variable 'x' = 25
Step 136: movq %rax, %rbx                          # Mover operando derecho a rbx (25)
Step 137: popq %rax                                # Restaurar operando izquierdo (150)
Step 138: addq %rbx, %rax                          # 150 + 25 = 175
         >> rax = 175

Step 139: movq %rax, -24(%rbp)                     # Almacenar 175 en variable 'z'
         >> z = 175

Step 140: movq -8(%rbp), %rax                      # Cargar variable 'x' = 25
Step 141: pushq %rax                               # Guardar operando izquierdo (25)
Step 142: movl $30, %eax                           # Cargar constante 30
Step 143: movq %rax, %rbx                          # Operando derecho a rbx (30)
Step 144: popq %rax                                # Restaurar operando izquierdo (25)
Step 145: Comparación                              # 25 < 30 → true
         >> rax = 1

# --- Iteración 6 ---
Step 146: movq -8(%rbp), %rax                      # Cargar variable 'x' = 25
Step 147: pushq %rax                               # Guardar operando izquierdo (25)
Step 148: movl $1, %eax                            # Cargar constante 1
Step 149: movq %rax, %rbx                          # Mover operando derecho a rbx (1)
Step 150: popq %rax                                # Restaurar operando izquierdo (25)
Step 151: addq %rbx, %rax                          # 25 + 1 = 26
         >> rax = 26

Step 152: movq %rax, -8(%rbp)                      # Almacenar 26 en variable 'x'
         >> x = 26

Step 153: movq -24(%rbp), %rax                     # Cargar variable 'z' = 175
Step 154: pushq %rax                               # Guardar operando izquierdo (175)
Step 155: movq -8(%rbp), %rax                      # Cargar variable 'x' = 26
Step 156: movq %rax, %rbx                          # Mover operando derecho a rbx (26)
Step 157: popq %rax                                # Restaurar operando izquierdo (175)
Step 158: addq %rbx, %rax                          # 175 + 26 = 201
         >> rax = 201

Step 159: movq %rax, -24(%rbp)                     # Almacenar 201 en variable 'z'
         >> z = 201

Step 160: movq -8(%rbp), %rax                      # Cargar variable 'x' = 26
Step 161: pushq %rax                               # Guardar operando izquierdo (26)
Step 162: movl $30, %eax                           # Cargar constante 30
Step 163: movq %rax, %rbx                          # Operando derecho a rbx (30)
Step 164: popq %rax                                # Restaurar operando izquierdo (26)
Step 165: Comparación                              # 26 < 30 → true
         >> rax = 1

# --- Iteración 7 ---
Step 166: movq -8(%rbp), %rax                      # Cargar variable 'x' = 26
Step 167: pushq %rax                               # Guardar operando izquierdo (26)
Step 168: movl $1, %eax                            # Cargar constante 1
Step 169: movq %rax, %rbx                          # Mover operando derecho a rbx (1)
Step 170: popq %rax                                # Restaurar operando izquierdo (26)
Step 171: addq %rbx, %rax                          # 26 + 1 = 27
         >> rax = 27

Step 172: movq %rax, -8(%rbp)                      # Almacenar 27 en variable 'x'
         >> x = 27

Step 173: movq -24(%rbp), %rax                     # Cargar variable 'z' = 201
Step 174: pushq %rax                               # Guardar operando izquierdo (201)
Step 175: movq -8(%rbp), %rax                      # Cargar variable 'x' = 27
Step 176: movq %rax, %rbx                          # Mover operando derecho a rbx (27)
Step 177: popq %rax                                # Restaurar operando izquierdo (201)
Step 178: addq %rbx, %rax                          # 201 + 27 = 228
         >> rax = 228

Step 179: movq %rax, -24(%rbp)                     # Almacenar 228 en variable 'z'
         >> z = 228

Step 180: movq -8(%rbp), %rax                      # Cargar variable 'x' = 27
Step 181: pushq %rax                               # Guardar operando izquierdo (27)
Step 182: movl $30, %eax                           # Cargar constante 30
Step 183: movq %rax, %rbx                          # Operando derecho a rbx (30)
Step 184: popq %rax                                # Restaurar operando izquierdo (27)
Step 185: Comparación                              # 27 < 30 → true
         >> rax = 1

# --- Iteración 8 ---
Step 186: movq -8(%rbp), %rax                      # Cargar variable 'x' = 27
Step 187: pushq %rax                               # Guardar operando izquierdo (27)
Step 188: movl $1, %eax                            # Cargar constante 1
Step 189: movq %rax, %rbx                          # Mover operando derecho a rbx (1)
Step 190: popq %rax                                # Restaurar operando izquierdo (27)
Step 191: addq %rbx, %rax                          # 27 + 1 = 28
         >> rax = 28

Step 192: movq %rax, -8(%rbp)                      # Almacenar 28 en variable 'x'
         >> x = 28

Step 193: movq -24(%rbp), %rax                     # Cargar variable 'z' = 228
Step 194: pushq %rax                               # Guardar operando izquierdo (228)
Step 195: movq -8(%rbp), %rax                      # Cargar variable 'x' = 28
Step 196: movq %rax, %rbx                          # Mover operando derecho a rbx (28)
Step 197: popq %rax                                # Restaurar operando izquierdo (228)
Step 198: addq %rbx, %rax                          # 228 + 28 = 256
         >> rax = 256

Step 199: movq %rax, -24(%rbp)                     # Almacenar 256 en variable 'z'
         >> z = 256

Step 200: movq -8(%rbp), %rax                      # Cargar variable 'x' = 28
Step 201: pushq %rax                               # Guardar operando izquierdo (28)
Step 202: movl $30, %eax                           # Cargar constante 30
Step 203: movq %rax, %rbx                          # Operando derecho a rbx (30)
Step 204: popq %rax                                # Restaurar operando izquierdo (28)
Step 205: Comparación                              # 28 < 30 → true
         >> rax = 1

# --- Iteración 9 ---
Step 206: movq -8(%rbp), %rax                      # Cargar variable 'x' = 28
Step 207: pushq %rax                               # Guardar operando izquierdo (28)
Step 208: movl $1, %eax                            # Cargar constante 1
Step 209: movq %rax, %rbx                          # Mover operando derecho a rbx (1)
Step 210: popq %rax                                # Restaurar operando izquierdo (28)
Step 211: addq %rbx, %rax                          # 28 + 1 = 29
         >> rax = 29

Step 212: movq %rax, -8(%rbp)                      # Almacenar 29 en variable 'x'
         >> x = 29

Step 213: movq -24(%rbp), %rax                     # Cargar variable 'z' = 256
Step 214: pushq %rax                               # Guardar operando izquierdo (256)
Step 215: movq -8(%rbp), %rax                      # Cargar variable 'x' = 29
Step 216: movq %rax, %rbx                          # Mover operando derecho a rbx (29)
Step 217: popq %rax                                # Restaurar operando izquierdo (256)
Step 218: addq %rbx, %rax                          # 256 + 29 = 285
         >> rax = 285

Step 219: movq %rax, -24(%rbp)                     # Almacenar 285 en variable 'z'
         >> z = 285

Step 220: movq -8(%rbp), %rax                      # Cargar variable 'x' = 29
Step 221: pushq %rax                               # Guardar operando izquierdo (29)
Step 222: movl $30, %eax                           # Cargar constante 30
Step 223: movq %rax, %rbx                          # Operando derecho a rbx (30)
Step 224: popq %rax                                # Restaurar operando izquierdo (29)
Step 225: Comparación                              # 29 < 30 → true
         >> rax = 1

# --- Iteración 10 ---
Step 226: movq -8(%rbp), %rax                      # Cargar variable 'x' = 29
Step 227: pushq %rax                               # Guardar operando izquierdo (29)
Step 228: movl $1, %eax                            # Cargar constante 1
Step 229: movq %rax, %rbx                          # Mover operando derecho a rbx (1)
Step 230: popq %rax                                # Restaurar operando izquierdo (29)
Step 231: addq %rbx, %rax                          # 29 + 1 = 30
         >> rax = 30

Step 232: movq %rax, -8(%rbp)                      # Almacenar 30 en variable 'x'
         >> x = 30

Step 233: movq -24(%rbp), %rax                     # Cargar variable 'z' = 285
Step 234: pushq %rax                               # Guardar operando izquierdo (285)
Step 235: movq -8(%rbp), %rax                      # Cargar variable 'x' = 30
Step 236: movq %rax, %rbx                          # Mover operando derecho a rbx (30)
Step 237: popq %rax                                # Restaurar operando izquierdo (285)
Step 238: addq %rbx, %rax                          # 285 + 30 = 315
         >> rax = 315

Step 239: movq %rax, -24(%rbp)                     # Almacenar 315 en variable 'z'
         >> z = 315

Step 240: movq -8(%rbp), %rax                      # Cargar variable 'x' = 30
Step 241: pushq %rax                               # Guardar operando izquierdo (30)
Step 242: movl $30, %eax                           # Cargar constante 30
Step 243: movq %rax, %rbx                          # Operando derecho a rbx (30)
Step 244: popq %rax                                # Restaurar operando izquierdo (30)
Step 245: Comparación                              # 30 < 30 → false
         >> rax = 0

Step 246: jmp .L8                                  # Volver al inicio del loop
# --- END WHILE (Total iteraciones: 10) ---

Step 247: movq -24(%rbp), %rax                     # Cargar variable 'z' = 315
Step 248: # Return statement                       # Valor de retorno = 315
         >> RETURN VALUE: 315

# ============================================================
# ESTADO FINAL
# ============================================================
# Registros:
#   rax = 315
#   rbx = 30
#   rcx = 0
#   rdx = 0
#
# Variables:
#   x = 30
#   y = 5
#   z = 315
#
# RESULTADO FINAL (return value): 315
# ============================================================

