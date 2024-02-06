package main.syntaxtree.visitor;

import main.syntaxtree.enums.Mode;
import main.syntaxtree.enums.Type;
import main.syntaxtree.nodes.BodyOp;
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

import java.util.Map;

public class CVisitor implements Visitor{
    private int bufferSize = 256;
    private StringBuffer procFunSigns;

    public CVisitor() {
        procFunSigns = new StringBuffer();
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
    public Object visit(Id id) {
        return null;
    }

    @Override
    public Object visit(FunDeclOp funDeclOp) {
        return null;
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
