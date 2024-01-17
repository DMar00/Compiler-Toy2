package main.syntaxtree.nodes.stat;

import main.syntaxtree.nodes.Node;
import main.syntaxtree.nodes.expr.Expr;
import main.syntaxtree.visitor.Visitor;

import java.util.List;

public class ReturnOp extends Node implements Stat {
    public List<Expr> exprList;
    public ReturnOp(List<Expr> exprList) {
        super("ReturnOp");
        this.exprList = exprList;
    }

    @Override
    public Object accept(Visitor v) {
        return v.visit(this);
    }
}
