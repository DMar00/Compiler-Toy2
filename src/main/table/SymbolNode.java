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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("-->\n");
        for (String key : keySet()) {
            sb.append(String.format("\t%s\n", get(key).toString()));
        }

        // Aggiungi le tabelle dei genitori in modo ricorsivo
        /*if (parentScope != null) {
            sb.append("\nParent Table:\n");
            sb.append(parentScope.toString());
        }*/

        return sb.toString();
    }

}
