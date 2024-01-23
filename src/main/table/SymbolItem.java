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
    private List<Type> returnTypeList;
    private boolean hasReturnType;
    //Marker
    private boolean marker;

    public SymbolItem() {
        this.marker = true;
    }

    //costruttore per variabili
    public SymbolItem(String id, Type varType) {
        this.id = id;
        this.varType = varType;
        this.itemType = SymbolItemType.VARIABLE;
        this.params = null;
        this.returnTypeList = null;
        this.hasReturnType = false;
        this.marker = true;
    }

    //costruttore per funzioni
    public SymbolItem(String id, List<ProcFunParamOp> params, List<Type> returnTypeList) {
        this.id = id;
        this.itemType = SymbolItemType.FUNCTION;
        this.params = params;
        this.returnTypeList = returnTypeList;
        this.hasReturnType = false;
        this.varType = null;
        this.marker = true;
    }

    //costruttore per funzioni
    public SymbolItem(String id, List<ProcFunParamOp> params) {
        this.id = id;
        this.itemType = SymbolItemType.PROCEDURE;
        this.params = params;
        this.returnTypeList = null;
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

    public List<Type> getReturnTypeList() {
        return returnTypeList;
    }

    public void setReturnTypeList(List<Type> returnTypeList) {
        this.returnTypeList = returnTypeList;
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

    @Override
    public String toString() {
        return "[id: "+id+", type: "+itemType+", varType: "+varType+", marker: "+marker+"]";
    }
}
