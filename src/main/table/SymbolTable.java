package main.table;

import main.syntaxtree.nodes.expr.Id;
import main.syntaxtree.nodes.expr.constNodes.ConstNode;

import java.util.Map;

public class SymbolTable {
    private SymbolNode activeTable = null;

    public void enterScope(){
        activeTable = new SymbolNode(activeTable);
    }

    public void exitScope(){
        if(activeTable != null && activeTable.getParent() != null) {
            activeTable = activeTable.getParent();
        }
    }

    public void exitScopeNull(){
            activeTable = activeTable.getParent();
    }

    public SymbolItem lookup(String idName) {
        SymbolNode currentLooking = activeTable;

        while(currentLooking != null) {
            //se trova il SymbolItem nella tabella corrente lo restituisce
            if(currentLooking.containsKey(idName))
                return currentLooking.get(idName);
            //se non trova il SymbolItem nella tabella corrente la cerca nella tabella padre
            currentLooking = currentLooking.getParent();
        }

        return null;
    }

    //verifica se la tabella corrente contiene o meno l'id "idName"
    public boolean probe(String idName) {
        return activeTable.containsKey(idName);
    }

    //aggiunge un Item alla tabella corrente
    public void addId(SymbolItem item) {
        //verifico se item è già presente nella tabella corrente
        //se non lo è inserisco l'item
        if(!activeTable.containsKey(item.getId())) {
            activeTable.put(item.getId(), item);
        }
    }

    public SymbolNode getActiveTable() {
        return activeTable;
    }

    public void setActiveTable(SymbolNode activeTable) {
        this.activeTable = activeTable;
    }

    @Override
    public String toString() {
        if (activeTable != null) {
            return activeTable.toString();
        } else {
            return "No active table.";
        }
    }

}
