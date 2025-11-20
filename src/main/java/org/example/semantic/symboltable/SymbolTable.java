package org.example.semantic.symboltable;
import org.example.semantic.symboltable.Scope;
import org.example.semantic.symboltable.SymbolEntry;
import java.util.List;

public class SymbolTable {

    private Scope globalScope;
    private Scope currentScope;
    private int scopeCounter;
    private int currentStackOffset;

    public SymbolTable() {
        this.globalScope = new Scope("global", null);
        this.currentScope = globalScope;
        this.scopeCounter = 0;
        this.currentStackOffset = 0;
    }



    public void enterScope(String scopeName) {
        String fullName = scopeName + "_" + (++scopeCounter);
        currentScope = new Scope(fullName, currentScope);
    }

   
    public boolean exitScope() {
        if (currentScope.getParent() == null) {
            return false;
        }
        currentScope = currentScope.getParent();
        return true;
    }



    public boolean declare(String name, String type) {
        return currentScope.declare(name, type, -1, -1);
    }

   
    public boolean declare(String name, String type, int line, int column) {
        return currentScope.declare(name, type, line, column);
    }

    
    public boolean declare(String name, String type, Object value, int line, int column) {
        return currentScope.declare(name, type, value, line, column);
    }

  
    public boolean declareWithAddress(String name, String type, String address, int size, boolean isGlobal) {
        if (!currentScope.declare(name, type, -1, -1)) {
            return false;
        }

        SymbolEntry entry = currentScope.lookup(name);
        if (entry != null) {
            entry.setAddress(address);
            entry.setSize(size);
            entry.setGlobal(isGlobal);
            return true;
        }
        return false;
    }

   
    public boolean declareWithAddress(String name, String type, Object value,
            String address, int size, boolean isGlobal,
            int line, int column) {
        if (!currentScope.declare(name, type, value, line, column)) {
            return false;
        }

        SymbolEntry entry = currentScope.lookup(name);
        if (entry != null) {
            entry.setAddress(address);
            entry.setSize(size);
            entry.setGlobal(isGlobal);
            return true;
        }
        return false;
    }

    public SymbolEntry lookup(String name) {
        Scope scope = currentScope;
        while (scope != null) {
            SymbolEntry entry = scope.lookup(name);
            if (entry != null) {
                return entry;
            }
            scope = scope.getParent();
        }
        return null;
    }

    
    public SymbolEntry lookupLocal(String name) {
        return currentScope.lookup(name);
    }

    
    public boolean exists(String name) {
        return lookup(name) != null;
    }

  
    public boolean existsLocal(String name) {
        return lookupLocal(name) != null;
    }


    public boolean assign(String name, Object value) {
        SymbolEntry entry = lookup(name);
        if (entry != null) {
            entry.setValue(value);
            return true;
        }
        return false;
    }

   
    public Object getValue(String name) {
        SymbolEntry entry = lookup(name);
        return entry != null ? entry.getValue() : null;
    }

  
    public String getType(String name) {
        SymbolEntry entry = lookup(name);
        return entry != null ? entry.getType() : null;
    }

   
    public boolean isInitialized(String name) {
        SymbolEntry entry = lookup(name);
        return entry != null && entry.isInitialized();
    }


    public int allocateStackSpace(int size) {
        currentStackOffset += size;
        return currentStackOffset;
    }

   
    public void resetStackOffset() {
        currentStackOffset = 0;
    }

   
    public int getCurrentStackOffset() {
        return currentStackOffset;
    }


    public Scope getCurrentScope() {
        return currentScope;
    }

    public Scope getGlobalScope() {
        return globalScope;
    }

    public int getCurrentScopeLevel() {
        return currentScope.getLevel();
    }


   

  

   
  

    private int countSymbols(Scope scope) {
        int count = scope.getSymbolCount();
        for (Scope child : scope.getChildren()) {
            count += countSymbols(child);
        }
        return count;
    }

    private int countScopes(Scope scope) {
        int count = 1;
        for (Scope child : scope.getChildren()) {
            count += countScopes(child);
        }
        return count;
    }

   
    public void clear() {
        this.globalScope = new Scope("global", null);
        this.currentScope = globalScope;
        this.scopeCounter = 0;
        this.currentStackOffset = 0;
    }
}