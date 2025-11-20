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

import java_cup.runtime.Symbol;

public class Main_extended {

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.err.println("Uso: java Main_extended <archivo_entrada> [opciones]");
                System.err.println("Opciones:");
                System.err.println("  -tokens    : mostrar tokens");
                System.err.println("  -parse     : solo parsing");
                System.err.println("  -tree      : mostrar árbol AST");
                System.err.println("  -symbols   : mostrar tabla de símbolos");
                System.err.println("  -asm       : generar código Assembly x86-64");
                System.err.println("  -all       : hacer todo");
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
                        System.err.println("opcion desconocida: " + args[i]);
                        break;
                }
            }

            File file = new File(inputFile);
            if (!file.exists()) {
                System.err.println("error: el archivo '" + inputFile + "' no existe");
                System.exit(1);
            }

            FileReader fileReader = new FileReader(file);
            Lexer lexer = new Lexer(fileReader);

            if (showTokens) {

                Symbol token;
                do {
                    token = lexer.next_token();
                    if (token.sym != sym.EOF) {

                    }
                } while (token.sym != sym.EOF);

                fileReader.close();
                fileReader = new FileReader(file);
                lexer = new Lexer(fileReader);
            }

            parser p = new parser(lexer);
            Symbol parseResult = p.parse();
            ProgramNode ast = (ProgramNode) parseResult.value;

            if (showParsing) {
                System.out.println("\n" + "=".repeat(70));
                System.out.println("PARSING");
                System.out.println("=".repeat(70));
                System.out.println("parsing completado exitosamente");
                System.out.println("  ast generado: " + (ast != null ? "OK" : "NULL"));
                System.out.println("=".repeat(70));
            }

            if (ast == null) {
                System.err.println("\nerror: no se pudo generar el AST");
                System.exit(1);
            }

            SymbolTable symbolTable = new SymbolTable();
            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(symbolTable);
            boolean semanticSuccess = semanticAnalyzer.analyze(ast);

            if (!semanticSuccess) {
                System.err.println("\nerrores semanticos encontrados");
                System.err.println("la compilacion no puede continuar");
                System.exit(1);
            }

            if (generateAssembly) {
                X86AssemblyGenerator codeGen = new X86AssemblyGenerator(symbolTable);
                String assemblyCode = codeGen.generateCode(ast);

                String outputFile = inputFile.replace(".txt", ".s");
                try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
                    writer.println(assemblyCode);
                } catch (IOException e) {
                    System.err.println("error al guardar el archivo: " + e.getMessage());
                    System.exit(1);
                }
            }

            System.out.println("compilación exitosa: " + inputFile);

            fileReader.close();

        } catch (FileNotFoundException e) {
            System.err.println("error: archivo no encontrado - " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("\nerror durante la compilacion:");
            System.err.println("   " + e.getMessage());
            System.exit(1);
        }
    }
}