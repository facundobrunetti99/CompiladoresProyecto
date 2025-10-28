package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.example.ast.ProgramNode;
import org.example.codegen.X86AssemblyGenerator;
import org.example.semantic.symboltable.SemanticAnalyzer;
import org.example.semantic.symboltable.SymbolTable;

import java_cup.Lexer;
import java_cup.parser;
import java_cup.runtime.Symbol;
import jflex.core.sym;

public class Main_extended {

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.err.println("Uso: java Main_extended <archivo_entrada> [opciones]");
                System.err.println("Opciones:");
                System.err.println("  -tokens    : Mostrar tokens");
                System.err.println("  -parse     : Solo parsing");
                System.err.println("  -tree      : Mostrar árbol AST");
                System.err.println("  -symbols   : Mostrar tabla de símbolos");
                System.err.println("  -asm       : Generar código Assembly x86-64");
                System.err.println("  -all       : Hacer todo");
                System.exit(1);
            }

            String inputFile = args[0];
            boolean showTokens = false;
            boolean showParsing = false;
            boolean showTree = false;
            boolean showSymbols = false;
            boolean generateAssembly = false;

            for (int i = 1; i < args.length; i++) {
                switch (args[i]) {
                    case "-tokens":
                        showTokens = true;
                        break;
                    case "-parse":
                        showParsing = true;
                        break;
                    case "-tree":
                        showTree = true;
                        break;
                    case "-symbols":
                        showSymbols = true;
                        break;
                    case "-asm":
                        generateAssembly = true;
                        break;
                    case "-all":
                        showTokens = true;
                        showParsing = true;
                        showTree = true;
                        showSymbols = true;
                        generateAssembly = true;
                        break;
                    default:
                        System.err.println("Opción desconocida: " + args[i]);
                        break;
                }
            }

            System.out.println("=".repeat(70));
            System.out.println("COMPILADOR CON TABLA DE SÍMBOLOS Y ESTRUCTURAS DE CONTROL");
            System.out.println("Archivo: " + inputFile);
            System.out.println("=".repeat(70));

            File file = new File(inputFile);
            if (!file.exists()) {
                System.err.println("Error: El archivo '" + inputFile + "' no existe");
                System.exit(1);
            }

            // Fase 1: ANÁLISIS LÉXICO
            System.out.println("\n" + "=".repeat(70));
            System.out.println("FASE 1: ANÁLISIS LÉXICO");
            System.out.println("=".repeat(70));

            FileReader fileReader = new FileReader(file);
            Lexer lexer = new Lexer(fileReader);

            if (showTokens) {
                System.out.println("\nTokens encontrados:");
                System.out.println("-".repeat(70));

                Symbol token;
                do {
                    token = lexer.next_token();
                    if (token.sym != sym.EOF) {
                        System.out.print(" ");
                    }
                } while (token.sym != sym.EOF);
                System.out.println("\n" + "-".repeat(70));

                // Reiniciar el lexer para el parser
                fileReader.close();
                fileReader = new FileReader(file);
                lexer = new Lexer(fileReader);
            }

            // Fase 2: ANÁLISIS SINTÁCTICO
            System.out.println("\n" + "=".repeat(70));
            System.out.println("FASE 2: ANÁLISIS SINTÁCTICO");
            System.out.println("=".repeat(70));

            parser p = new parser(lexer);
            Symbol parseResult = p.parse();
            ProgramNode ast = (ProgramNode) parseResult.value;

            if (showParsing) {
                System.out.println("\n✓ Parsing completado exitosamente");
                System.out.println("  AST generado: " + (ast != null ? "OK" : "NULL"));
            }

            if (ast == null) {
                System.err.println("\nError: No se pudo generar el AST");
                System.exit(1);
            }

            // Fase 3: ÁRBOL AST
            if (showTree) {
                System.out.println("\n" + "=".repeat(70));
                System.out.println("FASE 3: ÁRBOL DE SINTAXIS ABSTRACTA (AST)");
                System.out.println("=".repeat(70));

                ASTtreePrinter treePrinter = new ASTtreePrinter();
                String treeOutput = treePrinter.printTree(ast);
                System.out.println(treeOutput);
            }

            // Fase 4: ANÁLISIS SEMÁNTICO - TABLA DE SÍMBOLOS
            System.out.println("\n" + "=".repeat(70));
            System.out.println("FASE 4: ANÁLISIS SEMÁNTICO");
            System.out.println("=".repeat(70));

            SymbolTable symbolTable = new SymbolTable();
            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(symbolTable);

            System.out.println("\nEjecutando análisis semántico...");
            boolean semanticSuccess = semanticAnalyzer.analyze(ast);

            if (!semanticSuccess) {
                System.err.println("\n✗ Errores semánticos encontrados");
                System.err.println("La compilación no puede continuar");
                System.exit(1);
            }

            System.out.println("✓ Análisis semántico completado exitosamente");

            if (showSymbols) {
                System.out.println();
                symbolTable.printSymbolTable();
                symbolTable.printStatistics();
            }

            // Fase 5: GENERACIÓN DE CÓDIGO
            if (generateAssembly) {
                System.out.println("\n" + "=".repeat(70));
                System.out.println("FASE 5: GENERACIÓN DE CÓDIGO x86-64");
                System.out.println("=".repeat(70));

                X86AssemblyGenerator codeGen = new X86AssemblyGenerator(symbolTable);
                String assemblyCode = codeGen.generateCode(ast);

                System.out.println("\n" + assemblyCode);

                // Guardar en archivo
                String outputFile = inputFile.replace(".txt", ".s");
                try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
                    writer.println(assemblyCode);
                    System.out.println("\n✓ Código Assembly guardado en: " + outputFile);
                } catch (IOException e) {
                    System.err.println("Error al guardar el archivo: " + e.getMessage());
                }
            }

            // Resumen final
            System.out.println("\n" + "=".repeat(70));
            System.out.println("COMPILACIÓN EXITOSA");
            System.out.println("=".repeat(70));
            System.out.println("Todas las fases completadas correctamente");
            System.out.println("=".repeat(70));

            fileReader.close();

        } catch (FileNotFoundException e) {
            System.err.println("Error: Archivo no encontrado - " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("\nError durante la compilación:");
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
