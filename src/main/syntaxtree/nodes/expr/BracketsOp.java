package main.syntaxtree.nodes.expr;

import main.syntaxtree.visitor.Visitor;

public class BracketsOp extends Expr{
    public Expr expr;
    public BracketsOp(Expr expr) {
        super("BracketsOp");
        this.expr = expr;
    }
    public Object accept(Visitor visitor){
        return visitor.visit(this);
    }


}
