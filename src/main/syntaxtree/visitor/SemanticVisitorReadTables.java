package main.syntaxtree.visitor;

import main.exceptions.*;
import main.exceptions.proc.MainNotFound;
import main.syntaxtree.enums.Mode;
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

    private SymbolItem findInSpecificScope(String scopeName, String idToFind){
        SymbolTable copy = activeSymbolTable.clone();
        copy.enterSpecificScope(scopeName);
        SymbolItem found = copy.lookup(idToFind);
        return found;
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
        //controllo che le espressioni coinvolte non siano chiamate a funzioni che restituiscono valori multipli
        Expr rightExpr = expr.rightNode;
        Expr leftExpr = expr.leftNode;
        if(rightExpr instanceof FunCallOp){
            FunCallOp f = (FunCallOp) rightExpr;
            checkIfFunctionReturnMoreValue(f);
        }

        if(leftExpr instanceof FunCallOp){
            FunCallOp f = (FunCallOp) leftExpr;
            checkIfFunctionReturnMoreValue(f);
        }

        //
        rightExpr.accept(this);
        leftExpr.accept(this);

        //controllo compatibilità tipi nell'espressione binaria
        Type type = CompType.getTypeFromBinaryExpr(expr);
        if(type == null){
            String operator = Utils.ExprToSign(expr);
            throw new InvalidTypeForBinaryExpr(operator, expr.leftNode.getNodeType(), expr.rightNode.getNodeType());
        }


        return type;
    }

    private Type visitUnaryExpr(UnaryExpr expr){
        //controllo che le espressioni coinvolte non siano chiamate a funzioni che restituiscono valori multipli
        Expr rightExpr = expr.rightNode;
        if(rightExpr instanceof FunCallOp){
            FunCallOp f = (FunCallOp) rightExpr;
            checkIfFunctionReturnMoreValue(f);
        }

        //
        rightExpr.accept(this);

        //controllo tipi nell'espressione
        Type type = CompType.getTypeFromUnaryExpr(expr);

        if(type == null){
            String operator = Utils.ExprToSign(expr);
            throw new InvalidTypeForUnaryExpr(operator, expr.rightNode.getNodeType());
        }


        return type;
    }

    private void checkIfFunctionReturnMoreValue(FunCallOp f){
        SymbolItem s = findInSpecificScope(Utils.rootNodeName, f.funName.idName);
        int retTypes = s.getReturnTypeList().size();
        if(retTypes>1) {
            //TODO migliora Eccezione
            throw new RuntimeException("La funzione passata come param restituisce più valori!");
        }
    }

    private int whileCount = 0, ifCount = 0, elifCount = 0, elseCount=0;
    private String getProgressiveName(String basicName){
        if(basicName.equals("while")){
            return basicName+"_"+whileCount;
        }
        //TODO continua
        return null;
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
            if(st instanceof WhileOp){
                whileCount++;
            }
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
                    //System.out.println("E' un id? "+isId+"..:"+t);
                    exprDX.add(t);
                }
            }

            //se le size delle due liste coincidono, allora abbiamo un numero
            //di parametri giusto per l'assegnazione, quindi possiamo controllare i tipi
            if(idsSX.size() == exprDX.size()){
                for(int i = 0 ; i<idsSX.size(); i++){
                    //L'assegnazione è possibile solo tra tipi uguali
                    if(idsSX.get(i).getNodeType() != exprDX.get(i)){
                        //System.out.println("id: "+idsSX.get(i).getNodeType()+", e: "+exprDX.get(i));
                        throw new MismatchedTypes(exprDX.get(i).toString(), idsSX.get(i).getNodeType().toString());
                    }

                }
            }else {
                throw new MismatchedParameterCount();
            }

        }

        //TODO controllo in assegnazione che può essere effettuata solo se i parametri sono con Mode out
        for(Id id : assignOp.idList){
            //cerco nello scope globale (perchè lì procedure e funzioni sono definite)
            //la proc o procedura corrente, e mi prendo i parametri
            String activeNameScope = activeSymbolTable.getActiveTable().getNameScope();
            SymbolItem s = findInSpecificScope(Utils.rootNodeName, activeNameScope);
            List<ProcFunParamOp> params = s.getParams();
            if(params!= null && params.size()>0){
                for(ProcFunParamOp p : params){
                    if(id.idName.equals(p.id.idName)){
                        Mode mode = p.mode;
                        if(!(mode == Mode.OUT))
                            //TODO migliora eccezione
                            throw new RuntimeException("Stai tentando di modificare un paramentro non passato per riferimenro");
                    }
                }
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
        }else{
            found = activeSymbolTable.lookup(idName);
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

    /*--------*/

    @Override
    public Object visit(ProcCallOp procCallOp) {
        String procName = procCallOp.procName.idName;
        List<ProcExpr> procParam = procCallOp.exprList;

        //visito ogni parametro
        if(procParam != null && procParam.size()>0){
            for(ProcExpr e : procParam){
                e.accept(this);
            }
        }


        int sizeParam = 0;
        int sizeParamFound = 0;
        if(procParam != null) {
            //controllo che non ci siano come parametri chamate a funzioni con valori di ritorno multipli
            for(ProcExpr pe : procParam){
                Expr e = pe.expr;
                if(e instanceof FunCallOp){
                    FunCallOp f = (FunCallOp) e;
                    checkIfFunctionReturnMoreValue(f);
                }
            }
            sizeParam = procParam.size();
        }


        //controllo in tab globale (solo lì sono definite procedure) se c'è una proc con nome procName
        SymbolItem found = findInSpecificScope(Utils.rootNodeName, procName);

        if(found != null && found.getItemType()==SymbolItemType.PROCEDURE){
            List<ProcFunParamOp> paramsFound = found.getParams();

            if(paramsFound != null) sizeParamFound = paramsFound.size();

            //controllo che il numero di paratri nella chiamata sia corretto
            if(procParam!= null && paramsFound != null && (procParam.size() == paramsFound.size())){
                for(int i=0; i<paramsFound.size(); i++){
                    String idProcParam;
                    if(procParam.get(i).expr instanceof Id)
                        idProcParam = ((Id)(procParam.get(i).expr)).idName;
                    else
                        idProcParam = "Expr";


                    //controllo che i tipi coincidano
                    if(! (procParam.get(i).expr.getNodeType() == paramsFound.get(i).type))
                        throw new InvalidParameterType(SymbolItemType.PROCEDURE.toString(), procName, paramsFound.get(i).type.toString(), procParam.get(i).expr.getNodeType().toString());
                    //controllo che se c'è @ allora il paramsFound(i) c'è MODE = OUT
                    //e che dopo @ ci sia un id
                    if(procParam.get(i).procMode){  //false = noRif (per valore), true = per riferimento
                        if(!(paramsFound.get(i).mode == Mode.OUT))
                            throw new InvalidParameterReference(SymbolItemType.PROCEDURE.toString(), procName, idProcParam);
                        if(!(procParam.get(i).expr instanceof Id)) //TODO id deve essere per forza variabile?
                            throw new InvalidParameter(SymbolItemType.PROCEDURE.toString(), procName, idProcParam);
                    }else{
                        if(paramsFound.get(i).mode == Mode.OUT)
                            throw new InvalidParameterNotReference(SymbolItemType.PROCEDURE.toString(), procName, idProcParam);
                    }
                }
            }else{
                throw new MismatchedParameterCountCall(SymbolItemType.PROCEDURE.toString(), procName, sizeParamFound, sizeParam);
            }
        }else{
            throw new IdNotDeclared(SymbolItemType.PROCEDURE.toString(), procName);
        }
        return null;
    }

    @Override
    public Object visit(ProcExpr procExpr) {
        procExpr.expr.accept(this);
        return null;
    }

    /*--------*/
    @Override
    public Object visit(FunDeclOp funDeclOp) {
        String funcName = funDeclOp.functionName.idName;
        List<ProcFunParamOp> funParameters = funDeclOp.functionParamList;
        List<Type> funReturnTypes = funDeclOp.functionReturTypeList;

        //attivo tabella della func con nome funcName
        activeSymbolTable.enterSpecificScope(funcName);

        //in fase creazione tabella controllo che ci sia il return
        //qui controllo ciò che viene restituito
        for(Stat s : funDeclOp.functionBody.statList){
            if (s instanceof ReturnOp){
                ReturnOp returnOp = (ReturnOp) s;
                returnOp.accept(this);

                //controllo che valore/i restituito/i siano dello stesso tipo
                List<Expr> returnedExpr = returnOp.exprList;
                for(int j=0; j<funReturnTypes.size(); j++){
                    if(!(funReturnTypes.get(j)==returnedExpr.get(j).getNodeType())){
                        throw new RuntimeException("id non stesso tipo");
                    }
                }
            }
        }

        //visito body per controlli
        funDeclOp.functionBody.accept(this);

        //ritorno in scope padre
        activeSymbolTable.exitScope();

        return null;
    }

    @Override
    public Object visit(ReturnOp returnOp) {
        for(Expr e: returnOp.exprList)
            e.accept(this);
        return null;
    }

    @Override
    public Object visit(FunCallOp funCallOp) {
        String funName = funCallOp.funName.idName;
        List<Expr> funParams = funCallOp.exprList;

        //visito ogni parametro
        if(funParams != null && funParams.size()>0){
            for(Expr e : funParams){
                e.accept(this);
            }
        }

        //controllo in tab globale (solo lì sono definite funzioni) se c'è una funz con nome funcName
        SymbolItem found = findInSpecificScope(Utils.rootNodeName, funName);

        //altri controlli
        int sizeParam = 0;
        int sizeParamFound = 0;
        if(funParams != null) {
            for(Expr e: funParams){
                //in caso di somma(funcRet2Value())  e funcRet2Value() ritorna due valori
                if(e instanceof FunCallOp){
                    FunCallOp f = (FunCallOp) e;
                    checkIfFunctionReturnMoreValue(f);
                }
            }
            sizeParam = funParams.size();
        }

        if(found != null && found.getItemType()==SymbolItemType.FUNCTION){
            List<ProcFunParamOp> paramsFound = found.getParams();
            if(paramsFound != null) sizeParamFound = paramsFound.size();

            //controllo che il numero di parametri nella chiamata sia corretto
            if(funParams != null && paramsFound != null && (sizeParam == paramsFound.size())){

                for(int i=0; i<paramsFound.size(); i++){
                    String idProcParam;
                    if(funParams.get(i) instanceof Id)
                        idProcParam = ((Id)funParams.get(i)).idName;
                    else
                        idProcParam = "Expr";
                    //controllo che i tipi coincidano
                    if(! (funParams.get(i).getNodeType() == paramsFound.get(i).type))
                        throw new InvalidParameterType(SymbolItemType.FUNCTION.toString(), funName, paramsFound.get(i).type.toString(), funParams.get(i).getNodeType().toString());
                }
            }else{
                throw new MismatchedParameterCountCall(SymbolItemType.FUNCTION.toString(), funName, sizeParamFound, sizeParam);
            }
        }else{
            throw new IdNotDeclared(SymbolItemType.FUNCTION.toString(), funName);
        }
        return null;
    }

    /*--------*/


    /*-------------------------------------------------*/
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
        //entro scope while
        //activeSymbolTable.enterSpecificScope();

        //mi assicuro che espressione sia booleana, e la visito
        whileOp.whileExpr.accept(this);
        if(whileOp.whileExpr.getNodeType() != Type.BOOLEAN){
            //TODO migliora eccezione
            throw new RuntimeException("Il while vuole un boolean come espressione");
        }

        //visito body
        whileOp.doBody.accept(this);

        //esco scope while

        return null;
    }

    @Override
    public Object visit(IOArgsOp ioArgsOp) {
        return null;
    }


    /*-------------------------------------------------*/

    @Override
    public Object visit(ProcFunParamOp procFunParamOp) {
        return null;
    }

    @Override
    public Object visit(VarDeclOp varDeclOp) {
        return null;
    }

    @Override
    public Object visit(ConstNode constNode) {
        return null;
    }

}
