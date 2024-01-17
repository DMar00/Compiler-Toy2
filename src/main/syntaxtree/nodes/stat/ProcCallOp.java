package main.syntaxtree.nodes.stat;

import main.syntaxtree.nodes.Node;
import main.syntaxtree.nodes.expr.Id;
import main.syntaxtree.nodes.expr.ProcExpr;
import main.syntaxtree.visitor.Visitor;

import java.util.List;

public class ProcCallOp extends Node implements Stat {
    public Id procName;
    public List<ProcExpr> exprList;
    public ProcCallOp(Id procName, List<ProcExpr> exprList) {
        super("ProcCallOp");
        this.procName = procName;
        this.exprList = exprList;
    }

    @Override
    public Object accept(Visitor v) {
        return v.visit(this);
    }
}
