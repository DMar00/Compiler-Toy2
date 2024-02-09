package main.syntaxtree.visitor.semanticVisitor;

import main.exceptions.IdAlreadyDeclared;
import main.exceptions.IdAlreadyDeclaredOtherType;
import main.exceptions.func.InvalidReturnCountException;
import main.exceptions.func.InvalidReturnValue;
import main.exceptions.func.MismatchedReturnCount;
import main.exceptions.proc.UnexpectedReturn;
import main.syntaxtree.enums.Mode;
import main.syntaxtree.enums.Type;
import main.syntaxtree.nodes.BodyOp;
import main.syntaxtree.nodes.Node;
import main.syntaxtree.nodes.ProcFunParamOp;
import main.syntaxtree.nodes.ProgramOp;
import main.syntaxtree.nodes.expr.FunCallOp;
import main.syntaxtree.nodes.expr.Id;
import main.syntaxtree.nodes.expr.ProcExpr;
import main.syntaxtree.nodes.expr.binExpr.*;
import main.syntaxtree.nodes.expr.constNodes.*;
import main.syntaxtree.nodes.expr.unExpr.MinusOp;
import main.syntaxtree.nodes.expr.unExpr.NotOp;
import main.syntaxtree.nodes.iter.FunDeclOp;
import main.syntaxtree.nodes.iter.IterOp;
import main.syntaxtree.nodes.iter.ProcOp;
import main.syntaxtree.nodes.iter.VarDeclOp;
import main.syntaxtree.nodes.stat.*;
import main.syntaxtree.visitor.Visitor;
import main.table.SymbolItem;
import main.table.SymbolItemType;
import main.table.SymbolNode;
import main.table.SymbolTable;
import main.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SemanticVisitorFirstVisit extends SemanticVisitorAbstract implements Visitor {

    //Serve per avere già una lista di funzioni con relativi tipi di ritorno
    //che ci servono nel CVisitor per gestire ad esempio i return multipli , etc..
    private HashMap<String, List<Type>> funcMap;

    public SemanticVisitorFirstVisit() {
        activeSymbolTable = new SymbolTable();
        funcMap = new HashMap<>();
    }

    public HashMap<String, List<Type>> getFuncMap() {
        return funcMap;
    }

    /*----------------------------------------------------------------------------------------*/
    @Override
    public Object visit(ProgramOp programOp) {
        //creo scope globale
        activeSymbolTable.enterScope(Utils.rootNodeName);

        //itero su iterList (lista di variabili, funzioni o procedure)
        if(programOp.itersList!=null){
            for(IterOp i : programOp.itersList){
                i.accept(this);
            }
        }

        //TODO delete print
        //System.out.println("Visita 1°\n"+activeSymbolTable);

        return null;
    }

    @Override
    public Object visit(Id id) {
        String idName = id.idName;
        checkIdAlreadyDeclaredActiveScope(idName, id.getIdItemType());

        return null;
    }

    @Override
    public Object visit(VarDeclOp varDeclOp) {
        Type varType = varDeclOp.type;

        for (Map.Entry<Id, ConstNode> entry : varDeclOp.ids.entrySet()) {
            Id id = entry.getKey();
            String idName = id.idName; //var name
            ConstNode constNode = entry.getValue(); //var value

            //visito id per controllare se già c'è
            id.setIdItemType(SymbolItemType.VARIABLE);
            id.accept(this);

            //creo SymbolItem
            SymbolItem item = new SymbolItem();
            item.setItemType(SymbolItemType.VARIABLE);
            item.setId(idName);

            //se la variabile è dichiarata con il tipo
            //var ... n1,...: integer
            if(varType != null){
                item.setVarType(varType);

                id.setNodeType(varType); //aggiungo tipo per ast
            }

            //se la variabile è dichiarata con assegnazione costante
            //var ... n1, .... ^=5
            if(constNode != null){
                Type constType = Utils.constToType(constNode);  //prendo il tipo specifico dalla superclasse ConstNode
                item.setVarType(constType);

                id.setNodeType(constType); //aggiungo tipo id per ast
                varDeclOp.type = constType; //aggiungo tipo vardeclop per ast
            }

            //aggiungo variabile a tabella simboli attuale
            activeSymbolTable.addId(item);

        }
        return null;
    }

    @Override
    public Object visit(ProcOp procOp) {
        Id procId = procOp.procName;
        String procedureName = procId.idName;

        //visito id per controllare se già c'è
        procId.setIdItemType(SymbolItemType.PROCEDURE);
        procId.accept(this);

        //aggiungo la procedura alla tabella dei simboli
        SymbolItem item = new SymbolItem(procedureName, procOp.procParamsList);
        activeSymbolTable.addId(item);

        //creo la tabella dei simboli per questa procedura
        activeSymbolTable.enterScope(procedureName);

        //eseguo controlli parametri
        if(procOp.procParamsList != null){
            for (ProcFunParamOp p : procOp.procParamsList){
                p.accept(this);
            }
        }

        //aggiungo come nodo figlio la nuova tabella procedure al padre
        activeSymbolTable.addChildToParentScope(activeSymbolTable.getActiveTable());

        //visitiamo il corpo della procedura per riempire la tabella della procedura
        //procOp.procBody.setFunProcName(procedureName); //mi serve per controllo su return ad esempio
        procOp.procBody.accept(this);

        //usciamo dalla tabella della procedura e torniamo al padre (tab globale)
        activeSymbolTable.exitScope();

        return null;
    }

    @Override
    public Object visit(ProcFunParamOp procFunParamOp) {
        Id idParam = procFunParamOp.id;
        String paramName = idParam.idName;

        //visito id per controllare se già c'è
        //quindi controllo se in tab simboli corrente già ci sono variabili con stesso id visitando id
        idParam.setIdItemType(SymbolItemType.VARIABLE);
        idParam.accept(this);

        Type paramType = procFunParamOp.type;

        //aggiungo parametro come variabile in tabella simboli attuale
        SymbolItem symbolItem = new SymbolItem(paramName, paramType);
        if(procFunParamOp.mode != Mode.OUT)
            symbolItem.setParamOUT(false);

        activeSymbolTable.addId(symbolItem);

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

    @Override
    public Object visit(FunDeclOp funDeclOp) {
        Id funcId = funDeclOp.functionName;
        String funcName = funcId.idName;
        List<ProcFunParamOp> funParameters = funDeclOp.functionParamList;
        List<Type> funReturnTypes = funDeclOp.functionReturTypeList;
        funcMap.put(funcName, funReturnTypes);  //RICORDA : serve per il CVisitor


        //verifico che non ci sia altro definito con lo stesso nome
        /*if(activeSymbolTable.probe(funcName)){
            checkIdAlreadyDeclaredActiveScope(funcName, SymbolItemType.FUNCTION);
        }*/
        funcId.setIdItemType(SymbolItemType.PROCEDURE);
        funcId.accept(this);

        //aggiungo la funzione alla tabella dei simboli globale
        SymbolItem item = new SymbolItem(funcName, funParameters, funReturnTypes);
        activeSymbolTable.addId(item);

        //creo la tabella dei simboli per questa funzione
        activeSymbolTable.enterScope(funcName);

        //eseguo controlli parametri
        if(funParameters != null){
            for (ProcFunParamOp p : funParameters){
                p.accept(this);
            }
        }

        //controllo che i tipi di ritorno siano >= 1
        if(!(funReturnTypes.size()>=1))
            throw new InvalidReturnValue("");


        //aggiungo come nodo figlio la nuova tabella funzione al padre
        activeSymbolTable.addChildToParentScope(activeSymbolTable.getActiveTable());

        //visitiamo il corpo della funzione per riempire la tabella della funzione
        //funDeclOp.functionBody.setFunProcName(funcName);    //mi serve per controllo su return ad esempio
        funDeclOp.functionBody.accept(this);

        //controllo che ci sia il return
        /*int nReturns = funDeclOp.functionBody.getReturnCount();
        if(nReturns != 1)
            throw new InvalidReturnCountException(funcName);*/

        //usciamo dalla tabella della funzione e torniamo al padre (tab globale)
        activeSymbolTable.exitScope();

        return null;
    }

    @Override
    public Object visit(WhileOp whileOp) {
        //crea tabella scoping per while
        activeSymbolTable.enterScope(setProgressiveName("while"));

        //aggiungo come nodo figlio la nuova tabella funzione al padre
        activeSymbolTable.addChildToParentScope(activeSymbolTable.getActiveTable());

        //visito corpo while
        whileOp.doBody.setFunProcName(whileOp.getFunProcName()); //mi serve per controllo su return ad esempio
        whileOp.doBody.accept(this);

        //esco scope while
        activeSymbolTable.exitScope();
        return null;
    }

    @Override
    public Object visit(IfOp ifOp) {
        //crea tabella scoping per if
        activeSymbolTable.enterScope(setProgressiveName("if"));

        //aggiungo come nodo figlio la nuova tabella funzione al padre
        activeSymbolTable.addChildToParentScope(activeSymbolTable.getActiveTable());

        //visito corpo if
        ifOp.ifBody.setFunProcName(ifOp.ifBody.getFunProcName()); //mi serve per controllo su return ad esempio
        ifOp.ifBody.accept(this);

        //esco scope if
        activeSymbolTable.exitScope();

        //visito lista elifs se ci sono
        List<ElifOp> elifList = ifOp.elifs;
        if(elifList != null && elifList.size()>0){
            for(ElifOp e : elifList){
                e.accept(this);
            }
        }

        //se c'è un else creo tab di scoping anche per else
        if(ifOp.elseBody!=null)
            ifOp.elseBody.accept(this);

        //esco scope if
        /*activeSymbolTable.exitScope();*/
        return null;
    }

    @Override
    public Object visit(ElifOp elifOp) {
        //crea tabella scoping per elif
        activeSymbolTable.enterScope(setProgressiveName("elif"));

        //aggiungo come nodo figlio la nuova tabella funzione al padre
        activeSymbolTable.addChildToParentScope(activeSymbolTable.getActiveTable());

        //visito corpo elif
        elifOp.bodyOp.setFunProcName(elifOp.bodyOp.getFunProcName()); //mi serve per controllo su return ad esempio
        elifOp.bodyOp.accept(this);

        //esco da scope elif
        activeSymbolTable.exitScope();

        return null;
    }

    @Override
    public Object visit(ElseOp elseOp) {
        activeSymbolTable.enterScope(setProgressiveName("else"));

        activeSymbolTable.addChildToParentScope(activeSymbolTable.getActiveTable());

        elseOp.elseBody.setFunProcName( elseOp.elseBody.getFunProcName()); //mi serve per controllo su return ad esempio
        elseOp.elseBody.accept(this);

        activeSymbolTable.exitScope();

        return null;
    }

    /*----------------------------------------------------------------------------------------*/

    @Override
    public Object visit(ReturnOp returnOp) {
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
    public Object visit(ProcCallOp procCallOp) {
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
