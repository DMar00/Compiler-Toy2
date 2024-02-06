package main.syntaxtree.visitor.semanticVisitor;

import main.exceptions.*;
import main.exceptions.assign.MismatchedParameterCount;
import main.exceptions.assing_expr.InvalidVariableId;
import main.exceptions.func.InvalidReturnCountException;
import main.exceptions.func.InvalidReturnValue;
import main.exceptions.if_elif_while.NonBooleanExpression;
import main.exceptions.io.Invalid_IO_Id;
import main.exceptions.proc.MainNotFound;
import main.exceptions.proc.UnexpectedReturn;
import main.exceptions.return_.MismatchedReturnCount;
import main.syntaxtree.enums.IOMode;
import main.syntaxtree.enums.Mode;
import main.syntaxtree.enums.Type;
import main.syntaxtree.nodes.BodyOp;
import main.syntaxtree.nodes.Node;
import main.syntaxtree.nodes.ProcFunParamOp;
import main.syntaxtree.nodes.ProgramOp;
import main.syntaxtree.nodes.expr.Expr;
import main.syntaxtree.nodes.expr.FunCallOp;
import main.syntaxtree.nodes.expr.Id;
import main.syntaxtree.nodes.expr.ProcExpr;
import main.syntaxtree.nodes.expr.binExpr.*;
import main.syntaxtree.nodes.expr.constNodes.*;
import main.syntaxtree.nodes.expr.unExpr.MinusOp;
import main.syntaxtree.nodes.expr.unExpr.NotOp;
import main.syntaxtree.nodes.expr.unExpr.UnaryExpr;
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
import main.typecheck.CompType;
import main.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SemanticVisitorSecondVisit extends SemanticVisitorAbstract implements Visitor {
    public SemanticVisitorSecondVisit(SymbolTable symbolTableRoot) {
        this.activeSymbolTable = symbolTableRoot;
        resetVariablesCount();
    }

    private void checkIfVariableId(Expr e){
        Id id = (Id) e;
        if(!(id.getIdItemType() == SymbolItemType.VARIABLE)){
            throw new InvalidVariableId(id.idName, id.getIdItemType().toString());
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

        //controllo che se un'espressione è un ID deve essere un id di variabile
        if(rightExpr instanceof Id){
            checkIfVariableId(rightExpr);
        }
        if(leftExpr instanceof Id){
            checkIfVariableId(leftExpr);
        }

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

        //controllo che se un'espressione è un ID deve essere un id di variabile
        if(rightExpr instanceof Id){
            checkIfVariableId(rightExpr);
        }

        //controllo compatibilità tipi nell'espressione binaria
        Type type = CompType.getTypeFromUnaryExpr(expr);
        if(type == null){
            String operator = Utils.ExprToSign(expr);
            throw new InvalidTypeForUnaryExpr(operator, expr.rightNode.getNodeType());
        }

        return type;
    }

    /*----------------------------------------------------------------------------------------*/

    @Override
    public Object visit(ProgramOp programOp) {
        //controllo presenza del main
        SymbolNode node = activeSymbolTable.getActiveTable();
        boolean mainFound = false;
        for (Map.Entry<String, SymbolItem> entry : node.entrySet()) {
            String id = entry.getKey();
            SymbolItem values = entry.getValue();
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
    public Object visit(BodyOp bodyOp) {
        //contatore return -> non ci può essere più di un return in un corpo
        int returnCount = 0;

        //variabile booleana che mi dice se negli if-elsif-else o while sono completi di return
        boolean statReturn = false;

        //controllo statements : AssignOp, ...
        for(Stat st: bodyOp.statList){
            ((Node)st).setFunProcName(bodyOp.getFunProcName());
            st.accept(this);

            if(st instanceof ReturnOp){
                returnCount ++;
                if(returnCount > 1){
                    throw new InvalidReturnCountException(bodyOp.getFunProcName());
                }
                bodyOp.setNodeTypes( ((Node)st).getNodeTypes() ) ;
                statReturn = true;
            }

            if(st instanceof IfOp ){
                boolean r = ((Node)st).isHasReturnTypes();
                if(r)
                    statReturn=true;
            }

        }

        bodyOp.setHasReturnTypes(statReturn);
        return null;
    }

    @Override
    public Object visit(Id id) {
        SymbolItem found = findInParentsScopes(id.idName);

        id.setOut(found.isParamOUT());
        id.setNodeType(found.getVarType());
        id.setIdItemType(found.getItemType());
        return null;
    }

    @Override
    public Object visit(AssignOp assignOp) {
        for (Id id : assignOp.idList){
            id.accept(this);

            //se non è id di variabile
            checkIfVariableId(id);
        }

        List<Type> exprsType= new ArrayList<>();  //lista tipi exprs a destra
        for (Expr e: assignOp.exprList){
            //visito espressione
            e.accept(this);

            //se non è id di variabile
            if(e instanceof Id)
                checkIfVariableId(e);

            //riempio exprDX
            if( e instanceof FunCallOp){
                FunCallOp f = (FunCallOp) e;
                String funName = f.funName.idName;
                //trovo funName in tabella globale
                SymbolItem funFound = findInSpecificScope(Utils.rootNodeName, funName);
                //prendo tipi di ritorno e li metto in exprDX
                for(Type t: funFound.getReturnTypeList()){
                    exprsType.add(t);
                }
            }else{  //n1 ^= 5+3-1;  ||  n1, ... ^= n4, ...;
                exprsType.add(e.getNodeType());
            }
        }


        //CONTROLLO tipi e numero di variabili/funCall per le assegnazioni
        //n1 ^= n6;
        //n1, ..., nn ^= s1, ...., sn
        //n1, ..., nn ^= s1, ..., func() ...   -->     n1, n2, n3 ^= s1, func()
        int numIds = assignOp.idList.size();
        int numExpr = assignOp.exprList.size();
        //se il numero di parametri a destra è superiore al numero di parametri a sinistra, eccezione
        if(numExpr > numIds) {
            throw new MismatchedParameterCount();
        }else{ //id <= expr
            List<Id> idsSX = assignOp.idList;   //lista id a sinistra
            List<Type> exprDX = exprsType;  //lista exprs a destra

            //System.out.println("id size: "+idsSX.size()+" - exprs size: "+exprDX.size());

            //se le size delle due liste coincidono, allora abbiamo un numero
            //di parametri giusto per l'assegnazione, quindi possiamo controllare i tipi
            if(exprDX!=null && (idsSX.size() == exprDX.size())){
                for(int i = 0 ; i<idsSX.size(); i++){
                    //L'assegnazione è possibile solo tra tipi uguali
                    if(idsSX.get(i).getNodeType() != exprDX.get(i)){
                        throw new MismatchedTypes(exprDX.get(i).toString(), idsSX.get(i).getNodeType().toString());
                    }

                }
            }else {
                throw new MismatchedParameterCount();
            }
        }

        return null;
    }

    /*-------------*/

    @Override
    public Object visit(ProcOp procOp) {
        String procName = procOp.procName.idName;

        //attivo tabella della proc con nome procName
        activeSymbolTable.enterSpecificScope(procName);

        //visito body per controlli
        procOp.procBody.setFunProcName(procName);
        procOp.procBody.accept(this);

        //ritorno in scope padre
        activeSymbolTable.exitScope();
        return null;
    }

    @Override
    public Object visit(ProcCallOp procCallOp) {
        String procName = procCallOp.procName.idName;
        List<ProcExpr> procParam = procCallOp.exprList;

        //visito ogni parametro
        if(procParam != null && procParam.size()>0){
            for(ProcExpr e : procParam){
                e.accept(this);

                //verifico che se l'espressione è un id , deve essere un'id di variabile
                if(e.expr instanceof Id){
                    checkIfVariableId(e.expr);
                }
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
                        //if(!(procParam.get(i).expr instanceof Id))
                           // throw new InvalidParameter(SymbolItemType.PROCEDURE.toString(), procName, idProcParam);
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

    /*-------------*/

    @Override
    public Object visit(FunDeclOp funDeclOp) {
        String funcName = funDeclOp.functionName.idName;
        List<ProcFunParamOp> funcParameters = funDeclOp.functionParamList;
        List<Type> funcReturnTypes = funDeclOp.functionReturTypeList;
        BodyOp funcBody = funDeclOp.functionBody;

        //attivo tabella della func con nome funcName
        activeSymbolTable.enterSpecificScope(funcName);

        //visito body, per ottenere anche tipo e controllare che quindi ci sia il return giusto
        funcBody.setFunProcName(funcName);
        funcBody.accept(this);
        if(!funcBody.isHasReturnTypes())
            throw new RuntimeException("Missing return in all branch in '" + funcName+"' !");


        //ritorno in scope padre
        activeSymbolTable.exitScope();

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

                //verifico che se l'espressione è un id , deve essere un'id di variabile
                if(e instanceof Id){
                    checkIfVariableId(e);
                }
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

            //controllo che funzione abbia un solo valore di ritorno e assegno tipo al nodo
            if(found.getReturnTypeList().size()==1)
                funCallOp.setNodeType(found.getReturnTypeList().get(0));

            //controllo che il numero di parametri nella chiamata sia corretto
            //System.out.println("inseriti : "+sizeParam+ " - richiesti : "+ sizeParamFound);
            if( (sizeParam == 0 && sizeParamFound == 0) || (funParams != null && paramsFound != null && (sizeParam == sizeParamFound))){

                for(int i=0; i<sizeParamFound; i++){
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

    @Override
    public Object visit(ReturnOp returnOp) {
        List<Type> exprTypes = new ArrayList<>();
        for(Expr e: returnOp.exprList){
            e.accept(this);
            exprTypes.add(e.getNodeType());
        }

        String returnProcFun = returnOp.getFunProcName();
        //System.out.println(returnProcFun);
        SymbolItem found = activeSymbolTable.lookup(returnProcFun);
        if(found.getItemType() == SymbolItemType.PROCEDURE)
            throw new UnexpectedReturn(returnOp.getFunProcName());

        //se è una funzione, per ogni return trovato verifico che i tipi siano uguali e il numero sia corretto
        if(found.getItemType() == SymbolItemType.FUNCTION){
            List<Type> returnTypeFound = found.getReturnTypeList();
            if(exprTypes.size()!=returnTypeFound.size())
                throw new MismatchedReturnCount(returnProcFun, returnTypeFound.size(), exprTypes.size());
            else{
                for(int i=0 ; i<returnTypeFound.size(); i++){
                    if(exprTypes.get(i) != returnTypeFound.get(i))  //TODO tipi uguali o compatibili?
                        throw new MismatchedTypes(exprTypes.get(i).toString(), returnTypeFound.get(i).toString());
                }
            }

            ////////PER IL TIPO
            returnOp.setNodeTypes(exprTypes);
        }

        return null;
    }

    /*-------------*/

    @Override
    public Object visit(ElifOp elifOp) {
        //entro scope if
        activeSymbolTable.enterSpecificScope(setProgressiveName("elif"));

        //mi assicuro che espressione sia booleana, e la visito
        elifOp.expr.accept(this);
        if(elifOp.expr.getNodeType() != Type.BOOLEAN){
            throw new NonBooleanExpression("elif");
        }

        //visito body
        elifOp.bodyOp.setFunProcName(elifOp.getFunProcName());
        elifOp.bodyOp.accept(this);
        elifOp.setNodeTypes(elifOp.bodyOp.getNodeTypes());

        //esco scope elif
        activeSymbolTable.exitScope();

        return null;
    }

    @Override
    public Object visit(IfOp ifOp) {
        //entro scope if
        activeSymbolTable.enterSpecificScope(setProgressiveName("if"));

        //mi assicuro che espressione sia booleana, e la visito
        ifOp.expr.accept(this);
        if(ifOp.expr.getNodeType() != Type.BOOLEAN){
            throw new NonBooleanExpression("if");
        }

        //visito body
        ifOp.ifBody.setFunProcName(ifOp.getFunProcName());
        ifOp.ifBody.accept(this);
        ifOp.setNodeTypes(ifOp.ifBody.getNodeTypes());

        //visito elifs
        List<ElifOp> elifs = ifOp.elifs;
        if(elifs != null && elifs.size()>0){
            for(ElifOp e : elifs){
                e.setFunProcName(ifOp.getFunProcName());
                e.accept(this);
            }

        }

        //se ci sta else lo visito
        if(ifOp.elseBody!=null){
            ifOp.elseBody.setFunProcName(ifOp.getFunProcName());
            ifOp.elseBody.accept(this);
        }



        //il return o sta sia in if che elif che else oppure non va bene
        boolean isReturnInAll = true;
        List<Type> ifTypes = ifOp.getNodeTypes();
        if(ifTypes == null)
            isReturnInAll = false;

        if(elifs != null && elifs.size()>0){
            for(ElifOp e : elifs){
                List<Type> elifTypes = e.getNodeTypes();
                if(ifTypes == null)
                    isReturnInAll = false;
            }
        }

        if(ifOp.elseBody==null) //l'else deve esserci per forza
            isReturnInAll = false;
        else {
            List<Type> elseTypes = ifOp.elseBody.getNodeTypes();
            if(elseTypes == null)
                isReturnInAll = false;
        }

        if(isReturnInAll)
            ifOp.setHasReturnTypes(true);
        else
            ifOp.setHasReturnTypes(false);




        //esco scope if
        activeSymbolTable.exitScope();

        return null;
    }

    @Override
    public Object visit(ElseOp elseOp) {
        activeSymbolTable.enterSpecificScope(setProgressiveName("else"));

        elseOp.elseBody.setFunProcName(elseOp.getFunProcName());
        elseOp.elseBody.accept(this);
        elseOp.setNodeTypes(elseOp.elseBody.getNodeTypes());

        activeSymbolTable.exitScope();

        return null;
    }

    @Override
    public Object visit(WhileOp whileOp) {
        //entro scope while
        activeSymbolTable.enterSpecificScope(setProgressiveName("while"));

        //mi assicuro che espressione sia booleana, e la visito
        whileOp.whileExpr.accept(this);
        if(whileOp.whileExpr.getNodeType() != Type.BOOLEAN){
            throw new NonBooleanExpression("while");
        }

        //visito body
        whileOp.doBody.setFunProcName(whileOp.getFunProcName());
        whileOp.doBody.accept(this);

        //esco scope while
        activeSymbolTable.exitScope();
        return null;
    }

    /*-------------*/

    @Override
    public Object visit(IOArgsOp ioArgsOp) {
        IOMode mode = ioArgsOp.mode;
        List<IOArgsOp.IoExpr> exprList = ioArgsOp.exprList;

        if(mode == IOMode.READ){    //  <--
            for(IOArgsOp.IoExpr e : exprList){
                if(e.dollarMode()){
                    e.expression().accept(this);
                    if(!(e.expression() instanceof Id)){
                        throw new Invalid_IO_Id(); //Il valore nel $ deve essere id
                    }else{
                        Id id = (Id) e.expression();
                        if(id.getIdItemType() != SymbolItemType.VARIABLE){
                            throw new Invalid_IO_Id();
                        }
                    }
                }else{
                    e.expression().accept(this);
                    if(!(e.expression() instanceof StringConstNode) && !(e.expression().getNodeType() == Type.STRING)) {
                        throw new RuntimeException("fuori dal $() non può essere un id, gli id vanno dentro $()");
                    }
                }
            }
        }

        if(mode == IOMode.WRITE || mode == IOMode.WRITERETURN){  // -->
            for(IOArgsOp.IoExpr e : exprList) {
                e.expression().accept(this);
                if(e.dollarMode()){

                }else{
                    if(!(e.expression() instanceof StringConstNode) && !(e.expression().getNodeType() == Type.STRING)) {
                        throw new RuntimeException("fuori dal $() non può essere un id, gli id vanno dentro $()");
                    }
                }
            }
        }

        return null;
    }

    /*-------------*/

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

    /*-------------*/

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

    /*-------------*/

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

    /*-------------*/

    @Override
    public Object visit(VarDeclOp varDeclOp) {
        return null;
    }

    @Override
    public Object visit(ProcFunParamOp procFunParamOp) {
        return null;
    }

    @Override
    public Object visit(ConstNode constNode) {
        return null;
    }
}
