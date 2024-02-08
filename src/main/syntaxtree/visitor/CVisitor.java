package main.syntaxtree.visitor;

import main.syntaxtree.enums.Mode;
import main.syntaxtree.enums.Type;
import main.syntaxtree.nodes.BodyOp;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CVisitor implements Visitor{
    private int bufferSize = 256;
    private StringBuffer procFunSigns;
    private HashMap<String, List<Type>> funcMap;

    public CVisitor(HashMap<String, List<Type>> funcMap) {
        procFunSigns = new StringBuffer();
        this.funcMap = funcMap;
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
        StringBuffer resultProgram =new StringBuffer();

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
            String s1 = transType + " " + id.idName + " ";
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
            String var = "\t" + v.accept(this);
            sb.append(var);
        }

        for(Stat s : bodyOp.statList) {
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
                params.append((String) op.accept(this));
                if(!(i == paramsSize))
                    params.append(",");
            }
        }

        StringBuffer result = new StringBuffer();
        String sign = returnType + " " + nameProc +"("+params+")";
        result.append(sign);
        result.append("{\n");
        result.append((String) procOp.procBody.accept(this));
        result.append("\n}\n");

        sb.append(result);
        if(!nameProc.equals("main"))
            procFunSigns.append(sign+";\n");

        return sb.toString();
    }

    private void isFunc(Expr e) {
        if(e instanceof FunCallOp) {
            FunCallOp f = (FunCallOp) e;

        }
    }

    @Override
    public Object visit(AssignOp assignOp) {
        StringBuffer sb = new StringBuffer();
        //a ^= 5;  ->  a = 5;
        //a, b ^= 5, 6; -> a = 5; b = 6;

        for(int i = 0, j=0 ; i< assignOp.idList.size();  j++){
            Id id = assignOp.idList.get(i);
            Expr e = assignOp.exprList.get(j);
            String expr = (String) e.accept(this);

            //se e è una funzione
            if(e instanceof  FunCallOp){
                FunCallOp f = (FunCallOp) e;
                String funcName = f.funName.idName;

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
                        System.out.println("i = "+i+ " - id : "+assignOp.idList.get(i).idName+" - j: "+j+" - expr:"+expr);
                        //dichiaro le variabili che saranno passate come puntatore --> int p0; ...
                        //Type t = retTypesList.get(j);
                        Type t = retTypesList.get(k);
                        varPointers.add("p"+k);
                        String decl = "\t"+transformVariables(t,"") + " p" + k +";\n";
                        sb.append(decl);
                        //assegnazione id = pointer;
                        Id updatedId = assignOp.idList.get(i);
                        String assign = "\t"+updatedId.idName + " =  p" + k +";\n";
                        sbAssignPointers.append(assign);
                        i++;
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
                    String s = "\t" + assignOp.idList.get(i).idName + " = " + expr + ";\n";
                    sb.append(s);
                    i++;
                }
            }else{
                System.out.println("i = "+i);

                String s = "\t" + assignOp.idList.get(i).idName + " = " + expr + ";\n";
                sb.append(s);
                i++;
            }
        }

        /*for(int i = 0, k=0; i<assignOp.idList.size(); i++, k++) {
            Expr e = assignOp.exprList.get(i);
            String expr = (String) e.accept(this);

            //se e è una funzione
            if(e instanceof  FunCallOp){
                FunCallOp f = (FunCallOp) e;
                int paramListSize = f.exprList.size();
                List<Type> retTypesList = funcMap.get(f.funName.idName);
                int retTypes = retTypesList.size();
                if(retTypes == 1) {
                    //se la funzione restituisce un valore
                    String s = "\t" + assignOp.idList.get(i).idName + " = " + expr + ";\n";
                    sb.append(s);
                }else{
                    //se la funzione restituisce più valori
                    i+=retTypes;
                    for(int j=0 ; j<retTypes ; j++){
                        //dichiaro le variabili che saranno passate come puntatore int p0; ...
                        Type t = retTypesList.get(j);
                        String varPoint = "p"+j;
                        varPointers = new ArrayList<>();
                        varPointers.add(varPoint);
                        String p = "\t"+transformVariables(t, "") + " "+varPoint+ ";\n";
                        sb.append(p);
                    }
                    String exprSubString = expr.substring(0,expr.length()-1); //prendo la chiamata a funzione senza la parentesi finale
                    sb.append("\t"+exprSubString);
                    if(paramListSize>0)
                        sb.append(", ");
                    //aggiungo dei puntatori alla chiamata a funzione
                    for(int j=0 ; j<retTypes ; j++){
                        String s = "&"+varPointers.get(j);
                        sb.append(s);
                        if(j != retTypes-1)
                            sb.append(", ");
                    }
                    sb.append(");\n");
                    StringBuffer sa = new StringBuffer();
                    for(int j=0; j<retTypes; j++){
                        String a = "\t"+assignOp.idList.get(k).idName + " = ";
                        sa.append(a);
                        sa.append(varPointers.get(indexVarPointer));
                        sa.append(";\n");
                        k++;
                        indexVarPointer++;
                    }

                    sb.append(sa);
                }
            }else{
                //se non è una funzione
                String s = "\t" + assignOp.idList.get(i).idName + " = " + expr + ";\n";
                sb.append(s);
            }

        }*/

        //a ^= func(); -> a = func();  func ha un sono val di ritorno
        //a, b ^= func(); -> a = *p1; b = *p2;
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
        return id.idName;
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
    public Object visit(ElifOp elifOp) {
        return null;
    }

    @Override
    public Object visit(IfOp ifOp) {
        return null;
    }

    @Override
    public Object visit(ElseOp elseOp) {
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
