package main.syntaxtree.nodes.iter;

import main.syntaxtree.enums.Type;
import main.syntaxtree.nodes.BodyOp;
import main.syntaxtree.nodes.Node;
import main.syntaxtree.nodes.ProcFunParamOp;
import main.syntaxtree.nodes.expr.Id;
import main.syntaxtree.visitor.Visitor;

import java.util.List;

public class FunDeclOp extends Node implements IterOp{
    public Id functionName;
    public List<ProcFunParamOp> functionParamList;
    public List<Type> functionReturTypeList;
    public BodyOp functionBody;

    public FunDeclOp(Id functionName, List<ProcFunParamOp> functionParamList, List<Type> functionReturTypeList, BodyOp functionBody) {
        super("FunDeclOp");
        this.functionName = functionName;
        this.functionParamList = functionParamList;
        this.functionReturTypeList = functionReturTypeList;
        this.functionBody = functionBody;
    }

    @Override
    public Object accept(Visitor v) {
        return v.visit(this);
    }
}
