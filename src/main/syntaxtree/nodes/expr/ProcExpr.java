package main.syntaxtree.nodes.expr;

import main.syntaxtree.visitor.Visitor;

public class ProcExpr extends Expr{
    public boolean procMode;        //false = noRif (valore), true = riferimento
    public Expr expr;
    public ProcExpr(boolean procMode, Expr expr) {
        super("ProcExpr");
        this.procMode = procMode;
        this.expr = expr;
    }
    @Override
    public Object accept(Visitor v) {
        return v.visit(this);
    }
}
