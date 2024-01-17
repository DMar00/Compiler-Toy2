package main.syntaxtree.nodes.expr;

import main.syntaxtree.visitor.Visitor;

public class NotOp extends Expr{
    public Expr expr;
    public NotOp(Expr expr) {
        super("NotOp");
        this.expr = expr;
    }

    public Object accept(Visitor visitor){
        return visitor.visit(this);
    }
}
