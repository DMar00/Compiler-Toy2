package main.syntaxtree.visitor;

import main.exceptions.*;
import main.syntaxtree.enums.Type;
import main.syntaxtree.nodes.expr.unExpr.*;
import main.table.*;
import main.syntaxtree.nodes.*;
import main.syntaxtree.nodes.expr.*;
import main.syntaxtree.nodes.expr.binExpr.*;
import main.syntaxtree.nodes.expr.constNodes.*;
import main.syntaxtree.nodes.iter.*;
import main.syntaxtree.nodes.stat.*;
import main.typecheck.CompType;
import main.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SemanticVisitorReadTables implements Visitor {
    private SymbolTable activeSymbolTable;

    public SemanticVisitorReadTables(SymbolTable symbolTableRoot) {
        this.activeSymbolTable = symbolTableRoot;
    }

    private SymbolItem findInOtherScope(String idToFind){
        //copio tabella e ricerco su copia
        SymbolTable copyTableNode = activeSymbolTable.clone();

        //ciclo sugli scope fin quando non arrico alla radice
        boolean found = false;
        SymbolItem itemFound = new SymbolItem();
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

        if(!found){
            //TODO invece di "id" come faccio a dire se variabile, nome di func  etc...
            throw new IdNotDeclared("Id", idToFind);
        }

        return itemFound;
    }

    private void checkMarkerTrue(){
        //controllo marker dichiarazioni
        //potrebbero esserci dei VAR n^= 5; non dichiarati col tipo
        SymbolNode node = activeSymbolTable.getActiveTable();
        for (Map.Entry<String, SymbolItem> entry : node.entrySet()) {
            //controllo se ci sono marker = true
            String id = entry.getKey();
            SymbolItem values = entry.getValue();
            if(values.isMarker())   //se ci sono lancio eccezione
                throw new IdNotDeclared(values.getItemType().toString(),id);
        }
    }

    private Type visitBinaryExpr(BinaryExpr expr){
        expr.rightNode.accept(this);
        expr.leftNode.accept(this);

        //controllo compatibilità tipi nell'espressione binaria
        Type type = CompType.getTypeFromBinaryExpr(expr);
        if(type == null)
            throw new InvalidTypeForBinaryExpr(expr.name, expr.leftNode.getNodeType(), expr.rightNode.getNodeType());

        return type;
    }

    private Type visitUnaryExpr(UnaryExpr expr){
        expr.rightNode.accept(this);

        //controllo tipi nell'espressione
        Type type = CompType.getTypeFromUnaryExpr(expr);

        if(type == null)
            throw new InvalidTypeForUnaryExpr(expr.name, expr.rightNode.getNodeType());

        return type;
    }

    /*-------------------Interface methods---------------------*/
    @Override
    public Object visit(ProgramOp programOp) {
        //controllo marker dichiarazioni in tabella scope corrente
        checkMarkerTrue();

        //controllo che ci sia main
        SymbolNode node = activeSymbolTable.getActiveTable();
        boolean mainFound = false;
        for (Map.Entry<String, SymbolItem> entry : node.entrySet()) {
            String id = entry.getKey();
            SymbolItem values = entry.getValue();
            //TODO il main può avere parametri ? controlla specifica
            if(id.equals("main") && (values.getItemType() == SymbolItemType.PROCEDURE)){
                mainFound = true;
                break;
            }
        }
        if(!mainFound)
            throw new MainNotFound();

        //nello scope globale non c'è bisogno di fare controlli su assegnazioni etc perchè non si possono fare

        //controllo gli scope figli dello scope globale
        if(programOp.itersList!=null){
            for(IterOp i : programOp.itersList){
                //le dichiarazioni di variabili sono state già visitate in 1° visita
                if(!(i instanceof VarDeclOp))
                    i.accept(this);
            }
        }

        //TODO delete print
        System.out.println("Visita 2°\n"+activeSymbolTable);

        return null;
    }

    @Override
    public Object visit(ProcOp procOp) {
        String procName = procOp.procName.idName;

        //attivo tabella della proc con nome procName
        activeSymbolTable.enterSpecificScope(procName);

        //TODO paramtri procedura da controllare

        //controllo marker dichiarazioni in tabella scope corrente
        checkMarkerTrue();

        //visito body per controlli
        procOp.procBody.accept(this);

        //ritorno in scope padre
        activeSymbolTable.exitScope();
        return null;
    }

    @Override
    public Object visit(BodyOp bodyOp) {
        //controllo statements : AssignOp, ...
        for(Stat st: bodyOp.statList){
            st.accept(this);
        }

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

        //CONTROLLO tipi e numero di variabili/funCall per le assegnazioni
        //n1 ^= n6;
        //n1, ..., nn ^= s1, ...., sn
        //n1, ..., nn ^= s1, ..., func() ...   -->     n1, n2, n3 ^= s1, func()
        int numIds = assignOp.idList.size();
        int numExpr = assignOp.exprList.size();
        //se il numero di parametri a destra è superiore al numero di parametri a sinistra, eccezione
        if(numExpr > numIds){
            throw new MismatchedParameterCount();
        }else{
            List<Id> idsSX = assignOp.idList;
            List<Type> exprDX = new ArrayList<>();

            for(Expr e: assignOp.exprList){ //fun()->3,5    :   exprDX[int, int]
                if( e instanceof FunCallOp){
                    FunCallOp f = (FunCallOp) e;
                    String funName = f.funName.idName;
                    //trovo funName in tabella globale
                    SymbolTable copy = activeSymbolTable.clone();
                    copy.enterSpecificScope(Utils.rootNodeName);
                    SymbolItem funFound = copy.lookup(funName);
                    //prendo tipi di ritorno e li metto in exprDX
                    for(Type t: funFound.getReturnTypeList()){
                        exprDX.add(t);
                    }
                }else{  //n1 ^= 5+3-1;  ||  n1, ... ^= n4, ...;
                    Type t = e.getNodeType();
                    boolean isId = e instanceof Id;
                    System.out.println("E' un id? "+isId+"..:"+t);
                    exprDX.add(t);
                }
            }

            //se le size delle due liste coincidono, allora abbiamo un numero
            //di parametri giusto per l'assegnazione, quindi possiamo controllare i tipi
            if(idsSX.size() == exprDX.size()){
                for(int i = 0 ; i<idsSX.size(); i++){
                    //TODO l'assegnazione quindi è possibile solo tra tipi uguali ?
                    if(idsSX.get(i).getNodeType() != exprDX.get(i)){
                        System.out.println("id: "+idsSX.get(i).getNodeType()+", e: "+exprDX.get(i));
                        throw new MismatchedTypes(exprDX.get(i).toString(), idsSX.get(i).getNodeType().toString());
                    }

                }
            }else {
                throw new MismatchedParameterCount();
            }

        }
        return null;
    }

    @Override
    public Object visit(Id id) {
        //es ho n1 ^= n3;
        String idName = id.idName;

        //se nella tabella corrente non è stato dichiarato, quindi non è presente
        //controllo negli scope precedenti che sia dichiarato
        SymbolItem found;
        if(!activeSymbolTable.probe(idName)){
            found = findInOtherScope(idName);
            System.out.println(idName+": In altro scope");
        }else{
            found = activeSymbolTable.lookup(idName);
            System.out.println(idName+": In scope");
        }

        id.setNodeType(found.getVarType());

        return null;
    }

    /*--------*/

    @Override
    public Object visit(AddOp addOp) {
        Type t = visitBinaryExpr(addOp);
        addOp.setNodeType(t);
        return null;
    }

    @Override
    public Object visit(DiffOp diffOp) {
        Type t = visitBinaryExpr(diffOp);
        diffOp.setNodeType(t);
        return null;
    }

    @Override
    public Object visit(DivOp divOp) {
        Type t = visitBinaryExpr(divOp);
        divOp.setNodeType(t);
        return null;
    }

    @Override
    public Object visit(MulOp mulOp) {
        Type t = visitBinaryExpr(mulOp);
        mulOp.setNodeType(t);
        return null;
    }

    /*--------*/

    @Override
    public Object visit(IntConstNode constNode) {
        constNode.setNodeType(Type.INTEGER);
        return null;
    }

    @Override
    public Object visit(RealConstNode constNode) {
        constNode.setNodeType(Type.REAL);
        return null;
    }

    @Override
    public Object visit(StringConstNode constNode) {
        constNode.setNodeType(Type.STRING);
        return null;
    }

    @Override
    public Object visit(BoolConstNode constNode) {
        constNode.setNodeType(Type.BOOLEAN);
        return null;
    }

    /*--------*/

    @Override
    public Object visit(AndOp andOp) {
        Type t = visitBinaryExpr(andOp);
        andOp.setNodeType(t);
        return null;
    }

    @Override
    public Object visit(OrOp orOp) {
        Type t = visitBinaryExpr(orOp);
        orOp.setNodeType(t);
        return null;
    }

    /*--------*/

    @Override
    public Object visit(EqOp eqOp) {
        Type t = visitBinaryExpr(eqOp);
        eqOp.setNodeType(t);
        return null;
    }

    @Override
    public Object visit(GeOp geOp) {
        Type t = visitBinaryExpr(geOp);
        geOp.setNodeType(t);
        return null;
    }

    @Override
    public Object visit(GtOp gtOp) {
        Type t = visitBinaryExpr(gtOp);
        gtOp.setNodeType(t);
        return null;
    }

    @Override
    public Object visit(LeOp leOp) {
        Type t = visitBinaryExpr(leOp);
        leOp.setNodeType(t);
        return null;
    }

    @Override
    public Object visit(LtOp ltOp) {
        Type t = visitBinaryExpr(ltOp);
        ltOp.setNodeType(t);
        return null;
    }

    @Override
    public Object visit(NeOp neOp) {
        Type t = visitBinaryExpr(neOp);
        neOp.setNodeType(t);
        return null;
    }

    /*--------*/

    @Override
    public Object visit(MinusOp minusOp) {
        Type t = visitUnaryExpr(minusOp);
        minusOp.setNodeType(t);
        return null;
    }

    @Override
    public Object visit(NotOp notOp) {
        Type t = visitUnaryExpr(notOp);
        notOp.setNodeType(t);
        return null;
    }


    /*-------------------------------------------------*/



    /*-------------------------------------------------*/
    @Override
    public Object visit(VarDeclOp varDeclOp) {
        return null;
    }

    @Override
    public Object visit(FunDeclOp funDeclOp) {
        return null;
    }

    @Override
    public Object visit(ProcFunParamOp procFunParamOp) {
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
