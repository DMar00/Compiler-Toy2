package main.table;

import main.syntaxtree.enums.Type;
import main.syntaxtree.nodes.ProcFunParamOp;

import java.util.List;

//costituisce l'insieme dei valori presenti in una riga di una tabella
public class SymbolItem {
    private SymbolItemType itemType;    //function or variable
    private String id;
    //variables
    private Type varType;
    //functions
    private List<ProcFunParamOp> params;
    private Type returnType;
    private boolean hasReturnType;
    //Marker
    private boolean marker;

    //costruttore per variabili
    public SymbolItem(String id, Type varType) {
        this.id = id;
        this.varType = varType;
        this.itemType = SymbolItemType.VARIABLE;
        this.params = null;
        this.returnType = null;
        this.hasReturnType = false;
        this.marker = true;
    }

    //costruttore per funzioni
    public SymbolItem(String id, List<ProcFunParamOp> params, Type returnType) {
        this.id = id;
        this.itemType = SymbolItemType.FUNCTION;
        this.params = params;
        this.returnType = returnType;
        this.hasReturnType = false;
        this.varType = null;
        this.marker = true;
    }

    public SymbolItemType getItemType() {
        return itemType;
    }

    public void setItemType(SymbolItemType itemType) {
        this.itemType = itemType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Type getVarType() {
        return varType;
    }

    public void setVarType(Type varType) {
        this.varType = varType;
    }

    public List<ProcFunParamOp> getParams() {
        return params;
    }

    public void setParams(List<ProcFunParamOp> params) {
        this.params = params;
    }

    public Type getReturnType() {
        return returnType;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    public boolean isHasReturnType() {
        return hasReturnType;
    }

    public void setHasReturnType(boolean hasReturnType) {
        this.hasReturnType = hasReturnType;
    }

    public boolean isMarker() {
        return marker;
    }

    public void setMarker(boolean marker) {
        this.marker = marker;
    }
}
