package main.syntaxtree.visitor;

import main.exceptions.IdAlreadyDeclaredOtherType;
import main.exceptions.IdAlreadyDeclared;
import main.exceptions.IdNotDeclared;
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
import main.table.SymbolNode;
import main.table.SymbolTable;

import java.util.Map;

public class SemanticVisitor implements Visitor{
    private SymbolTable symbolTable;

    public SemanticVisitor(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public void checkIdAlreadyDeclared(String idToFind, SymbolItemType type){
        SymbolItemType itemTypeFound = symbolTable.lookup(idToFind).getItemType();
        if(itemTypeFound.name() != type.name())
            throw new IdAlreadyDeclaredOtherType(type.toString(), idToFind, itemTypeFound.toString());
        else throw new IdAlreadyDeclared(type.toString(), idToFind);
    }

    @Override
    public Object visit(ProgramOp programOp) {
        //entriamo nello scope globale quindi attiviamo tabella per quello scope
        symbolTable.enterScope();
        //verifico se l'iterList (lista di variabili, funzioni o procedure) in ProgramOp è non vuota
        if(programOp.itersList!=null){
            for(IterOp i : programOp.itersList){
                i.accept(this);
            }
        }

        System.out.println("Globale" + symbolTable);
        return null;
    }

    @Override
    public Object visit(VarDeclOp varDeclOp) {
        //var n, n1, ..... : integer;\
        //var n, n1, ..... : integer;     s, s1: string\
        Type varType = varDeclOp.type;
        for (Map.Entry<Id, ConstNode> entry : varDeclOp.ids.entrySet()) {
            Id idName = entry.getKey();
            String idString = idName.idName;
            //Controllo se la variabile è già presente nella tabella corrente
            if(!symbolTable.probe(idString)){
                //se la variabile non è presente, quindi non ci sono usi precedenti, la dichiaro con il marker a false
                SymbolItem item = new SymbolItem(idString, varType);
                item.setMarker(false);
                symbolTable.addId(item);
            }else{
                //se invece è già presente,
                SymbolItem item = symbolTable.lookup(idString);
                //se il marker = true, la variabile è già stata usata in precedenza ma non era dichiarata
                //quindi metto il marker a false perchè ora l'ho dichiarata

                /*if(item.isMarker())
                    item.setMarker(false);
                else{ //se il marker = false, significa che è stata già dichiarata una var con lo stesso id
                    //throw new IdAlreadyDeclared("Variable", idString);
                    checkIdAlreadyDeclared(idString, SymbolItemType.VARIABLE);
                }*/

                if((item.getItemType()==SymbolItemType.VARIABLE) && item.isMarker())
                    item.setMarker(false);
                else
                    checkIdAlreadyDeclared(idString, SymbolItemType.VARIABLE);

            }
            idName.accept(this);
        }
        return null;
    }

    @Override
    public Object visit(Id id) {
        if(!symbolTable.probe(id.idName)){
            SymbolItem item = new SymbolItem(id.idName, (Type) null);
            symbolTable.addId(item);
        }
        return null;
    }

    @Override
    public Object visit(ProcOp procOp) {
        //TODO visit id ????
        Id procName = procOp.procName;
        //controllo se nella tabella globale è presente una procedura o altro con lo stesso nome
        if(symbolTable.probe(procName.idName)){
            checkIdAlreadyDeclared(procName.idName, SymbolItemType.PROCEDURE);
            //SymbolItemType itemTypeFound = symbolTable.lookup(procName.idName).getItemType();
            /*if(itemTypeFound.name() != SymbolItemType.PROCEDURE.name())
                throw new IdAlreadyDeclaredOtherType("Procedure", procName.idName, itemTypeFound.name());
            else throw new IdAlreadyDeclared("Procedure", procName.idName);*/
        }
        //aggiungiamo la procedura alla tabella dei simboli globale
        SymbolItem item = new SymbolItem(procName.idName, procOp.procParamsList);
        symbolTable.addId(item);
        //creiamo la tabella dei simboli per questa procedura
        symbolTable.enterScope();
        //visitiamo il corpo della procedura per riempire la tabella della procedura
        procOp.procBody.accept(this);

        System.out.println("Procedura " + procName.idName +" "+ symbolTable);

        //usciamo dalla tabella della procedura e torniamo al padre
        symbolTable.exitScope();


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
        //


        /*SymbolTable current = symbolTable;
        boolean allMarkerFalse = false;
        SymbolItem symbolItemError = new SymbolItem();
        int i = 1;

        while(current!=null){
            ///System.out.println(i);
            for (Map.Entry<String, SymbolItem> entry : current.getActiveTable().entrySet()) {
                SymbolItem symbolItem = entry.getValue();
                boolean m = symbolItem.isMarker();
                if (m && i==1) {
                    //System.out.println(symbolItem);
                    symbolItemError = symbolItem;
                    current.exitScope();
                    break; // Esci subito dal ciclo for
                }
                if(symbolItem.getId().equals(symbolItemError.getId()))
                    System.out.println("Trovatooo");
                if(i > 1 && symbolItem.getId().equals(symbolItemError.getId())){
                    System.out.println(symbolItem);
                    entry.getValue().setMarker(false);
                    allMarkerFalse = true;
                    current = null;
                    break; // Esci subito dal ciclo for
                }

            }
            i++;
            if(current==null)
                break;
            /*if (current != null) {
                current.setActiveTable(current.getActiveTable().getParent());
            }*//*
        }
        if (!allMarkerFalse)
            throw new IdNotDeclared(symbolItemError.getItemType().toString(), symbolItemError.getId().toString());

        //symbolTable = current;*/

        SymbolItem symbolItemError = new SymbolItem();
        for (Map.Entry<String, SymbolItem> entry : symbolTable.getActiveTable().entrySet()) {
            SymbolItem symbolItem = entry.getValue();
            boolean m = symbolItem.isMarker();
            if (m) {
                symbolItemError = symbolItem;
                break;
            }
        }
        System.out.println("Trovato m=true su: "+symbolItemError);

        SymbolTable tableCopy = symbolTable;
        tableCopy.exitScope();
        boolean allMarkerFalse = false;
        while (tableCopy.getActiveTable() != null){
            System.out.println("ciclo");
            boolean found = tableCopy.probe(symbolItemError.getId());
            if(!found) {
                tableCopy.exitScopeNull();
                System.out.println("Non Trovatooo in scope: "+ tableCopy);
            }else {
                System.out.println("Trovatooo");
                allMarkerFalse = true;
                break;
            }
        }

        if (allMarkerFalse == false)
            throw new IdNotDeclared(symbolItemError.getItemType().toString(), symbolItemError.getId().toString());

        return null;
    }



    @Override
    public Object visit(AssignOp assignOp) {
        for (Id id : assignOp.idList){
            id.accept(this);
        }
        for (Expr e: assignOp.exprList){
            e.accept(this);
        }
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
