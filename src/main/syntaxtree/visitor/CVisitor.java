package main.syntaxtree.visitor;

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
import main.syntaxtree.nodes.iter.FunDeclOp;
import main.syntaxtree.nodes.iter.IterOp;
import main.syntaxtree.nodes.iter.ProcOp;
import main.syntaxtree.nodes.iter.VarDeclOp;
import main.syntaxtree.nodes.stat.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CVisitor implements Visitor{
    private StringBuffer resultProgram;
    private int bufferSize = 256;
    private StringBuffer procFunSigns;
    private HashMap<String, List<Type>> funcMap;
    private HashMap<String, List<String>> idMap;
    /**/
    private int pointerCount;
    /**/

    public CVisitor(HashMap<String, List<Type>> funcMap) {
        resultProgram =new StringBuffer();
        procFunSigns = new StringBuffer();
        idMap = new HashMap<>();
        this.funcMap = funcMap;
        pointerCount = 0;
    }

    public void printToFile(String path) throws IOException {
        BufferedWriter bwr = new BufferedWriter(new FileWriter(path));
        bwr.write(resultProgram.toString());
        bwr.flush();
        bwr.close();
    }

    private String transformVariables(Type t, String name){
        switch (t){
            case INTEGER, BOOLEAN -> {
                return "int " + name;
            }
            case REAL -> {
                return "float " + name;
            }
            case STRING -> {
                return "char "+ name + "["+bufferSize+"]";
            }
            default -> {
                return null;
            }
        }
    }

    /********************************************************************************/

    @Override
    public Object visit(ProgramOp programOp) {

        //1. librerie da includere
        String library1 = "#include <stdio.h>\n";
        resultProgram.append(library1);

        //
        StringBuffer sb =new StringBuffer();
        for(IterOp i : programOp.itersList) {
            String s = (String) i.accept(this);
            sb.append(s);
        }

        //2. firma funzioni (eccetto main)
        resultProgram.append(procFunSigns);
        //3. programma
        resultProgram.append(sb);

        //TODO delete print
        System.out.println(resultProgram);
        return null;
    }

    @Override
    public Object visit(VarDeclOp varDeclOp) {
        StringBuffer sb = new StringBuffer();

        Type type = varDeclOp.type;
        String transType = transformVariables(type, "");

        for(Map.Entry<Id, ConstNode> entry : varDeclOp.ids.entrySet()) {
            Id id = entry.getKey();
            ConstNode cn = entry.getValue();
            String s1 = "\t" + transType + " " + id.idName + " ";
            sb.append(s1);   //int n =
            if(cn != null) {
                String value = (String) cn.accept(this);
                String s2 = "= " + value;
                sb.append(s2);   //int n = 5
            }
            sb.append(";\n");
        }

        return sb.toString();
    }

    @Override
    public Object visit(ProcFunParamOp procFunParamOp) {
        StringBuffer sb =new StringBuffer();

        //gestione parametri
        Mode modeParam = procFunParamOp.mode;
        String nameParam = procFunParamOp.id.idName;
        Type typeParam = procFunParamOp.type;

        String name;
        if(modeParam == Mode.OUT){
            if(idMap.containsKey(procFunParamOp.getFunProcName())) {
                idMap.get(procFunParamOp.getFunProcName()).add(nameParam);
            } else  {
                idMap.put(procFunParamOp.getFunProcName(), new ArrayList<>());
                idMap.get(procFunParamOp.getFunProcName()).add(nameParam);
            }
            name = "*"+nameParam+"_out";
        }else{
            name = nameParam;
        }

        String result = transformVariables(typeParam, name);
        sb.append(result);

        return sb.toString();
    }

    @Override
    public Object visit(BodyOp bodyOp) {

        StringBuffer sb =new StringBuffer();

        for(VarDeclOp v : bodyOp.varDeclOpList) {
            String var = (String) v.accept(this);
            sb.append(var);
        }

        for(Stat s : bodyOp.statList) {
            ((Node)s).setFunProcName(bodyOp.getFunProcName());
            String var = (String) s.accept(this);
            sb.append(var);
        }

        return sb.toString();
    }

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

    @Override
    public Object visit(ProcOp procOp) {
        //int main(...){
        //}

        StringBuffer sb =new StringBuffer();
        String nameProc = procOp.procName.idName;
        //return type
        String returnType;
        if(nameProc.equals("main"))
            returnType = "int";
        else
            returnType = "void";

        //proc params
        StringBuffer params = new StringBuffer();
        //TODO come si passano le stringhe ??????
        if(procOp.procParamsList != null){
            int paramsSize = procOp.procParamsList.size();
            int i = 0;
            for(ProcFunParamOp op : procOp.procParamsList){
                i++;
                op.setFunProcName(procOp.procName.idName);
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
        if(!nameProc.equals("main"))
            procFunSigns.append(sign+";\n");

        return sb.toString();
    }

    @Override
    public Object visit(ProcCallOp procCallOp) {
        StringBuffer sb = new StringBuffer();
        StringBuffer sbParams = new StringBuffer();
        List<ProcExpr> procParams = procCallOp.exprList;
        for(int i=0; i<procParams.size(); i++){
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

    @Override
    public Object visit(ProcExpr procExpr) {
        StringBuffer sb = new StringBuffer();
        String e = (String) procExpr.expr.accept(this);
        if(procExpr.procMode) {
            sb.append("&" + e);
        } else {
            sb.append(e);
        }
        return sb.toString();
    }

    @Override
    public Object visit(AssignOp assignOp) {
        StringBuffer sb = new StringBuffer();
        //a ^= 5;  ->  a = 5;
        //a, b ^= 5, 6; -> a = 5; b = 6;
        //a ^= func(); -> a = func();  func ha un sono val di ritorno
        //a, b ^= func(); -> a = *p1; b = *p2;

        for(int i = 0, j=0 ; i< assignOp.idList.size();  j++){
            Id id = assignOp.idList.get(i);
            assignOp.idList.get(i).setFunProcName(assignOp.getFunProcName());
            Expr e = assignOp.exprList.get(j);
            String expr = (String) e.accept(this);

            //se e è una funzione
            if(e instanceof  FunCallOp){
                FunCallOp f = (FunCallOp) e;
                String funcName = f.funName.idName;
                //id.setFunProcName(funcName);

                //lista parametri in chiamata a funzione
                List<Expr> paramCallList = f.exprList;

                //lista valori di ritorno della funzione
                List<Type> retTypesList = funcMap.get(f.funName.idName);

                //size valori ritorno
                int retTypes = retTypesList.size();
                if(retTypes>1){
                    StringBuffer sbDecls = new StringBuffer();
                    StringBuffer sbAssignPointers = new StringBuffer();

                    List<String> varPointers = new ArrayList<>();
                    for(int k=0; k<retTypes; k++){
                        //System.out.println("i = "+i+ " - id : "+assignOp.idList.get(i).idName+" - j: "+j+" - expr:"+expr);
                        //dichiaro le variabili che saranno passate come puntatore --> int p0; ...
                        //Type t = retTypesList.get(j);
                        Type t = retTypesList.get(k);
                        varPointers.add("p"+pointerCount);
                        String decl = "\t"+transformVariables(t,"") + " p" + pointerCount +";\n";
                        sb.append(decl);
                        //assegnazione id = pointer;
                        assignOp.idList.get(i).setFunProcName(assignOp.getFunProcName());
                        String updatedId =  (String) assignOp.idList.get(i).accept(this);
                        String assign = "\t"+updatedId + " =  p" + pointerCount+";\n";
                        sbAssignPointers.append(assign);
                        i++;
                        pointerCount++;
                    }

                    //chiamata a funzione --> func(param1, ..., *p ...); ...
                    StringBuffer sbCall = new StringBuffer();
                    StringBuffer sbParams = new StringBuffer();
                    for(int w = 0 ; w < paramCallList.size(); w++){
                        String param = (String) paramCallList.get(w).accept(this);
                        sbParams.append(param);
                        if(w != paramCallList.size()-1)
                            sbParams.append(", ");
                    }

                    StringBuffer sbPointers = new StringBuffer();
                    if(paramCallList.size()>0)
                        sbPointers.append(", ");
                    for(int x = 0 ; x < varPointers.size(); x++){
                        String p = "&"+varPointers.get(x);
                        sbPointers.append(p);
                        if(x != varPointers.size()-1)
                            sbPointers.append(", ");
                    }

                    sbCall.append("\t"+funcName+"("+ sbParams+sbPointers+ ");\n");

                    //
                    sb.append(sbDecls);
                    sb.append(sbCall);
                    sb.append(sbAssignPointers);
                }else{
                    String idFun = (String) assignOp.idList.get(i).accept(this);
                    String s = "\t" + idFun + " = " + expr + ";\n";
                    sb.append(s);
                    i++;
                }
            }else{
                //System.out.println("i = "+i);
                String idFun = (String) assignOp.idList.get(i).accept(this);
                String s = "\t" + idFun + " = " + expr + ";\n";
                sb.append(s);
                i++;
            }
        }

        return sb.toString();
    }

    @Override
    public Object visit(FunDeclOp funDeclOp) {
        StringBuffer sb =new StringBuffer();
        String nameFunc = funDeclOp.functionName.idName;
        //return type
        String returnType;
        int returnSize = funDeclOp.functionReturTypeList.size();

        if(returnSize == 1) {
            String t = transformVariables(funDeclOp.functionReturTypeList.get(0),"");
            returnType = t;
        }
        else {
            returnType = "void";
        }

        StringBuffer params = new StringBuffer();
        //TODO come si passano le stringhe ??????
        if(funDeclOp.functionParamList != null){
            int paramsSize = funDeclOp.functionParamList.size();
            int i = 0;
            for(ProcFunParamOp op : funDeclOp.functionParamList){
                i++;
                params.append((String) op.accept(this));
                if(!(i == paramsSize))
                    params.append(", ");
            }
            if(returnSize > 1){
                if(paramsSize > 0) {
                    params.append(", ");
                }
                for(int j = 0; j < returnSize; j++){
                    String t = transformVariables(funDeclOp.functionReturTypeList.get(j),"");
                    String p = t + " *p" + j;
                    params.append(p);
                    if(!(j == returnSize-1))
                        params.append(", ");
                }
            }
        }

        StringBuffer result = new StringBuffer();
        String s1 = returnType + " " + nameFunc + " (" + params + ")";
        result.append(s1);
        result.append("{\n");
        result.append((String) funDeclOp.functionBody.accept(this));
        result.append("\n}\n");

        sb.append(result);

        procFunSigns.append(s1+";\n");

        return sb.toString();
    }

    @Override
    public Object visit(FunCallOp funCallOp) {
        StringBuffer sbPar = new StringBuffer();
        StringBuffer sb = new StringBuffer();

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

    @Override
    public Object visit(Id id) {

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

    @Override
    public Object visit(ReturnOp returnOp) {
        StringBuffer sb = new StringBuffer();
        String s;

        if(returnOp.exprList.size() == 1) {
            String s1 = (String) returnOp.exprList.get(0).accept(this);
            s = "\treturn " + s1 + ";";
            sb.append(s);
        }
        else {
            List<Type> types = funcMap.get(returnOp.getFunProcName());
            int i = 0;
            for(Type t: types) {
                String e = (String) returnOp.exprList.get(i).accept(this);
                String p = "\t*p" + i + " = " + e + ";\n";
                i++;
                sb.append(p);
            }
        }



        return sb.toString();
    }

    @Override
    public Object visit(MinusOp minusOp) {
        StringBuffer sb = new StringBuffer();
        String s = (String) minusOp.rightNode.accept(this);
        sb.append("-"+s);
        return sb.toString();
    }

    @Override
    public Object visit(NotOp notOp) {
        StringBuffer sb = new StringBuffer();
        String s = (String) notOp.rightNode.accept(this);
        sb.append("!"+s);
        return sb.toString();
    }

    private String writeBinaryExpr(BinaryExpr e, String symbol) {
        StringBuffer sb = new StringBuffer();
        String s = (String) e.leftNode.accept(this);
        String s1 = (String) e.rightNode.accept(this);
        sb.append(s + symbol + s1);
        return sb.toString();
    }

    @Override
    public Object visit(AddOp addOp) {
        String eLeft = (String) addOp.leftNode.accept(this);
        String eRight = (String) addOp.rightNode.accept(this);

        if(addOp.leftNode.getNodeType() == Type.STRING && addOp.rightNode.getNodeType() == Type.STRING) {
            return eLeft.substring(0, eLeft.length()-1) + eRight.substring(1, eRight.length());
        } else {
            return writeBinaryExpr(addOp, "+");
        }
    }

    @Override
    public Object visit(AndOp andOp) {
        return writeBinaryExpr(andOp, "&&");
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
    public Object visit(EqOp eqOp) {
        return writeBinaryExpr(eqOp, "=");
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
    public Object visit(LeOp leOp) {
        return writeBinaryExpr(leOp, "<=");
    }

    @Override
    public Object visit(LtOp ltOp) {
        return writeBinaryExpr(ltOp, "<");
    }

    @Override
    public Object visit(MulOp mulOp) {
        return writeBinaryExpr(mulOp, "*");
    }

    @Override
    public Object visit(NeOp neOp) {
        return writeBinaryExpr(neOp, "<>");
    }

    @Override
    public Object visit(OrOp orOp) {
        return writeBinaryExpr(orOp, "||");
    }

    @Override
    public Object visit(IfOp ifOp) {
        StringBuffer sb = new StringBuffer();
        StringBuffer sbElifs = new StringBuffer();
        StringBuffer sbElse = new StringBuffer();


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
        StringBuffer sb = new StringBuffer();
        String s = " else if (" + elifOp.expr.accept(this) + ") {\n" + elifOp.bodyOp.accept(this) + "\n}";
        sb.append(s);
        return sb.toString();
    }

    @Override
    public Object visit(ElseOp elseOp) {
        StringBuffer sb = new StringBuffer();
        String s = " else" + "{\n" + elseOp.elseBody.accept(this) + "\n}";
        sb.append(s);
        return sb.toString();
    }

    @Override
    public Object visit(WhileOp whileOp) {
        StringBuffer sb = new StringBuffer();
        String s = "while (" + whileOp.whileExpr.accept(this) + ") {\n" + whileOp.doBody.accept(this) + "\n}";
        sb.append(s);
        return sb.toString();
    }

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
            String expr = (String) e.expression().accept(this);
            if(e.dollarMode()) {
                e.expression().setFunProcName(ioArgsOp.getFunProcName());
                String dollarId = (String) e.expression().accept(this);
                String type = String.valueOf(e.expression().getNodeType());
                String ft = getFormatSpecifier(type);
                //TODO controllo puntatore e stringa sul nn mettere &
                String param = "&" + dollarId;
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

    @Override
    public Object visit(ConstNode constNode) {
        return null;
    }
}
