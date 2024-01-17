package main.syntaxtree.nodes.stat;

import main.syntaxtree.nodes.Node;
import main.syntaxtree.nodes.expr.Expr;
import main.syntaxtree.nodes.expr.Id;
import main.syntaxtree.visitor.Visitor;

import java.util.List;

public class AssignOp extends Node implements Stat {
    public List<Id> idList;
    public List<Expr>  exprList;
    public AssignOp(List<Id> idList, List<Expr>  exprList) {
        super("AssignOp");
        this.idList = idList;
        this.exprList = exprList;
    }
    @Override
    public Object accept(Visitor v) {
        return v.visit(this);
    }
}
