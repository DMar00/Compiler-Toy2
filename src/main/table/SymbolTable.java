package main.table;

import main.table.SymbolItem;
import main.table.SymbolNode;

public class SymbolTable {
    private SymbolNode activeTable = null;

    /*getters*/
    public SymbolNode getActiveTable() {
        return activeTable;
    }

    /*enter scope methods*/
    public void enterScope(String nameScopeToGive){
        SymbolNode newNode = new SymbolNode(activeTable);
        newNode.setNameScope(nameScopeToGive);
        activeTable = newNode;
    }

    public void enterSpecificScope(String idScopeToFind){
        for(SymbolNode sn : activeTable.getChildrenScopes()){
            if(sn.getNameScope().equals(idScopeToFind))
                activeTable = sn;
        }
    }

    /*add children tables*/
    public void addChildToParentScope(SymbolNode child){
        SymbolNode parentNode = activeTable.getParent();
        parentNode.addChildScope(child);
    }

    /*exit scope methods*/
    public void exitScope(){
        if(activeTable != null && activeTable.getParent() != null) {
            activeTable = activeTable.getParent();
        }
    }

    public void exitScopeNull(){
        activeTable = activeTable.getParent();
    }

    /*lookup method*/
    public SymbolItem lookupOnlyActive(String idName) {
        SymbolNode currentLooking = activeTable;

        if(currentLooking.containsKey(idName))
            return currentLooking.get(idName);

        return null;
    }

    public SymbolItem lookup(String idName) {
        SymbolNode currentLooking = activeTable;
        //System.out.println("Scope corrente: "+ currentLooking.getNameScope());

        while(currentLooking != null) {
            //se trova il SymbolItem nella tabella corrente lo restituisce
            if(currentLooking.containsKey(idName)){
                //System.out.println("Trovato "+idName+" in: "+ currentLooking.getNameScope());
                //System.out.println("lookup() - id:"+idName+" - scope: "+currentLooking.getNameScope() + " - type:"+currentLooking.get(idName).getVarType());
                return currentLooking.get(idName);
            }
            //se non trova il SymbolItem nella tabella corrente la cerca nella tabella padre
            currentLooking = currentLooking.getParent();
        }

        return null;
    }


    /*probe method : check if current table contains the id "idName"*/
    public boolean probe(String idName) {
        return activeTable.containsKey(idName);
    }

    /*addId method: add a SymbolItem to current table*/
    public void addId(SymbolItem item) {
        //verifico se item è già presente nella tabella corrente
        //se non lo è inserisco l'item
        if(!activeTable.containsKey(item.getId())) {
            activeTable.put(item.getId(), item);
        }
    }


    /*toString methods*/
    @Override
    public String toString() {
        if (activeTable != null) {
            return printTableWithChildren(activeTable, 0);
        } else {
            return "No active table.";
        }
    }

    private String printTableWithChildren(SymbolNode table, int depth) {
        StringBuilder sb = new StringBuilder();

        // Print the current table
        sb.append(getIndentation(depth)).append(table.getNameScope()).append(" -->\n");
        for (String key : table.keySet()) {
            sb.append(getIndentation(depth + 1)).append(table.get(key).toString()).append("\n");
        }

        // Recursively print children tables
        for (SymbolNode child : table.getChildrenScopes()) {
            sb.append(printTableWithChildren(child, depth + 1));
        }

        return sb.toString();
    }

    private String getIndentation(int depth) {
        StringBuilder indentation = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            indentation.append("\t");
        }
        return indentation.toString();
    }

    /*clone*/
    @Override
    public SymbolTable clone(){
        SymbolTable symbolTable = new SymbolTable();
        symbolTable.activeTable = this.activeTable;
        return symbolTable;
    }
}
