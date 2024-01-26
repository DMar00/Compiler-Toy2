package main.syntaxtree.visitor;

import main.exceptions.*;
import main.exceptions.func.InvalidReturnCountException;
import main.exceptions.func.InvalidReturnValue;
import main.exceptions.func.MismatchedReturnCount;
import main.exceptions.proc.UnexpectedReturn;
import main.exceptions.proc_func.ParamAlreadyDeclared;
import main.syntaxtree.enums.Mode;
import main.syntaxtree.nodes.expr.unExpr.MinusOp;
import main.syntaxtree.nodes.expr.unExpr.NotOp;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SemanticVisitorCreateTables implements Visitor {
    private SymbolTable activeSymbolTable;

    public SemanticVisitorCreateTables() {
        activeSymbolTable = new SymbolTable();
    }

    public SymbolTable getActiveSymbolTable() {
        return activeSymbolTable;
    }

    private void checkIdAlreadyDeclared(String idToFind, SymbolItemType type){
        SymbolItemType itemTypeFound = activeSymbolTable.lookup(idToFind).getItemType();
        if(itemTypeFound.name() != type.name())
            throw new IdAlreadyDeclaredOtherType(type.toString(), idToFind, itemTypeFound.toString());
        else throw new IdAlreadyDeclared(type.toString(), idToFind);
    }

    private int whileCount = 0, ifCount = 0, elifCount = 0, elseCount=0;
    private String setProgressiveName(String basicName){
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
            //System.out.println("c'è un else");
            elseCount++;
            return basicName+"_"+elseCount;
        }
        return null;
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
                    item.setMarker(false);   //ancora non è stata trovata dichiarazione con tipo
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

            idName.accept(this);
        }

        return null;
    }

    @Override
    public Object visit(ProcOp procOp) {
        Id procId = procOp.procName;
        String procedureName = procId.idName;

        //due procedure con stesso nome ma con parametri diversi, si può? PER NOI NO (SCELTA)

        //controllo se nella tabella globale è presente una procedura o altro con lo stesso nome
        if(activeSymbolTable.probe(procedureName)){
            checkIdAlreadyDeclared(procedureName, SymbolItemType.PROCEDURE);
        }

        //controllo che si può fare già in ProcParam, dove lancio eccezione se già ci sta
        //controllo se ci sono paramentri con stesso id
        if(procOp.procParamsList!=null && procOp.procParamsList.size()>0){
            List<String> newList = new ArrayList<>();
            for(ProcFunParamOp p : procOp.procParamsList){
                String s = p.id.idName;
                if(!newList.contains(s))
                    newList.add(s);
                else
                    throw new ParamAlreadyDeclared(s, procedureName, SymbolItemType.PROCEDURE.toString());
            }
        }

        //controllo che non ci sia return
        for(Stat s : procOp.procBody.statList){
            if (s instanceof ReturnOp)
                throw new UnexpectedReturn(procedureName);
        }

        //aggiungo la procedura alla tabella dei simboli
        // globale + setto marker false in quanto sto definendo procedura
        SymbolItem item = new SymbolItem(procedureName, procOp.procParamsList);
        item.setMarker(false);
        activeSymbolTable.addId(item);

        //creo la tabella dei simboli per questa procedura
        activeSymbolTable.enterScope(procedureName);

        //visit di ogni parametro (così lo aggiungiamo alla tabella dei simboli della proc)
        if(procOp.procParamsList!=null && procOp.procParamsList.size()>0){
            for(ProcFunParamOp p : procOp.procParamsList){
                p.accept(this);
            }
        }

        //aggiungo come nodo figlio la nuova tabella procedure al padre
        activeSymbolTable.addChildToParentScope(activeSymbolTable.getActiveTable());

        //visitiamo il corpo della procedura per riempire la tabella della procedura
        procOp.procBody.accept(this);

        //usciamo dalla tabella della procedura e torniamo al padre (tab globale)
        activeSymbolTable.exitScope();

        return null;
    }

    @Override
    public Object visit(ProcFunParamOp procFunParamOp) {
        String id = procFunParamOp.id.idName;
        Type t = procFunParamOp.type;
        if(!activeSymbolTable.probe(id)){
            SymbolItem symbolItem = new SymbolItem(id, t);
            symbolItem.setMarker(false);
            if(procFunParamOp.mode != Mode.OUT)
                symbolItem.setParamOUT(false);
            activeSymbolTable.addId(symbolItem);
        }

        procFunParamOp.setNodeType(t);
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
        String funcName = funDeclOp.functionName.idName;
        List<ProcFunParamOp> funParameters = funDeclOp.functionParamList;
        List<Type> funReturnTypes = funDeclOp.functionReturTypeList;

        //verifico che non ci sia altro definito con lo stesso nome
        if(activeSymbolTable.probe(funcName)){
            checkIdAlreadyDeclared(funcName, SymbolItemType.FUNCTION);
        }


        //controllo che si può fare già in ProcParam, dove lancio eccezione se già ci sta
        //controllo se ci sono paramentri con stesso id
        if(funParameters!=null && funParameters.size()>0){
            List<String> newList = new ArrayList<>();
            for(ProcFunParamOp p : funParameters){
                String s = p.id.idName;
                if(!newList.contains(s))
                    newList.add(s);
                else
                    throw new ParamAlreadyDeclared(s, funcName, SymbolItemType.FUNCTION.toString());
            }
        }

        //controllo che i tipi di ritorno siano superiori ad 1
        if(!(funReturnTypes.size()>=1))
            throw new InvalidReturnValue();


        //aggiungo la funzione alla tabella dei simboli
        // globale + setto marker false in quanto sto definendo funzione
        SymbolItem item = new SymbolItem(funcName, funParameters, funReturnTypes);
        item.setMarker(false);
        activeSymbolTable.addId(item);

        //creo la tabella dei simboli per questa funzione
        activeSymbolTable.enterScope(funcName);

        //visit di ogni parametro (così lo aggiungiamo alla tabella dei simboli della func)
        if(funParameters!=null && funParameters.size()>0){
            for(ProcFunParamOp p : funParameters){
                p.accept(this);
            }
        }

        //controllo che ci sia il return
        int i = 0;
        for(Stat s : funDeclOp.functionBody.statList){
            if (s instanceof ReturnOp) {
                i++;
                ReturnOp returnOp = (ReturnOp) s;
                //controllo che il numero di valori restituiti coincide col numero di valori di ritorno dichiarati
                if(returnOp.exprList.size() != funReturnTypes.size())
                    throw new MismatchedReturnCount(funcName,funReturnTypes.size(), returnOp.exprList.size());
            }
        }
        if(!(i==1))
            throw new InvalidReturnCountException(funcName);

        //aggiungo come nodo figlio la nuova tabella funzione al padre
        activeSymbolTable.addChildToParentScope(activeSymbolTable.getActiveTable());

        //visitiamo il corpo della funzione per riempire la tabella della funzione
        funDeclOp.functionBody.accept(this);

        //usciamo dalla tabella della funzione e torniamo al padre (tab globale)
        activeSymbolTable.exitScope();

        return null;
    }

    @Override
    public Object visit(Id id) {
        String idName = id.idName;

        SymbolItem found = activeSymbolTable.lookup(idName);
        if(found == null)
            throw new IdNotDeclared("Id", idName);

        id.setNodeType(found.getVarType());
        return null;
    }

    @Override
    public Object visit(WhileOp whileOp) {
        //crea tabella scoping per while
        activeSymbolTable.enterScope(setProgressiveName("while"));

        //aggiungo come nodo figlio la nuova tabella funzione al padre
        activeSymbolTable.addChildToParentScope(activeSymbolTable.getActiveTable());

        //visito corpo while
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
        ifOp.ifBody.accept(this);

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
        activeSymbolTable.exitScope();

        return null;
    }

    @Override
    public Object visit(ElseOp elseOp) {
        activeSymbolTable.enterScope(setProgressiveName("else"));

        activeSymbolTable.addChildToParentScope(activeSymbolTable.getActiveTable());

        elseOp.elseBody.accept(this);

        activeSymbolTable.exitScope();
        return null;
    }

    @Override
    public Object visit(ElifOp elifOp) {
        //crea tabella scoping per if
        activeSymbolTable.enterScope(setProgressiveName("elif"));

        //aggiungo come nodo figlio la nuova tabella funzione al padre
        activeSymbolTable.addChildToParentScope(activeSymbolTable.getActiveTable());

        //visito corpo elif
        elifOp.bodyOp.accept(this);

        //esco da scope elif
        activeSymbolTable.exitScope();

        return null;
    }



    @Override
    public Object visit(IOArgsOp ioArgsOp) {
        return null;
    }

    /*---------------------------------------------------------*/


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
    public Object visit(ConstNode constNode) {
        return null;
    }
}
