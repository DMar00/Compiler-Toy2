package main.syntaxtree.nodes.stat;

import main.syntaxtree.nodes.BodyOp;
import main.syntaxtree.nodes.Node;
import main.syntaxtree.nodes.expr.Expr;
import main.syntaxtree.visitor.Visitor;

public class WhileOp extends Node implements Stat{
    public Expr whileExpr;
    public BodyOp doBody;
    public WhileOp(Expr whileExpr, BodyOp doBody) {
        super("WhileOp");
        this.whileExpr = whileExpr;
        this.doBody = doBody;
    }

    @Override
    public Object accept(Visitor v) {
        return v.visit(this);
    }
}
