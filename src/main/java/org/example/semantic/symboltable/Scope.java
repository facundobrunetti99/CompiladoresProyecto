package org.example.semantic.symboltable;

import java.util.*;


public class Scope {
    private String scopeName;
    private Scope parent;
    private Map<String, SymbolEntry> symbols;
    private List<Scope> children;
    private int level;

    public Scope(String scopeName, Scope parent) {
        this.scopeName = scopeName;
        this.parent = parent;
        this.symbols = new LinkedHashMap<>();
        this.children = new ArrayList<>();
        this.level = (parent == null) ? 0 : parent.level + 1;

        if (parent != null) {
            parent.addChild(this);
        }
    }

   
    public boolean declare(String name, String type, int line, int column) {
        if (symbols.containsKey(name)) {
            return false; // Ya existe
        }
        symbols.put(name, new SymbolEntry(name, type, line, column));
        return true;
    }

  
    public boolean declare(String name, String type, Object value, int line, int column) {
        if (symbols.containsKey(name)) {
            return false;
        }
        symbols.put(name, new SymbolEntry(name, type, value, line, column));
        return true;
    }

  
    public SymbolEntry lookup(String name) {
        return symbols.get(name);
    }

   
    public boolean contains(String name) {
        return symbols.containsKey(name);
    }

   
    public void addChild(Scope child) {
        children.add(child);
    }

   
    public String getScopeName() { return scopeName; }
    public Scope getParent() { return parent; }
    public int getLevel() { return level; }
    public Collection<SymbolEntry> getSymbols() { return symbols.values(); }
    public List<Scope> getChildren() { return children; }
    public int getSymbolCount() { return symbols.size(); }

    @Override
    public String toString() {
        return String.format("Scope[%s] (level=%d, symbols=%d)",
                scopeName, level, symbols.size());
    }
}