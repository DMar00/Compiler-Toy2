package main.syntaxtree.visitor.semanticVisitor;

import main.exceptions.IdAlreadyDeclared;
import main.exceptions.IdAlreadyDeclaredOtherType;
import main.exceptions.IdNotDeclared;
import main.exceptions.func.MultipleValuesException;
import main.syntaxtree.nodes.expr.FunCallOp;
import main.table.SymbolItem;
import main.table.SymbolItemType;
import main.table.SymbolTable;
import main.utils.Utils;

public abstract class SemanticVisitorAbstract {
    protected SymbolTable activeSymbolTable;
    public SymbolTable getActiveSymbolTable() {
        return activeSymbolTable;
    }

    protected void checkIdAlreadyDeclaredActiveScope(String idToFind, SymbolItemType type){
        SymbolItem itemFound = activeSymbolTable.lookupOnlyActive(idToFind);
        //System.out.println("Check found : "+activeSymbolTable.getActiveTable().getNameScope()+" - idToFind: "+idToFind+" - found: "+itemFound);

        if(itemFound != null){
            SymbolItemType itemTypeFound = itemFound.getItemType();
            if(!itemTypeFound.name().equals(type.name()))
                throw new IdAlreadyDeclaredOtherType(type.toString(), idToFind, itemTypeFound.toString());
            else throw new IdAlreadyDeclared(type.toString(), idToFind);
        }
    }

    protected SymbolItem findInSpecificScope(String scopeName, String idToFind){
        SymbolTable copy = activeSymbolTable.clone();
        copy.enterSpecificScope(scopeName);
        SymbolItem found = copy.lookup(idToFind);
        return found;
    }

    protected SymbolItem findInParentsScopes(String idToFind){
        System.out.println("Id to find : "+ idToFind + " in scope : " + activeSymbolTable.getActiveTable().getNameScope());

        //SymbolItem itemFound = activeSymbolTable.lookupOnlyActive(idToFind);
        SymbolItem itemFound = activeSymbolTable.lookupOnlyActive(idToFind);
        //System.out.println("fond: "+itemFound.getId()+" - scope: "+activeSymbolTable.getActiveTable().getNameScope()+" - type:"+itemFound.getVarType());

        boolean found;

        if(itemFound != null){
            found = true;
        }else{
            found = false;
            //copio tabella e ricerco su copia
            SymbolTable copyTableNode = activeSymbolTable.clone();

            //ciclo sugli scope fin quando non arrivo alla radice
            while (copyTableNode.getActiveTable().getParent()!=null){
                //controllo in scope padre
                copyTableNode.exitScopeNull();
                //se trovo metto found = true ed esco
                if(copyTableNode.probe(idToFind)){
                    found = true;
                    itemFound = copyTableNode.lookup(idToFind);
                    break;
                }
            }
        }

        if(!found){
            throw new IdNotDeclared("Id", idToFind);
        }

        return itemFound;
    }

    private int whileCount = 0, ifCount = 0, elifCount = 0, elseCount=0;

    protected String setProgressiveName(String basicName){
        if(basicName.equals("while")){
            whileCount++;
            return basicName+"_"+whileCount;
        } else if (basicName.equals("if")) {
            ifCount++;
            return basicName+"_"+ifCount;
        } else if (basicName.equals("elif")) {
            elifCount++;
            return basicName+"_"+elifCount;
        } else if (basicName.equals("else")) {
            elseCount++;
            return basicName+"_"+elseCount;
        }
        return null;
    }

    protected void resetVariablesCount(){
        whileCount = 0;
        ifCount = 0;
        elifCount = 0;
        elseCount = 0;
    }

    protected void checkIfFunctionReturnMoreValue(FunCallOp f){
        SymbolItem s = findInSpecificScope(Utils.rootNodeName, f.funName.idName);
        int retTypes = s.getReturnTypeList().size();
        if(retTypes>1) {
            throw new MultipleValuesException(f.funName.idName);
        }
    }
}
