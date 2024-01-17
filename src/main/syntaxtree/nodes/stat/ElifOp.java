package main.syntaxtree.nodes.stat;

import main.syntaxtree.nodes.BodyOp;
import main.syntaxtree.nodes.Node;
import main.syntaxtree.nodes.expr.Expr;
import main.syntaxtree.visitor.Visitor;

public class ElifOp extends Node implements Stat{
    public Expr expr;
    public BodyOp bodyOp;

    public ElifOp(Expr expr, BodyOp bodyOp) {
        super("ElifOp");
        this.expr = expr;
        this.bodyOp = bodyOp;
    }

    @Override
    public Object accept(Visitor v) {
        return v.visit(this);
    }
}
