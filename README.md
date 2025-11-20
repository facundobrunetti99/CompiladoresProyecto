Compilador de Lenguaje Imperativo con Generación de Código x86-64

- Nombre y apellidos de los integrantes del grupo;
- Velasco Benjamin
- Villegas Rodrigo
- Barroso Gonzalo
- Garcia Brunetti Facundo



Índice
- [Objetivo del Proyecto](#objetivo-del-proyecto)
- [Arquitectura General](#arquitectura-general)
- [Flujo de Compilación](#flujo-de-compilación)
- [Componentes Principales](#componentes-principales)
- [Decisiones de Diseño](#decisiones-de-diseño)
- [Ejemplo Completo](#ejemplo-completo-de-compilación)

---
Objetivo del Proyecto

Este proyecto implementa un compilador para un lenguaje imperativo que genera código ensamblador x86-64. El objetivo principal es demostrar todas las fases de compilación:


EJECUCION 
- Posicionarse en la carpeta raiz /CompiladoresProyecto/ =>> escribir el comando aca.
- COMANDO DE EJECUCION
- java -cp "target/classes;java-cup-11b-runtime.jar" org.example.Main_extended  error_test.txt -all
- El archivo error_test.txt se encuentra en la carpeta raiz /CompiladoresProyecto/
- Ademas ahi mismo se genera el codigo assembly error_test.s
- (Podemos cambiar o agregar cualquier archivo txt para poder ejecutarlo, solo basta cambiar el contenido del error_test.txt)
- En este caso el proyecto ya tiene generado el parser, sym . De no ser asi aplicar estos comandos en consola
- PASER CUP (ejecucion en consola en el editor de codigo)
-  java -jar java-cup-11b.jar -destdir src/main/java/org/example/lparser -package org.example.lparser -parser parser -symbols sym src/main/java/org/example/ycalc_extended.cup

-------------------------------------------------------------------------------------------

1. Análisis Léxico: Convertir código fuente en tokens
2. Análisis Sintáctico: Construir un Árbol de Sintaxis Abstracta (AST)
3. Análisis Semántico: Verificar consistencia y corrección del programa
4. Generación de Código: Producir código ensamblador x86-64 funcional
5. Simulación: Trazar la ejecución del código generado

Características del Lenguaje
- Tipos de datos: `int`, `bool`, `void`
- Funciones con parámetros y valores de retorno
- Estructuras de control: `if-else`, `while`
- Operadores aritméticos: `+`, `-`, `*`, `/`
- Operadores de comparación: `==`, `!=`, `<`, `>`, `<=`, `>=`
- Operadores lógicos: `&&`, `||`, `!`
- Llamadas a funciones

---
 Arquitectura General
```
┌─────────────────────────────────────────────────────────────┐
│                     CÓDIGO FUENTE (.txt)                    │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│  FASE 1: ANÁLISIS LÉXICO (Lexer.jflex)                     │
│  • Tokenización del código fuente                           │
│  • Reconocimiento de palabras clave, identificadores        │
│  • Generación de tokens para el parser                      │
└──────────────────────┬──────────────────────────────────────┘
                       │ Stream de Tokens
                       ▼
┌─────────────────────────────────────────────────────────────┐
│  FASE 2: ANÁLISIS SINTÁCTICO (parser.cup)                  │
│  • Construcción del AST (Abstract Syntax Tree)              │
│  • Verificación de estructura gramatical                    │
│  • Aplicación de reglas de precedencia                      │
└──────────────────────┬──────────────────────────────────────┘
                       │ AST (ProgramNode)
                       ▼
┌─────────────────────────────────────────────────────────────┐
│  FASE 3: ANÁLISIS SEMÁNTICO (SemanticAnalyzer)             │
│  • Verificación de declaración de variables                 │
│  • Validación de tipos                                      │
│  • Comprobación de ámbitos (scopes)                         │
│  • Verificación de llamadas a funciones                     │
│  • Construcción de tabla de símbolos                        │
└──────────────────────┬──────────────────────────────────────┘
                       │ AST Validado + Symbol Table
                       ▼
┌─────────────────────────────────────────────────────────────┐
│  FASE 4: GENERACIÓN DE CÓDIGO (X86AssemblyGenerator)       │
│  • Generación de código ensamblador x86-64                  │
│  • Gestión de stack frames                                  │
│  • Asignación de registros                                  │
│  • Implementación de convenciones de llamada                │
│  • Simulación de ejecución para traza                       │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│              CÓDIGO ASSEMBLY (.s) + TRAZA                   │
└─────────────────────────────────────────────────────────────┘
```


Flujo de Compilación

1. Entrada del Usuario (`Main_extended.java`)
```bash
java Main_extended programa.txt -asm
```

Responsabilidades:
- Procesar argumentos de línea de comandos
- Coordinar todas las fases de compilación
- Manejar errores y salidas

Opciones disponibles:
- `-tokens`: Mostrar tokens generados
- `-parse`: Solo realizar parsing
- `-tree`: Mostrar estructura del AST
- `-symbols`: Mostrar tabla de símbolos
- `-asm`: Generar código ensamblador
- `-all`: Ejecutar todas las fases



2. Análisis Léxico (`Lexer.jflex`)

¿Por qué usamos JFlex?
- Generación automática de analizadores léxicos
- Expresiones regulares para definir tokens
- Manejo eficiente de la entrada
- Integración directa con CUP (parser)

Tokens reconocidos:
```
Palabras clave: int, bool, void, main, func, return, if, else, while, true, false
Operadores:     +, -, *, /, =, ==, !=, <, >, <=, >=, &&, ||, !
Delimitadores:  (, ), {, }, ;, ,
Literales:      números enteros
Identificadores: variables y funciones
```
3.Análisis Sintáctico(`parser.cup`)
¿Por qué usamos CUP?
- Generador de parsers LALR(1)
- Definición declarativa de gramática
- Construcción automática del AST
- Manejo de precedencia y asociatividad

Estructura de la Gramática:
```
program → return_type function_list main_function

function_declaration → return_type ID ( parameters ) { declarations statements }

statement → assignment | return | if | while | expression

expression → arithmetic_expr | logical_expr | comparison_expr

logical_expr → logical_expr && logical_expr
             | logical_expr || logical_expr
             | ! logical_expr


Precedencia de Operadores (de menor a mayor):

1. OR (||)
2. AND (&&)
3. NOT (!)
4. Comparación (==, !=, <, >, <=, >=)
5. Suma/Resta (+, -)
6. Multiplicación/División (*, /)


¿Por qué esta precedencia?
- Refleja la semántica natural de las operaciones
- Compatible con lenguajes como C/Java
- Permite expresiones complejas sin paréntesis excesivos
---

4.Árbol de Sintaxis Abstracta (AST) (`org.example.ast.*`)

Patrón de Diseño: Visitor Pattern**

¿Por qué Visitor?
- Separación entre estructura de datos y operaciones
- Facilita agregar nuevas operaciones sin modificar nodos
- Usado en análisis semántico y generación de código
- Permite múltiples recorridos del AST

Jerarquía de Nodos:
```
ASTNode (interface)
├── ProgramNode
├── DeclarationNode
│   ├── FunctionDeclarationNode
│   ├── ParameterNode
│   └── VariableDeclarationNode
├── StatementNode
│   ├── AssignmentNode
│   ├── IfNode
│   ├── WhileNode
│   ├── ReturnNode
│   ├── ExpressionStatementNode
│   └── MainFunctionNode
└── ExpressionNode
    ├── BinaryOpNode (aritméticos)
    ├── ComparisonNode
    ├── LogicalOpNode
    ├── FunctionCallNode
    ├── NumberNode
    ├── BooleanNode
    └── VariableNode
```

Ejemplo de AST:
```
Código: int x = 5 + 3;

AST:
AssignmentNode
├── identifier: "x"
└── expression: BinaryOpNode
    ├── operator: "+"
    ├── left: NumberNode(5)
    └── right: NumberNode(3)


5.Análisis Semántico** (`SemanticAnalyzer.java`)

Objetivo: Verificar que el programa tiene sentido más allá de la sintaxis

Verificaciones realizadas:

a)Declaración de Variables
```java
int x;
y = 5;  // ERROR: 'y' no declarada
```

b)No Redeclaración en el Mismo Scope
```java
int x;
int x;  // ERROR: 'x' ya declarada
```

c)Declaración de Funciones
```java
foo();  // ERROR: 'foo' no declarada
```

d)Número de Argumentos
```java
int suma(int a, int b) { return a + b; }
suma(5);  // ERROR: esperaba 2 argumentos, recibió 1
```

e)Gestión de Scopes
```java
int x = 5;
if (x > 0) {
    int y = 10;  // scope local
}
y = 15;  // ERROR: 'y' no existe fuera del if


Tabla de Símbolos(`SymbolTable.java`):

Estructura jerárquica de scopes:
global
├── func_suma_1
│   ├── a (parámetro)
│   ├── b (parámetro)
│   └── result (local)
└── main_2
    ├── x (local)
    └── y (local)

¿Por qué scopes anidados?
- Refleja la estructura de bloques del programa
- Permite búsqueda de símbolos en cascada
- Facilita la gestión de variables locales
- Soporta shadowing de variables

---

6. Generación de Código x86-64 (`X86AssemblyGenerator.java`)

¿Por qué x86-64?
- Arquitectura ampliamente utilizada
- Conjunto de instrucciones rico
- Buena documentación disponible
- Permite optimizaciones complejas

Convenciones de Llamada (System V AMD64 ABI)

Registros usados:
```
rax - Valor de retorno y operaciones aritméticas
rbx - Operando temporal
rcx - Contador (loops)
rdx - Operando para división
rbp - Frame pointer (base del stack frame)
rsp - Stack pointer (tope de la pila)
```

Stack Frame:
```
        ┌─────────────────┐ ← rbp anterior
        │  Return address │
        ├─────────────────┤ ← rbp actual
        │  Saved rbp      │
        ├─────────────────┤
        │  Local var 1    │ -8(%rbp)
        ├─────────────────┤
        │  Local var 2    │ -16(%rbp)
        ├─────────────────┤
        │  ...            │
        └─────────────────┘ ← rsp
```
Simulación de Ejecución

¿Por qué simular?
- Verificar corrección del código generado
- Trazar valores de variables y registros
- Depurar problemas en la generación

---------------------------------------------------------------------------------------------------

Decisiones de Diseño

1. Separación de Fases
- Modularidad y mantenibilidad
- Facilita testing individual
- Permite reutilización de componentes
- Sigue principios de compiladores clásicos

2. Patrón Visitor para AST
- Desacopla operaciones de estructura
- Un solo recorrido para múltiples análisis
- Fácil agregar nuevas operaciones
- Estándar en diseño de compiladores

3. Symbol Table Jerárquica
- Representa scopes anidados naturalmente
- Búsqueda eficiente con herencia
- Gestión automática de lifetimes
- Soporta shadowing correcto

4. Simulación Integrada
- Validación inmediata del código generado
- Herramienta educativa valiosa
- Debugging sin ensamblar/ejecutar
- Visualización del flujo de ejecución

5. Stack-Based Code Generation
- Simple y predecible
- Compatible con convenciones x86-64
- Fácil gestión de expresiones complejas
- Base sólida para optimizaciones futuras

6. Short-Circuit Evaluation
- Eficiencia en evaluación lógica
- Previene errores (ej: división por cero)
- Comportamiento estándar esperado
- Optimización común en compiladores

7. Tipos Limitados (int, bool, void)
- Simplicidad en implementación inicial
- Suficiente para demostrar conceptos
- Facilita análisis semántico
- Base para extensión futura

---

##Flujo de Datos Completo

```
ENTRADA
   ↓
[Lexer] → Tokens
   ↓
[Parser] → AST
   ↓
[SemanticAnalyzer] → AST Validado + Symbol Table
   ↓
[X86AssemblyGenerator] → Assembly Code + Simulation Trace
   ↓
SALIDA (.s)
```
---
Conceptos Clave Implementados
1.Compilación Multi-Fase: Separación clara de responsabilidades
2. Visitor Pattern: Recorrido flexible del AST
3. Symbol Table: Gestión de scopes y variables
4. Code Generation: Traducción a lenguaje de máquina
5. ABI Compliance: Convenciones de llamada System V
6. Short-Circuit: Evaluación eficiente de expresiones lógicas
7. Stack Management: Control preciso del stack frame
8. Simulation: Validación y visualización de ejecución
