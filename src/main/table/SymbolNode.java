package main.table;

import java.util.HashMap;

//String = id
public class SymbolNode extends HashMap<String, SymbolItem> {
    private SymbolNode parentScope;

    public SymbolNode(SymbolNode parent) {
        super();
        parentScope= parent;
    }

    public SymbolNode getParent() {
        return parentScope;
    }
}
