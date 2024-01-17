package main.syntaxtree.nodes.iter;

import main.syntaxtree.nodes.BodyOp;
import main.syntaxtree.nodes.Node;
import main.syntaxtree.nodes.ProcFunParamOp;
import main.syntaxtree.nodes.expr.Id;
import main.syntaxtree.visitor.Visitor;

import java.util.List;

public class ProcOp extends Node implements IterOp{
    public Id procName;
    public List<ProcFunParamOp> procParamsList;
    public BodyOp procBody;

    public ProcOp(Id procName, List<ProcFunParamOp> procParamsList, BodyOp procBody) {
        super("ProcOp");
        this.procName = procName;
        this.procParamsList = procParamsList;
        this.procBody = procBody;
    }

    @Override
    public Object accept(Visitor v) {
        return v.visit(this);
    }
}
