package main.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SymbolNode extends HashMap<String, SymbolItem> {
    private SymbolNode parentScope;
    private List<SymbolNode> childrenScopes;
    private String nameScope;
    public SymbolNode(SymbolNode parent) {
        super();
        parentScope= parent;
        childrenScopes = new ArrayList<>();
    }

    public List<SymbolNode> getChildrenScopes() {
        return childrenScopes;
    }

    public void addChildScope(SymbolNode child) {
        this.childrenScopes.add(child);
    }

    public String getNameScope() {
        return nameScope;
    }

    public void setNameScope(String nameScope) {
        this.nameScope = nameScope;
    }

    public SymbolNode getParent() {
        return parentScope;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("\n");
        for (String key : keySet()) {
            sb.append(String.format("%s\n", get(key).toString()));
        }

        return sb.toString();
    }
}
