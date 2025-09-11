#!/bin/bash

# build.sh - Script para compilar el proyecto completo

echo "========================================="
echo "COMPILANDO PROYECTO EXTENDIDO"
echo "========================================="

# 1. Generar el scanner con JFlex
echo "1. Generando scanner con JFlex..."
java -jar jflex-1.8.2.jar ycalc_extended.flex
if [ $? -ne 0 ]; then
    echo "Error generando el scanner"
    exit 1
fi
echo "✓ Scanner generado"

# 2. Generar el parser con CUP
echo "2. Generando parser con CUP..."
java -jar java-cup-11b.jar ycalc_extended.cup
if [ $? -ne 0 ]; then
    echo "Error generando el parser"
    exit 1
fi
echo "✓ Parser generado"

# 3. Crear directorios necesarios
mkdir -p org/example/ast
mkdir -p org/example/interpreter
mkdir -p org/example/codegen
mkdir -p org/example/cup

# 4. Mover archivos generados
mv Lexer.java org/example/cup/
mv parser.java org/example/cup/
mv sym.java org/example/cup/

# 5. Compilar todos los archivos Java
echo "3. Compilando archivos Java..."
javac -cp ".:java-cup-11b.jar:jflex-1.8.2.jar" org/example/ast/*.java
javac -cp ".:java-cup-11b.jar:jflex-1.8.2.jar" org/example/interpreter/*.java
javac -cp ".:java-cup-11b.jar:jflex-1.8.2.jar" org/example/codegen/*.java
javac -cp ".:java-cup-11b.jar:jflex-1.8.2.jar" org/example/cup/*.java
javac -cp ".:java-cup-11b.jar:jflex-1.8.2.jar" org/example/*.java

if [ $? -ne 0 ]; then
    echo "Error compilando archivos Java"
    exit 1
fi
echo "✓ Compilación completada"

echo "========================================="
echo "PROYECTO COMPILADO EXITOSAMENTE"
echo "========================================="
echo ""
echo "Para ejecutar:"
echo "java -cp \".:java-cup-11b.jar:jflex-1.8.2.jar\" org.example.Main_extended <archivo> [opción]"
echo ""
echo "Opciones:"
echo "  -ast     : Mostrar solo AST"
echo "  -interp  : Ejecutar solo intérprete"
echo "  -asm     : Generar solo assembly"
echo "  -all     : Mostrar todo (por defecto)"

---

#!/bin/bash

# test.sh - Script para probar el compilador con diferentes programas

echo "========================================="
echo "PROBANDO COMPILADOR CON PROGRAMAS DE PRUEBA"
echo "========================================="

# Función para ejecutar una prueba
run_test() {
    local test_file=$1
    local description=$2

    echo ""
    echo "--- PRUEBA: $description ---"
    echo "Archivo: $test_file"
    echo ""

    java -cp ".:java-cup-11b.jar:jflex-1.8.2.jar" org.example.Main_extended $test_file

    if [ $? -ne 0 ]; then
        echo "❌ Error en la prueba: $test_file"
        return 1
    else
        echo "✅ Prueba exitosa: $test_file"
        return 0
    fi
}

# Crear archivos de prueba
echo "Creando archivos de prueba..."

# Test 1
cat > test1.txt << 'EOF'
void main(){
    int x;
    int y;
    x = 1;
    y = 1;
    x = x + 3 * 2 * y;
}
EOF

# Test 2
cat > test2.txt << 'EOF'
int main(){
    int x;
    int y;
    x = 1;
    y = 2;
    x = x + 3 * 2 * y;
    return x;
}
EOF

# Test 3
cat > test3.txt << 'EOF'
bool main(){
    int x;
    bool flag;
    x = 5;
    flag = true;
    x = x + 1;
    return flag;
}
EOF

# Test 4
cat > test4.txt << 'EOF'
int main(){
    int a;
    int b;
    int c;
    a = 10;
    b = 5;
    c = a * b + (a - b) / 2;
    return c;
}
EOF

echo "✓ Archivos de prueba creados"

# Ejecutar pruebas
run_test "test1.txt" "Programa void con variables enteras"
run_test "test2.txt" "Programa int con retorno"
run_test "test3.txt" "Programa bool con variables mixtas"
run_test "test4.txt" "Programa con operaciones complejas"

echo ""
echo "========================================="
echo "PRUEBAS COMPLETADAS"
echo "========================================="

---

# Makefile - Para sistemas que prefieren Make

.PHONY: all clean scanner parser compile test

# Variables
JFLEX_JAR = jflex-1.8.2.jar
CUP_JAR = java-cup-11b.jar
CLASSPATH = .:$(CUP_JAR):$(JFLEX_JAR)

all: compile

scanner:
	@echo "Generando scanner..."
	java -jar $(JFLEX_JAR) ycalc_extended.flex

parser: scanner
	@echo "Generando parser..."
	java -jar $(CUP_JAR) ycalc_extended.cup

setup: parser
	@echo "Creando estructura de directorios..."
	mkdir -p org/example/ast
	mkdir -p org/example/interpreter
	mkdir -p org/example/codegen
	mkdir -p org/example/cup
	mv Lexer.java org/example/cup/
	mv parser.java org/example/cup/
	mv sym.java org/example/cup/

compile: setup
	@echo "Compilando archivos Java..."
	javac -cp "$(CLASSPATH)" org/example/ast/*.java
	javac -cp "$(CLASSPATH)" org/example/interpreter/*.java
	javac -cp "$(CLASSPATH)" org/example/codegen/*.java
	javac -cp "$(CLASSPATH)" org/example/cup/*.java
	javac -cp "$(CLASSPATH)" org/example/*.java
	@echo "✓ Compilación completada"

test: compile
	@echo "Ejecutando pruebas..."
	./test.sh

run: compile
	@echo "Uso: make run FILE=archivo.txt [OPTION=-all]"
	java -cp "$(CLASSPATH)" org.example.Main_extended $(FILE) $(OPTION)

clean:
	@echo "Limpiando archivos generados..."
	rm -rf org/
	rm -f *.class
	rm -f Lexer.java parser.java sym.java
	rm -f test*.txt
	rm -f *.asm
	@echo "✓ Limpieza completada"