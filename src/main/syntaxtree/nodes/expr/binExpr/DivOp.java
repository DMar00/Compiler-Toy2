package main.syntaxtree.nodes.expr.binExpr;

import main.syntaxtree.nodes.expr.Expr;
import main.syntaxtree.visitor.Visitor;

public class DivOp extends BinaryExpr{
    public DivOp(Expr leftNode, Expr rightNode) {
        super("DivOp",leftNode, rightNode);
    }

    @Override
    public Object accept(Visitor v) {
        return v.visit(this);
    }
}
