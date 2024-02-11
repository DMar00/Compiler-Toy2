package main.syntaxtree.nodes.expr.binExpr;

import main.syntaxtree.nodes.expr.Expr;
import main.syntaxtree.visitor.Visitor;

public class AddOp extends BinaryExpr{
    private boolean inPrintOrRead = false;
    public AddOp(Expr leftNode, Expr rightNode) {
        super("AddOp",leftNode, rightNode);
    }

    public boolean isInPrintOrRead() {
        return inPrintOrRead;
    }

    public void setInPrintOrRead(boolean inPrintOrRead) {
        this.inPrintOrRead = inPrintOrRead;
    }

    @Override
    public Object accept(Visitor v) {
        return v.visit(this);
    }
}
