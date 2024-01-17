package main.syntaxtree.nodes.expr.binExpr;

import main.syntaxtree.nodes.expr.Expr;
import main.syntaxtree.visitor.Visitor;

public class GtOp extends BinaryExpr{
    public GtOp(Expr leftNode, Expr rightNode) {
        super("GtOp",leftNode, rightNode);
    }

    @Override
    public Object accept(Visitor v) {
        return v.visit(this);
    }
}
