# ========================================
# EXECUTABLE x86-64 Assembly Code
# ========================================

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
    movq $40, %rax
    movq %rax, -24(%rbp)
    movq -8(%rbp), %rax
    pushq %rax
    movq -16(%rbp), %rax
    movq %rax, %rbx
    popq %rax
    movq $0, %rcx
.L0_loop:
    cmpq %rbx, %rax
    jl .L0_end
    subq %rbx, %rax
    addq $1, %rcx
    jmp .L0_loop
.L0_end:
    movq %rcx, %rax
    pushq %rax
    movq -24(%rbp), %rax
    movq %rax, %rbx
    popq %rax
    imulq %rbx, %rax
    movq %rbp, %rsp
    popq %rbp
    ret

# ========================================
#     EXECUTION TRACE & SIMULATION
# ========================================

# SIMULATION START - Program with return type: int
# ========================================

Step 1: pushq %rbp                               # Save old frame pointer
Step 2: movq %rsp, %rbp                          # Set new frame pointer
Step 3: subq $32, %rsp                           # Allocate 32 bytes for 3 variable(s)
Step 4: movq $0, -8(%rbp)                        # Initialize variable 'x' = 0
Step 5: movq $0, -16(%rbp)                       # Initialize variable 'y' = 0
Step 6: movq $0, -24(%rbp)                       # Initialize variable 'z' = 0
Step 7: movq $20, %rax                           # Load constant 20 into rax
         >> rax = 20

Step 8: movq %rax, -8(%rbp)                      # Store 20 in variable 'x'
         >> x = 20

Step 9: movq $5, %rax                            # Load constant 5 into rax
         >> rax = 5

Step 10: movq %rax, -16(%rbp)                     # Store 5 in variable 'y'
         >> y = 5

Step 11: movq $40, %rax                           # Load constant 40 into rax
         >> rax = 40

Step 12: movq %rax, -24(%rbp)                     # Store 40 in variable 'z'
         >> z = 40

Step 13: movq -8(%rbp), %rax                      # Load variable 'x' (20) into rax
         >> rax = 20

Step 14: pushq %rax                               # Save left operand (20)
Step 15: movq -16(%rbp), %rax                     # Load variable 'y' (5) into rax
         >> rax = 5

Step 16: movq %rax, %rbx                          # Move right operand to rbx (5)
Step 17: popq %rax                                # Restore left operand to rax (20)
Step 18: Division loop                            # 20 / 5 = 4
         >> rax = 4

Step 19: pushq %rax                               # Save left operand (4)
Step 20: movq -24(%rbp), %rax                     # Load variable 'z' (40) into rax
         >> rax = 40

Step 21: movq %rax, %rbx                          # Move right operand to rbx (40)
Step 22: popq %rax                                # Restore left operand to rax (4)
Step 23: imulq %rbx, %rax                         # 4 * 40 = 160
         >> rax = 160

Step 24: # Return statement                       # Return value = 160
         >> RETURN VALUE: 160

Step 25: movq %rbp, %rsp                          # Restore stack pointer
Step 26: popq %rbp                                # Restore frame pointer
Step 27: ret                                      # Return with value 160

# ========================================
#           FINAL STATE
# ========================================
# Registers:
#   rax = 160
#   rbx = 40
#   rcx = 0
#   rdx = 0
#
# Variables:
#   x = 20
#   y = 5
#   z = 40
#
# ========================================
#   FINAL RESULT (return value): 160
# ========================================

