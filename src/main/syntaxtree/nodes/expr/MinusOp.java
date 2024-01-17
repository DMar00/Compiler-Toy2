package main.syntaxtree.nodes.expr;

import main.syntaxtree.visitor.Visitor;

public class MinusOp extends Expr{
    public Expr expr;

    public MinusOp(Expr expr) {
        super("MinusOp");
        this.expr = expr;
    }
    public Object accept(Visitor visitor){
        return visitor.visit(this);
    }

}
