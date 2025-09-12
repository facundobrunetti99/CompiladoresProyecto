package org.example;
import org.example.ast.*;
import org.example.codegen.AssemblyGenerator;
import org.example.interpetrer.Interpreter;
import org.example.ASTtreePrinter; // ← Nueva importación
import org.example.Lexer;
import java.io.*;

public class Main_extended {

    public static void main(String[] args) {
        try {
            // Verificar argumentos
            if (args.length == 0) {
                System.err.println("Uso: java Main_extended <archivo_entrada> [opciones]");
                System.err.println("Opciones:");
                System.err.println("  -tokens    : Mostrar tokens");
                System.err.println("  -parse     : Solo parsing");
                System.err.println("  -tree      : Mostrar árbol AST en consola");
                System.err.println("  -interpret : Ejecutar interpretación");
                System.err.println("  -asm       : Generar código Assembly");
                System.err.println("  -all       : Hacer todo");
                System.exit(1);
            }

            String inputFile = args[0];
            boolean showTokens = false;
            boolean showParsing = false;
            boolean showTree = false;
            // ← Variable agregada
            boolean runInterpreter = false;
            boolean generateAssembly = false;

            // Procesar opciones
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

                    case "-interpret":
                        runInterpreter = true;
                        break;
                    case "-asm":
                        generateAssembly = true;
                        break;
                    case "-all":
                        showTokens = true;
                        showParsing = true;
                        showTree = true;

                        runInterpreter = true;
                        generateAssembly = true;
                        break;
                    default:
                        System.err.println("Opción desconocida: " + args[i]);
                        break;
                }
            }

            System.out.println("===========================================");
            System.out.println("Iniciando compilación de: " + inputFile);
            System.out.println("===========================================");

            // Verificar que el archivo existe
            File file = new File(inputFile);
            if (!file.exists()) {
                System.err.println("Error: El archivo '" + inputFile + "' no existe.");
                System.exit(1);
            }

            // Crear el lexer
            System.out.println("Creando analizador léxico...");
            FileReader fileReader = new FileReader(inputFile);
            Lexer lexer = new Lexer(fileReader);

            if (showTokens) {
                System.out.println("\n=== TOKENS ===");
                System.out.println("(Funcionalidad de tokens pendiente de implementar)");
            }

            // Crear el parser
            System.out.println("Creando analizador...");
            parser p = new parser(lexer);

            if (showParsing) {
                System.out.println("\n=== ANÁLISIS  ===");
            }

            // Ejecutar el parsing
            System.out.println("Ejecutando análisis ...");
            Object result = p.parse().value;

            System.out.println("Análisis completado exitosamente");

            // Mostrar resultado básico
            if (result != null) {
                System.out.println("Resultado del parsing: " + result.getClass().getSimpleName());
                if (showParsing) {
                    System.out.println("   Contenido: " + result.toString());
                }

                // Procesar el AST si es un ProgramNode
                if (result instanceof ProgramNode) {
                    ProgramNode program = (ProgramNode) result;

                    // MOSTRAR ÁRBOL AST EN CONSOLA
                    if (showTree) {
                        System.out.println("\n=== ÁRBOL ABSTRACTO (AST) ===");
                        try {
                            ASTtreePrinter treePrinter = new ASTtreePrinter();
                            String treeOutput = treePrinter.printTree(program);
                            System.out.println(treeOutput);
                        } catch (Exception e) {
                            System.err.println("Error al mostrar el árbol AST:");
                            e.printStackTrace();
                        }
                    }

                    // Ejecutar interpretación si se solicita
                    if (runInterpreter) {
                        System.out.println("\n=== INTERPRETACIÓN ===");
                        try {
                            Interpreter interpreter = new Interpreter();
                            Object interpretResult = interpreter.interpret(program);
                            System.out.println("Interpretación completada");
                            if (interpretResult != null) {
                                System.out.println("Valor de retorno del programa: " + interpretResult);
                            }
                        } catch (Exception e) {
                            System.err.println("Error durante la interpretación:");
                            e.printStackTrace();
                        }
                    }

                    // Generar código Assembly si se solicita
                  /*   if (generateAssembly) {
                        System.out.println("\nGENERACIÓN DE CÓDIGO ASSEMBLY ");
                        try {
                            AssemblyGenerator generator = new AssemblyGenerator();
                            String assemblyCode = generator.generateCode(program);
                            System.out.println("Código Assembly generado:");
                            System.out.println(assemblyCode);
                        } catch (Exception e) {
                            System.err.println("Error durante la generación de Assembly:");
                            e.printStackTrace();
                        }
                    } */
                } else {
                    System.err.println("El resultado del parsing no es un ProgramNode válido");
                }

            } else {
                System.out.println("Parsing completado (resultado null)");
            }

            System.out.println("\n===========================================");
            System.out.println("Compilación completada sin errores");
            System.out.println("===========================================");

        } catch (FileNotFoundException e) {
            System.err.println("===========================================");
            System.err.println("Error: No se pudo encontrar el archivo: " + args[0]);
            System.err.println("===========================================");
            System.exit(1);
        } catch (Exception e) {
            System.err.println("===========================================");
            System.err.println("Compilación falló con error:");
            System.err.println("===========================================");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
