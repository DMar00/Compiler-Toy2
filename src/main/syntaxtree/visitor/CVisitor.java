package main.syntaxtree.visitor;

import main.syntaxtree.enums.*;
import main.syntaxtree.nodes.*;
import main.syntaxtree.nodes.expr.*;
import main.syntaxtree.nodes.expr.binExpr.*;
import main.syntaxtree.nodes.expr.constNodes.*;
import main.syntaxtree.nodes.expr.unExpr.*;
import main.syntaxtree.nodes.iter.*;
import main.syntaxtree.nodes.stat.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CVisitor implements Visitor{
    //programma C senza librerie e firme funzioni
    private StringBuffer resultProgram;
    //lista firme funzioni
    private StringBuffer procFunSigns;


    //Costruttore
    public CVisitor(HashMap<String, List<Type>> funcMap) {
        resultProgram =new StringBuffer();
        procFunSigns = new StringBuffer();
        idMap = new HashMap<>();
        this.funcMap = funcMap;
        pointerCount = 0;
    }

    //stampa del file .c creato
    public void printToFile(String path) throws IOException {
        BufferedWriter bwr = new BufferedWriter(new FileWriter(path));
        bwr.write(resultProgram.toString());
        bwr.flush();
        bwr.close();
    }

    //traduce tipi TOY2 in tipi C
    private String transformVariables(Type t, String name){
        switch (t){
            case INTEGER, BOOLEAN -> {
                return "int " + name;
            }
            case REAL -> {
                return "float " + name;
            }
            case STRING -> {
                return "char *"+ name;
            }
            default -> {
                return null;
            }
        }
    }

    //per ogni tipo c restituisce il corrispondente format specifier
    private String getFormatSpecifier(String type) {
        switch (type) {
            case "integer", "boolean" -> {
                return "%d";
            }
            case "real" -> {
                return "%f";
            }
            case "string" -> {
                return "%s";
            }
            default -> {
                return null;
            }
        }
    }

    //controllo se var puntatore
    private boolean isAPointer(String id){
        return id.charAt(0) == '*';
    }







    //mappa che tiene traccia per ogni funzione...
    private HashMap<String, List<Type>> funcMap;
    //mappa che tiene traccia per ogni funzione ...
    private HashMap<String, List<String>> idMap;
    /**/
    private int pointerCount;
    /**/
    private boolean isConcatPointer = false;
    /**/
    //private List<String> stringVars = new ArrayList<>();

    private String strcat (String toAdd1, String toAdd2){
        StringBuffer sb = new StringBuffer();

        //nella strdup non ci vuole * se la variabile è un puntatore
        String op1, op2;
        if(toAdd1.charAt(0) == '*')
            op1 = toAdd1.substring(1);
        else
            op1 = toAdd1;
        if(toAdd2.charAt(0) == '*')
            op2 = toAdd2.substring(1);
        else
            op2 = toAdd2;

        //controllo se è già stata dichiarata la variabile char *c che uso come temporanea per la concatenazione
        if(!isConcatPointer) {
            sb.append("char *c = strdup(" + op1 + ");\n");
            isConcatPointer = true;
        } else{
            sb.append("c = strdup(" + op1 + ");\n");
        }

        sb.append("c = strcat(c, "+ op2+");\n");
        return sb.toString();
    }

    private String assignStrCat(String base,  String expr){
        StringBuffer sb = new StringBuffer();
        sb.append("free("+ base+");\n");
        sb.append(expr);
        sb.append(base + " = strdup(c);\n");
        sb.append("free(c);\n");
        return sb.toString();
    }








    /********************************************************************************/

    @Override
    public Object visit(ProgramOp programOp) {
        //1. librerie da includere
        String library1 = "#include <stdio.h>\n";
        String library2 = "#include <string.h>\n";
        String library3 = "#include <stdlib.h>\n";
        resultProgram.append(library1);
        resultProgram.append(library2);
        resultProgram.append(library3);
        resultProgram.append("\n");

        //
        StringBuffer sb =new StringBuffer();
        for(IterOp i : programOp.itersList) {
            String s = (String) i.accept(this);
            sb.append(s);
        }

        //2. firma funzioni (eccetto main)
        resultProgram.append(procFunSigns);
        resultProgram.append("\n");

        //3. programma
        resultProgram.append(sb);

        //TODO delete print
        System.out.println(resultProgram);
        return null;
    }


    /*---------------Id---------------*/
    @Override
    public Object visit(Id id) {
        //Nel caso mi trovo in una procedura con parametri out (quindi puntatori)
        // o in una funzione che ritorna più di un valore (avrò altri parametri puntatori)
        // quindi devo poi successivamente
        // continuare a prenderli come puntatatori nel corpo della func/procedura
        if(idMap.containsKey(id.getFunProcName())) {
            if(idMap.get(id.getFunProcName()).contains(id.idName)){
                return "*" + id.idName + "_out";
            } else {
                return id.idName;
            }
        } else  {
            return id.idName;
        }
    }


    /*---------------VarDeclOp---------------*/
    @Override
    public Object visit(VarDeclOp varDeclOp) {
        StringBuffer sb = new StringBuffer();

        Type type = varDeclOp.type; //tipo variabili    se ad esempio var s : integer;\
        String transType = transformVariables(type, "");

        for(Map.Entry<Id, ConstNode> entry : varDeclOp.ids.entrySet()) {
            Id id = entry.getKey();
            ConstNode cn = entry.getValue();
            String s1 = "\t" + transType + " " + id.idName + " ";   //tipo nomeVariabile
            sb.append(s1);

            if(cn != null) {
                String value = (String) cn.accept(this);
                String s2;
                if(cn instanceof StringConstNode)
                    s2 = "= strdup("+value+")"; //char * n = strdup("hello")
                else
                    s2 = "= " + value;          //int n = 5

                sb.append(s2);

            }else{
                if(type == Type.STRING){
                    sb.append(" = strdup(\"\")");      //char *n = strdup("");
                    //facciamo così e non semplicemente char *n;  altrimenti non viene modificata
                    // se passata ad una funzione per riferimento
                }
            }
            sb.append(";\n");
        }

        return sb.toString();
    }


    /*---------------BodyOp---------------*/
    @Override
    public Object visit(BodyOp bodyOp) {
        StringBuffer sb =new StringBuffer();

        for(VarDeclOp v : bodyOp.varDeclOpList) {
            String var = (String) v.accept(this);
            sb.append(var);
        }

        for(Stat s : bodyOp.statList) {
            //aggiungo per ogni stat come attributo il nome della func/proc in cui è presente
            //perchè potrebbe servirmi per alcuni controlli
            ((Node)s).setFunProcName(bodyOp.getFunProcName());
            String var = (String) s.accept(this);
            sb.append(var);
        }

        return sb.toString();
    }


    /*---------------ConstNode---------------*/
    @Override
    public Object visit(IntConstNode constNode) {
        StringBuffer sb = new StringBuffer();
        sb.append(constNode.value+"");
        return sb.toString();
    }

    @Override
    public Object visit(RealConstNode constNode) {
        StringBuffer sb = new StringBuffer();
        sb.append(constNode.value+"");
        return sb.toString();
    }

    @Override
    public Object visit(StringConstNode constNode) {
        StringBuffer sb = new StringBuffer();
        String var = "\"" + constNode.value + "\"";
        sb.append(var);
        return sb.toString();
    }

    @Override
    public Object visit(BoolConstNode constNode) {
        StringBuffer sb = new StringBuffer();
        String val;
        if(constNode.value) {
            val = "1";
        } else
            val = "0";
        sb.append(val);
        return sb.toString();
    }


    /*---------------IfOp / ElifOp / ElseOp ---------------*/
    @Override
    public Object visit(IfOp ifOp) {
        StringBuffer sb = new StringBuffer();
        StringBuffer sbElifs = new StringBuffer();
        StringBuffer sbElse = new StringBuffer();

        //passo all'espressione come attributo il nome della func/proc in cui è presente
        ifOp.expr.setFunProcName(ifOp.getFunProcName());

        String s = "if (" + ifOp.expr.accept(this) + ") {\n" + ifOp.ifBody.accept(this) + "\n}";

        if(ifOp.elifs != null) {
            for(ElifOp e : ifOp.elifs) {
                String s1 = (String) e.accept(this);
                sbElifs.append(s1);
            }
        }

        if(ifOp.elseBody != null) {
            String s1 = (String) ifOp.elseBody.accept(this);
            sbElse.append(s1);
        }

        sb.append(s);
        sb.append(sbElifs);
        sb.append(sbElse);

        return sb.toString();
    }

    @Override
    public Object visit(ElifOp elifOp) {
        //passo all'espressione come attributo il nome della func/proc in cui è presente
        elifOp.expr.setFunProcName(elifOp.getFunProcName());

        StringBuffer sb = new StringBuffer();
        String s = " else if (" + elifOp.expr.accept(this) + ") {\n" + elifOp.bodyOp.accept(this) + "\n}";
        sb.append(s);

        return sb.toString();
    }

    @Override
    public Object visit(ElseOp elseOp) {
        //passo al body come attributo il nome della func/proc in cui è presente
        elseOp.elseBody.setFunProcName(elseOp.getFunProcName());

        StringBuffer sb = new StringBuffer();
        String s = " else" + "{\n" + elseOp.elseBody.accept(this) + "\n}";
        sb.append(s);

        return sb.toString();
    }


    /*---------------WhileOp---------------*/
    @Override
    public Object visit(WhileOp whileOp) {
        //passo al body come attributo il nome della func/proc in cui è presente
        whileOp.whileExpr.setFunProcName(whileOp.getFunProcName());

        StringBuffer sb = new StringBuffer();
        String s = "while (" + whileOp.whileExpr.accept(this) + ") {\n" + whileOp.doBody.accept(this) + "\n}";
        sb.append(s);

        return sb.toString();
    }


    /*---------------Proc/Func Params---------------*/
    @Override
    public Object visit(ProcFunParamOp procFunParamOp) {
        StringBuffer sb =new StringBuffer();
        Mode modeParam = procFunParamOp.mode;
        String nameParam = procFunParamOp.id.idName;
        Type typeParam = procFunParamOp.type;

        String name;
        //Se il parametro è passato per riferimento [Mode == OUT]
        if(modeParam == Mode.OUT){
            if(idMap.containsKey(procFunParamOp.getFunProcName())) {
                idMap.get(procFunParamOp.getFunProcName()).add(nameParam);
            } else  {
                idMap.put(procFunParamOp.getFunProcName(), new ArrayList<>());
                idMap.get(procFunParamOp.getFunProcName()).add(nameParam);
            }

            if(typeParam == Type.STRING)    //altrimenti fa [char **param_out]
                name = nameParam+"_out";
            else
                name = "*"+nameParam+"_out";
        }
        //Se il parametro è passato per valore
        else{
            name = nameParam;
        }

        String result = transformVariables(typeParam, name);
        sb.append(result);

        return sb.toString();
    }


    /*---------------Procedures---------------*/
    @Override
    public Object visit(ProcOp procOp) {
        StringBuffer sb =new StringBuffer();
        String nameProc = procOp.procName.idName;

        //solo la procedura main ha int, le altre sono void
        String returnType;
        if(nameProc.equals("main"))
            returnType = "int";
        else
            returnType = "void";

        //concateno i parametri della procedura separati da virgola
        StringBuffer params = new StringBuffer();
        if(procOp.procParamsList != null){
            int paramsSize = procOp.procParamsList.size();
            int i = 0;
            for(ProcFunParamOp op : procOp.procParamsList){
                i++;
                op.setFunProcName(procOp.procName.idName);  //setto nome procedura che contiene parametro
                params.append((String) op.accept(this));
                if(!(i == paramsSize))
                    params.append(", ");
            }
        }

        StringBuffer result = new StringBuffer();
        String sign = returnType + " " + nameProc +"("+params+")";
        result.append(sign);
        result.append("{\n");
        procOp.procBody.setFunProcName(nameProc);
        result.append((String) procOp.procBody.accept(this));
        result.append("\n}\n");
        sb.append(result);

        //la firma del main non va aggiunta alla lista delle firme, le altre si
        if(!nameProc.equals("main"))
            procFunSigns.append(sign+";\n");

        return sb.toString();
    }

    @Override
    public Object visit(ProcExpr procExpr) {
        StringBuffer sb = new StringBuffer();

        String e = (String) procExpr.expr.accept(this);

        //procMode = true [passaggio per riferimento], quindi
        // --> se la var è un puntatore deve essere passata senza *
        // --> se la var NON è un puntatore deve essere passata con &
        if(procExpr.procMode) {
            if(isAPointer(e)) //int *p0...;     proc(p0);
                sb.append(e.substring(1, e.length()));
            else if (procExpr.expr.getNodeType() == Type.STRING){
                sb.append(e);   //char *s...;   proc(s);
            } else    //int i1...;     proc(&i1);
                sb.append("&" + e);
        } else {
            sb.append(e);
        }

        return sb.toString();
    }

    @Override
    public Object visit(ProcCallOp procCallOp) {
        StringBuffer sb = new StringBuffer();
        StringBuffer sbParams = new StringBuffer();
        List<ProcExpr> procParams = procCallOp.exprList;

        //concateno parametri in chiamata a procedura separati da virgola
        for(int i=0; i<procParams.size(); i++){
            //setto nome procedura al parametro
            procParams.get(i).expr.setFunProcName(procCallOp.getFunProcName());
            String p = (String) procParams.get(i).accept(this);
            sbParams.append(p);
            if(i<procParams.size()-1){
                sbParams.append(", ");
            }
        }

        String s = "\t" + procCallOp.procName.idName + "(" + sbParams+ ");\n";
        sb.append(s);

        return sb.toString();
    }


    /*---------------Functions---------------*/
    @Override
    public Object visit(FunDeclOp funDeclOp) {
        StringBuffer sb =new StringBuffer();
        String nameFunc = funDeclOp.functionName.idName;
        List<Type> returnTypeList = funDeclOp.functionReturTypeList;
        int returnSize = returnTypeList.size();

        String returnType;

        //se c'è un solo valore di ritorno, trasformo quel tipo di ritorno in equivalente C
        if(returnSize == 1) {
            String t = transformVariables(funDeclOp.functionReturTypeList.get(0),"");
            returnType = t;
        }
        //se i valori di ritorno sono più di uno allora la funzione sarà void
        else {
            returnType = "void";
        }

        //per le funzioni con più valori di ritorno
        //devono essere aggiunti come parametri dei puntatori corrispondenti ai valori di ritorno
        if(returnSize > 1){
            if(funDeclOp.functionParamList == null)
                funDeclOp.functionParamList = new ArrayList<>();

            for(int j = 0; j < returnSize; j++){
                String p;
                if(returnTypeList.get(j) == Type.STRING)    //la stringa ha già tipo char*
                    p = "p"+j;
                else
                    p = "*p"+j;
                ProcFunParamOp pointerParam = new ProcFunParamOp(Mode.INOUT, new Id(p), returnTypeList.get(j));
                funDeclOp.functionParamList.add(pointerParam);  //aggiungo il nuovo parametro puntatore alla lista
            }
        }

        StringBuffer params = new StringBuffer();
        int paramsSize = 0;
        //se ci sono parametri per la funzione
        if(funDeclOp.functionParamList != null){
            //accodo i parametri uno dopo l'altro separati da virgola
            paramsSize = funDeclOp.functionParamList.size();
            int i = 0;
            for(ProcFunParamOp op : funDeclOp.functionParamList){
                i++;
                params.append((String) op.accept(this));
                if(!(i == paramsSize))
                    params.append(", ");
            }
        }

        StringBuffer result = new StringBuffer();
        String s1 = returnType + " " + nameFunc + " (" + params + ")";
        result.append(s1);
        result.append("{\n");
        result.append((String) funDeclOp.functionBody.accept(this));
        result.append("\n}\n");
        sb.append(result);

        //la firma della func la inserisco nella lista globale che mi serve per
        // dichiarare tutte le firme prima del main
        procFunSigns.append(s1+";\n");

        return sb.toString();
    }

    @Override
    public Object visit(ReturnOp returnOp) {
        StringBuffer sb = new StringBuffer();
        List<Type> types = funcMap.get(returnOp.getFunProcName());  //prendo i tipi di ritorno della func
        String s;

        //se la funzione restituisce un solo valore, allora --> return expr;
        if(returnOp.exprList.size() == 1) {
            String s1 = (String) returnOp.exprList.get(0).accept(this);

            if((types.get(0) == Type.STRING) && isAPointer(s1)){
                //se la funzione restituisce una stringa e il valore da restituire è un puntatore
                //non va allora messo *
                s = "\treturn " + s1.substring(1) + ";";
                sb.append(s);
            }else{
                s = "\treturn " + s1 + ";";
                sb.append(s);
            }
        }
        //se la funzione restituisce più valori, allora -->
        else {
            //per ogni tipo assegno valore di ritorno al puntatore corrispondente
            //ad esempio return 3, 5  ->  *p0 = 3;  *p1 = 5;
            for(int i=0 ; i< types.size(); i++){
                String expr = (String) returnOp.exprList.get(i).accept(this);

                if((types.get(i) == Type.STRING)){
                    //se è una stringa il tipo di ritorno
                    String f = "\tfree(p"+i+");";
                    sb.append(f);
                    String p = "\tp" + i + " = strdup(" + expr + ");\n";
                    sb.append(p);
                }else{
                    //se non è stringa il tipo di ritorno
                    String p = "\t*p" + i + " = " + expr + ";\n";
                    sb.append(p);
                }
            }
        }

        return sb.toString();
    }

    @Override
    public Object visit(FunCallOp funCallOp) {
        StringBuffer sbPar = new StringBuffer();
        StringBuffer sb = new StringBuffer();

        //accodo separati da virgola i parametri nella chiamata di funzione
        if(funCallOp.exprList != null) {
            for(int i = 0; i<funCallOp.exprList.size(); i++) {
                String expr = (String) funCallOp.exprList.get(i).accept(this);
                sbPar.append(expr);
                if(i<(funCallOp.exprList.size()-1)) {
                    sbPar.append(", ");
                }
            }
        }

        String s = funCallOp.funName.idName + "(" + sbPar + ")";
        sb.append(s);

        return sb.toString();
    }


    /*---------------AssignOp---------------*/
    @Override
    public Object visit(AssignOp assignOp) {
        StringBuffer sb = new StringBuffer();
        //a ^= 5;  ->  a = 5;
        //a, b ^= 5, 6; -> a = 5; b = 6;
        //a ^= func(); -> a = func();  func ha un sono val di ritorno
        //a, b ^= func(); -> a = *p1; b = *p2;

        List<Id> leftIdList = assignOp.idList;
        List<Expr> rightExprList = assignOp.exprList;
        //ciclo su ogni id nell aparte sinistra
        for(int i = 0, j = 0; i < leftIdList.size() ; j++){
            Id id = leftIdList.get(i);
            Expr e = rightExprList.get(j);

            id.setFunProcName(assignOp.getFunProcName());
            e.setFunProcName(assignOp.getFunProcName());

            String expr = (String) e.accept(this);
            Type exprType = e.getNodeType();

            //se e è una funzione
            int retTypes = 0;
            List<Type> retTypesList = null;
            List<Expr> paramCallList = null;
            String funcName = null;
            if(e instanceof  FunCallOp){
                FunCallOp f = (FunCallOp) e;
                funcName = f.funName.idName;
                //lista valori di ritorno della funzione
                retTypesList = funcMap.get(funcName);
                //lista parametri in chiamata a funzione
                paramCallList = f.exprList;
                //size valori ritorno
                retTypes = retTypesList.size();
            }

            //se e è una funzione che restituisce più di un valore
            if(e instanceof  FunCallOp && retTypes>1){
                StringBuffer sbAssignPointers = new StringBuffer();

                //dichiaro le variabili che saranno passate come puntatore --> int p0; ...
                //dichiaro una variabile per ogni valore di ritorno
                StringBuffer sbDecls = new StringBuffer();
                //List<String> varPointers = new ArrayList<>();
                HashMap<String, Type> varPointers = new HashMap<>();


                for(int k=0; k<retTypes; k++) {
                    Type t = retTypesList.get(k);

                    //tipo pn;
                    varPointers.put("p"+pointerCount, t);
                    String decl = "\t"+declareVariable(t, "p"+pointerCount);
                    sb.append(decl);

                    //assegnazione id = pointer;
                    assignOp.idList.get(i).setFunProcName(assignOp.getFunProcName());
                    String updatedId =  (String) assignOp.idList.get(i).accept(this);


                    String assign;
                    if(t == Type.STRING){
                        assign = "free("+updatedId+");\n"
                                +updatedId+" = strdup(p"+pointerCount+");\n";
                    }else{
                        assign = "\t"+updatedId + " =  p" + pointerCount+";\n";
                    }


                    sbAssignPointers.append(assign);

                    i++;
                    pointerCount++;
                }

                //chiamata a funzione --> func(param1, ..., *p ...); ...
                //scorro i parametri [no puntatori] e li accodo separati da virgola
                StringBuffer sbParams = new StringBuffer();
                if(paramCallList != null){
                    for(int w = 0 ; w < paramCallList.size(); w++){
                        String param = (String) paramCallList.get(w).accept(this);
                        sbParams.append(param);
                        if(w != paramCallList.size()-1)
                            sbParams.append(", ");
                    }
                }


                //accodo ai parametri anche i puntatori
                StringBuffer sbPointers = new StringBuffer();
                if(paramCallList != null && paramCallList.size()>0)
                    sbPointers.append(", ");

                //per ogni puntatore, se non è stringa passo &p0, altrimenti p0
                int x = 0;
                for(Map.Entry<String, Type> entry : varPointers.entrySet()){
                    String pointer = entry.getKey();
                    Type pointerType = entry.getValue();
                    String p;
                    if(pointerType == Type.STRING){
                        p = pointer;
                    }else{
                        p = "&"+pointer;
                    }

                    sbPointers.append(p);
                    if(x != varPointers.size()-1)
                        sbPointers.append(", ");

                    x++;
                }

                StringBuffer sbCall = new StringBuffer();
                sbCall.append("\t"+funcName+"("+ sbParams+sbPointers+ ");\n");

                //
                sb.append(sbDecls);
                sb.append(sbCall);
                sb.append(sbAssignPointers);
            }
            //se e non è una funzione o una funzione che restituisce un solo valore
            else {
                String idFunc = (String) assignOp.idList.get(i).accept(this);

                //Nel caso delle stringhe:
                //1. viene fatta la free della variabile stringa che si vuole modificare
                //2. viene eseguita la strdup per allocare memoria
                if(exprType == Type.STRING){
                    if(e instanceof AddOp){
                        String s = expr+
                                idFunc + " = strdup(c);\n"+
                                "free(c);";
                        sb.append(s);
                    }
                    else{
                        String s = assignString(idFunc, expr);
                        sb.append(s);
                    }
                }
                //int n, t;
                //n = t;
                else{
                    String s = idFunc + " = " + expr + ";\n";
                    sb.append(s);
                }
                i++;
            }

        }

        return sb.toString();
    }

    private String assignString(String id, String val){
        //char *s...., char *s1....
        //free(s)
        //s = strdup(s1)

        StringBuffer sb = new StringBuffer();

        //* non va usato per id a sinistra
        String var;
        if(isAPointer(id))
            var = id.substring(1);
        else
            var = id;

        //* non va usato per id a destra
        String val2;
        if(isAPointer(val))
            val2 = val.substring(1);
        else
            val2 = val;

        sb.append("free("+var+");\n");
        sb.append(var+" = strdup("+val2+");\n");
        return sb.toString();
    }

    private String declareVariable(Type t , String id){
        String s;
        if(t == Type.STRING){
            s = transformVariables(t, "") + " " + id + " = strdup(\"\");\n";
        }else{
            s = transformVariables(t, "") + " " + id +";\n";
        }
        return s;
    }


    /*---------------UnaryExpr---------------*/
    @Override
    public Object visit(MinusOp minusOp) {
        StringBuffer sb = new StringBuffer();
        minusOp.rightNode.setFunProcName(minusOp.getFunProcName());
        String s = (String) minusOp.rightNode.accept(this);
        sb.append("-"+s);
        return sb.toString();
    }

    @Override
    public Object visit(NotOp notOp) {
        StringBuffer sb = new StringBuffer();
        notOp.rightNode.setFunProcName(notOp.getFunProcName());
        String s = (String) notOp.rightNode.accept(this);
        sb.append("!"+s);
        return sb.toString();
    }


    /*---------------BinaryExpr---------------*/
    private String writeBinaryExpr(BinaryExpr e, String symbol) {
        StringBuffer sb = new StringBuffer();
        e.leftNode.setFunProcName(e.getFunProcName());
        e.rightNode.setFunProcName(e.getFunProcName());
        String s = (String) e.leftNode.accept(this);
        String s1 = (String) e.rightNode.accept(this);
        sb.append(s + symbol + s1);
        return sb.toString();
    }

    @Override
    public Object visit(AddOp addOp) {
        addOp.leftNode.setFunProcName(addOp.getFunProcName());
        String eLeft = (String) addOp.leftNode.accept(this);
        addOp.rightNode.setFunProcName(addOp.getFunProcName());
        String eRight = (String) addOp.rightNode.accept(this);
        System.out.println(eLeft + " - "+eRight);

        if(addOp.leftNode.getNodeType() == Type.STRING && addOp.rightNode.getNodeType() == Type.STRING) {
            if(addOp.isInPrintOrRead())
                return eLeft.substring(0, eLeft.length()-1) + eRight.substring(1, eRight.length());
            else {
                return strcat(eLeft, eRight);
            }
        } else {
            return writeBinaryExpr(addOp, "+");
        }
    }

    @Override
    public Object visit(DiffOp diffOp) {
        return writeBinaryExpr(diffOp, "-");
    }

    @Override
    public Object visit(DivOp divOp) {
        return writeBinaryExpr(divOp, "/");
    }

    @Override
    public Object visit(MulOp mulOp) {
        return writeBinaryExpr(mulOp, "*");
    }

    @Override
    public Object visit(LeOp leOp) {
        return writeBinaryExpr(leOp, "<=");
    }

    @Override
    public Object visit(LtOp ltOp) {
        return writeBinaryExpr(ltOp, "<");
    }

    @Override
    public Object visit(GeOp geOp) {
        return writeBinaryExpr(geOp, ">=");
    }

    @Override
    public Object visit(GtOp gtOp) {
        return writeBinaryExpr(gtOp, ">");
    }

    @Override
    public Object visit(AndOp andOp) {
        return writeBinaryExpr(andOp, "&&");
    }

    @Override
    public Object visit(OrOp orOp) {
        return writeBinaryExpr(orOp, "||");
    }

    @Override
    public Object visit(EqOp eqOp) {
        return writeBinaryExpr(eqOp, "=");
    }

    @Override
    public Object visit(NeOp neOp) {
        return writeBinaryExpr(neOp, "!=");
    }

    /*---------------IoArgsOp---------------*/

    private String visitWrite(IOArgsOp ioArgsOp){
        StringBuffer sb = new StringBuffer();
        StringBuffer sbExpr = new StringBuffer();
        StringBuffer sbDollarId = new StringBuffer();
        ArrayList<String> dollarIdList = new ArrayList<>();

        //scorro ioExpr
        if(ioArgsOp.exprList != null) {
            for(IOArgsOp.IoExpr e: ioArgsOp.exprList) {
                //readExpr.add(e);
                if(e.dollarMode()) {//se l'expr è nel $()
                    e.expression().setFunProcName(ioArgsOp.getFunProcName());

                    String dollarId = (String) e.expression().accept(this);
                    dollarIdList.add(dollarId); //aggiungo le variabili che vanno messe dopo la virgola nella printf
                    String type = String.valueOf(e.expression().getNodeType());
                    String ft = getFormatSpecifier(type);
                    sbExpr.append(ft);  //aggiungo segnaposto per la printf
                } else { //se l'expr è fuori dal $()
                    if(e.expression() instanceof AddOp)
                        ((AddOp)e.expression()).setInPrintOrRead(true);

                    String s = (String) e.expression().accept(this);
                    sbExpr.append(s.substring(1,s.length()-1)); //prendo la stringa senza virgolette esterne
                }
            }

            for(int i=0; i<dollarIdList.size(); i++) {
                //printf("...", x, y , z) --> andiamo a costruire la parte [x, y , z]
                sbDollarId.append(dollarIdList.get(i));
                if(i<dollarIdList.size()-1)
                    sbDollarId.append(", ");
            }
        }


        String nl;
        if(ioArgsOp.mode == IOMode.WRITERETURN) {   //ci serve per far andare a capo se -->!
            nl="\\n";
        }else{
            nl = "";
        }

        //se non ci sono segnaposti non mettiamo la parte con la virgola nella printf
        if(sbDollarId.length() == 0) {
            String s = "printf(\"" +sbExpr+ nl + "\");\n";
            sb.append(s);
        } else {//se ci sono segnaposti  mettiamo la parte dopo la virgola nella printf
            String s = "printf(\"" +sbExpr+ nl +"\", " + sbDollarId + ");\n";
            sb.append(s);
        }


        return sb.toString();
    }

    private String visitRead(IOArgsOp ioArgsOp){
        StringBuffer sb = new StringBuffer();

        for(IOArgsOp.IoExpr e : ioArgsOp.exprList) {
            if((e.expression() instanceof AddOp) && (!e.dollarMode()))
                ((AddOp)e.expression()).setInPrintOrRead(true);

            String expr = (String) e.expression().accept(this);
            if(e.dollarMode()) {
                e.expression().setFunProcName(ioArgsOp.getFunProcName());
                String dollarId = (String) e.expression().accept(this);
                String type = String.valueOf(e.expression().getNodeType());
                String ft = getFormatSpecifier(type);

                String param;
                //controllo se il parametro è un puntatore e nel caso tolgo *
                if(dollarId.charAt(0)=='*')
                    param = dollarId.substring(1, dollarId.length());
                else{
                    if(type.equals(Type.STRING.toString()))
                        param = dollarId;
                    else
                        param = "&" + dollarId;
                }

                String s = "scanf(\"" + ft + "\", " + param + ");\n";
                sb.append(s);
            } else {
                String exprSub = expr.substring(1, expr.length()-1);
                String s = "printf(\"" + exprSub + "\");\n";
                sb.append(s);
            }
        }
        return sb.toString();
    }

    @Override
    public Object visit(IOArgsOp ioArgsOp) {
        if(ioArgsOp.mode == IOMode.WRITE || ioArgsOp.mode == IOMode.WRITERETURN) {
            return visitWrite(ioArgsOp);
        } else if (ioArgsOp.mode == IOMode.READ) {
            return visitRead(ioArgsOp);
        }
        return null;
    }

    /*--------------------------------------*/
    @Override
    public Object visit(ConstNode constNode) {
        return null;
    }
}
