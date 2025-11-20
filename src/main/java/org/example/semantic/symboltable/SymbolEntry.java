package org.example.semantic.symboltable;

/**
 * Representa una entrada individual en la tabla de símbolos. Almacena toda la
 * información necesaria sobre un símbolo (variable, función, etc.)
 */
public class SymbolEntry {

    private String name;           // Nombre del símbolo
    private String type;           // Tipo (int, bool, void, etc.)
    private Object value;          // Valor actual (para interpretación)
    private boolean initialized;   // Si ha sido inicializada
    private int line;             // Línea de declaración
    private int column;           // Columna de declaración

    // Información para generación de código
    private String address;        // Dirección en memoria (ej: "-8(%rbp)")
    private int size;             // Tamaño en bytes
    private int stackOffset;      // Offset en el stack frame
    private boolean isGlobal;     // Si es variable global
    private boolean isParameter;  // Si es parámetro de función

    public SymbolEntry(String name, String type, int line, int column) {
        this.name = name;
        this.type = type;
        this.value = null;
        this.initialized = false;
        this.line = line;
        this.column = column;
        this.address = null;
        this.size = getDefaultSize(type);
        this.stackOffset = 0;
        this.isGlobal = false;
        this.isParameter = false;
    }

    public SymbolEntry(String name, String type, Object value, int line, int column) {
        this(name, type, line, column);
        this.value = value;
        this.initialized = (value != null);
    }

    private int getDefaultSize(String type) {
        switch (type) {
            case "int":
            case "bool":
                return 8; // x86-64 usa 8 bytes para alineación
            case "void":
                return 0;
            default:
                return 8;
        }
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getAddress() {
        return address;
    }

    public int getSize() {
        return size;
    }

    public int getStackOffset() {
        return stackOffset;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public boolean isParameter() {
        return isParameter;
    }

    // Setters
    public void setValue(Object value) {
        this.value = value;
        this.initialized = true;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setStackOffset(int offset) {
        this.stackOffset = offset;
    }

    public void setGlobal(boolean isGlobal) {
        this.isGlobal = isGlobal;
    }

    public void setParameter(boolean isParameter) {
        this.isParameter = isParameter;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-12s %-8s", name, type));

        if (address != null) {
            sb.append(String.format(" @%-12s", address));
        }

        if (initialized && value != null) {
            sb.append(String.format(" = %s", value));
        }

        if (isGlobal) {
            sb.append(" [GLOBAL]");
        }

        if (isParameter) {
            sb.append(" [PARAM]");
        }

        sb.append(String.format(" (line %d, col %d)", line, column));

        return sb.toString();
    }
}
