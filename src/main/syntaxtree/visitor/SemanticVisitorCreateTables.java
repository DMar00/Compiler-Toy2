package main.syntaxtree.visitor;

import main.exceptions.IdAlreadyDeclared;
import main.exceptions.IdAlreadyDeclaredOtherType;
import main.table.SymbolTable;
import main.utils.Utils;
import main.syntaxtree.enums.Type;
import main.syntaxtree.nodes.BodyOp;
import main.syntaxtree.nodes.ProcFunParamOp;
import main.syntaxtree.nodes.ProgramOp;
import main.syntaxtree.nodes.expr.*;
import main.syntaxtree.nodes.expr.binExpr.*;
import main.syntaxtree.nodes.expr.constNodes.*;
import main.syntaxtree.nodes.iter.FunDeclOp;
import main.syntaxtree.nodes.iter.IterOp;
import main.syntaxtree.nodes.iter.ProcOp;
import main.syntaxtree.nodes.iter.VarDeclOp;
import main.syntaxtree.nodes.stat.*;
import main.table.SymbolItem;
import main.table.SymbolItemType;
import java.util.Map;


public class SemanticVisitorCreateTables implements Visitor {
    private SymbolTable activeSymbolTable;

    public SemanticVisitorCreateTables() {
        activeSymbolTable = new SymbolTable();
    }

    public SymbolTable getActiveSymbolTable() {
        return activeSymbolTable;
    }

    public void checkIdAlreadyDeclared(String idToFind, SymbolItemType type){
        SymbolItemType itemTypeFound = activeSymbolTable.lookup(idToFind).getItemType();
        if(itemTypeFound.name() != type.name())
            throw new IdAlreadyDeclaredOtherType(type.toString(), idToFind, itemTypeFound.toString());
        else throw new IdAlreadyDeclared(type.toString(), idToFind);
    }

    /*-------------------Interface methods---------------------*/
    @Override
    public Object visit(ProgramOp programOp) {
        //entriamo nello scope globale quindi attiviamo tabella per quello scope
        activeSymbolTable.enterScope(Utils.rootNodeName);

        //verifico se l'iterList (lista di variabili, funzioni o procedure) in ProgramOp è non vuota
        if(programOp.itersList!=null){
            for(IterOp i : programOp.itersList){
                i.accept(this);
            }
        }

        //TODO delete print
        System.out.println("Visita 1°\n"+activeSymbolTable);

        return null;
    }

    @Override
    public Object visit(VarDeclOp varDeclOp) {
        Type varType = varDeclOp.type;
        for (Map.Entry<Id, ConstNode> entry : varDeclOp.ids.entrySet()) {
            Id idName = entry.getKey();
            String idString = idName.idName;    //var name
            ConstNode constNode = entry.getValue(); //var value
            //Controllo se la variabile è già presente nella tabella corrente
            if(!activeSymbolTable.probe(idString)){
                //se la variabile non è presente, quindi non ci sono usi/decl precedenti,
                // la dichiaro con il marker a false
                SymbolItem item = new SymbolItem();
                item.setId(idString);
                item.setItemType(SymbolItemType.VARIABLE);
                //VAR ... n1,...: integer   --> inserisco il tipo e il marker a false
                if(varType != null){
                    item.setVarType(varType);
                    item.setMarker(false);
                }
                //VAR ... n1, .... ^=5
                if(constNode != null){
                    Type constType = Utils.constToType(constNode);  //prendo il tipo specifico dalla superclasse ConstNode
                    item.setVarType(constType);
                    //TODO VAR n1^=5; la aggiungo alla tab?     per ora si
                    item.setMarker(true);   //ancora non è stata trovata dichiarazione con tipo
                }
                activeSymbolTable.addId(item);
            }else{
                //se invece è già presente, lo prendo dalla tabella
                SymbolItem item = activeSymbolTable.lookup(idString);

                //se il marker = true, la variabile è già stata usata (VAR n^=5) ma non dichiarata in precedenza
                //quindi metto il marker a false perchè ora l'ho dichiarata
                if((item.getItemType()== SymbolItemType.VARIABLE) && item.isMarker())
                    item.setMarker(false);
                else
                    checkIdAlreadyDeclared(idString, SymbolItemType.VARIABLE);
            }
        }
        return null;
    }

