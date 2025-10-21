# ========================================
# x86-64 Assembly Code (AT&T Syntax)
# ========================================

.section .text
.global main

main:
    pushq %rbp
    movq %rsp, %rbp
    subq $16, %rsp

    movq $0, -8(%rbp)
    movq $0, -16(%rbp)
    movq $20, %rax
    movq %rax, -8(%rbp)
    movq $5, %rax
    movq %rax, -16(%rbp)
    movq -8(%rbp), %rax
    pushq %rax
    movq -16(%rbp), %rax
    movq %rax, %rbx
    popq %rax
    cqto
    idivq %rbx
    movq %rbp, %rsp
    popq %rbp
    ret
    movq %rbp, %rsp
    popq %rbp
    ret


# ========================================
#     EXECUTION TRACE & SIMULATION
# ========================================

# SIMULATION START - Program with return type: int
# ========================================

Step 1: pushq %rbp                     # Save old frame pointer
Step 2: movq %rsp, %rbp                # Set new frame pointer
Step 3: subq $16, %rsp                 # Allocate 16 bytes for 2 variable(s)
Step 4: movq $0, -8(%rbp)              # Initialize variable 'x' = 0
Step 5: movq $0, -16(%rbp)             # Initialize variable 'y' = 0
Step 6: movq $20, %rax                 # Load constant 20 into rax
         >> rax = 20

Step 7: movq %rax, -8(%rbp)            # Store 20 in variable 'x'
         >> x = 20

Step 8: movq $5, %rax                  # Load constant 5 into rax
         >> rax = 5

Step 9: movq %rax, -16(%rbp)           # Store 5 in variable 'y'
         >> y = 5

Step 10: movq -8(%rbp), %rax            # Load variable 'x' (20) into rax
         >> rax = 20

Step 11: pushq %rax                     # Save left operand (20)
Step 12: movq -16(%rbp), %rax           # Load variable 'y' (5) into rax
         >> rax = 5

Step 13: movq %rax, %rbx                # Move right operand to rbx (5)
Step 14: popq %rax                      # Restore left operand to rax (20)
Step 15: idivq %rbx                     # 20 / 5 = 4
         >> rax = 4

Step 16: # Return statement             # Return value = 4
         >> RETURN VALUE: 4

Step 17: ret                            # Return to caller

# ========================================
#           FINAL STATE
# ========================================
# Registers:
#   rax = 4
#   rbx = 5
#   rcx = 0
#   rdx = 0
#
# Variables:
#   x = 20
#   y = 5
#
# ========================================
#   FINAL RESULT (return value): 4
# ========================================