    @Override
    public Object visit(ProcOp procOp) {
        Id procId = procOp.procName;
        String procedureName = procId.idName;

        //controllo se nella tabella globale è presente una procedura o altro con lo stesso nome
        if(activeSymbolTable.probe(procedureName)){
            checkIdAlreadyDeclared(procedureName, SymbolItemType.PROCEDURE);
        }

        //aggiungo la procedura alla tabella dei simboli
        // globale + setto marker false in quanto sto definendo procedura
        SymbolItem item = new SymbolItem(procedureName, procOp.procParamsList);
        item.setMarker(false);
        activeSymbolTable.addId(item);

        //creo la tabella dei simboli per questa procedura
        activeSymbolTable.enterScope(procedureName);

        //aggiungo come nodo figlio la nuova tabella procedure al padre
        activeSymbolTable.addChildToParentScope(activeSymbolTable.getActiveTable());

        //visitiamo il corpo della procedura per riempire la tabella della procedura
        procOp.procBody.accept(this);

        //usciamo dalla tabella della procedura e torniamo al padre (tab globale)
        activeSymbolTable.exitScope();

        return null;
    }

    @Override
    public Object visit(BodyOp bodyOp) {
        for(VarDeclOp var: bodyOp.varDeclOpList){
            var.accept(this);
        }
        for(Stat st: bodyOp.statList){
            st.accept(this);
        }
        return null;
    }

    /*---------------------------------------------------------*/

    @Override
    public Object visit(Id id) {
        return null;
    }

    @Override
    public Object visit(FunDeclOp funDeclOp) {
        return null;
    }

    @Override
    public Object visit(IntConstNode constNode) {
        return null;
    }

    @Override
    public Object visit(RealConstNode constNode) {
        return null;
    }

    @Override
    public Object visit(StringConstNode constNode) {
        return null;
    }

    @Override
    public Object visit(BoolConstNode constNode) {
        return null;
    }

    @Override
    public Object visit(ProcFunParamOp procFunParamOp) {
        return null;
    }

    @Override
    public Object visit(AssignOp assignOp) {
        return null;
    }

    @Override
    public Object visit(MinusOp minusOp) {
        return null;
    }

    @Override
    public Object visit(NotOp notOp) {
        return null;
    }

    @Override
    public Object visit(ProcExpr procExpr) {
        return null;
    }

    @Override
    public Object visit(FunCallOp funCallOp) {
        return null;
    }

    @Override
    public Object visit(AddOp addOp) {
        return null;
    }

    @Override
    public Object visit(AndOp andOp) {
        return null;
    }

    @Override
    public Object visit(DiffOp diffOp) {
        return null;
    }

    @Override
    public Object visit(DivOp divOp) {
        return null;
    }

    @Override
    public Object visit(EqOp eqOp) {
        return null;
    }

    @Override
    public Object visit(GeOp geOp) {
        return null;
    }

    @Override
    public Object visit(GtOp gtOp) {
        return null;
    }

    @Override
    public Object visit(LeOp leOp) {
        return null;
    }

    @Override
    public Object visit(LtOp ltOp) {
        return null;
    }

    @Override
    public Object visit(MulOp mulOp) {
        return null;
    }

    @Override
    public Object visit(NeOp neOp) {
        return null;
    }

    @Override
    public Object visit(OrOp orOp) {
        return null;
    }

    @Override
    public Object visit(BracketsOp bracketsOp) {
        return null;
    }

    @Override
    public Object visit(ProcCallOp procCallOp) {
        return null;
    }

    @Override
    public Object visit(ReturnOp returnOp) {
        return null;
    }

    @Override
    public Object visit(ElifOp elifOp) {
        return null;
    }

    @Override
    public Object visit(IfOp ifOp) {
        return null;
    }

    @Override
    public Object visit(WhileOp whileOp) {
        return null;
    }

    @Override
    public Object visit(IOArgsOp ioArgsOp) {
        return null;
    }

    @Override
    public Object visit(ConstNode constNode) {
        return null;
    }
}
