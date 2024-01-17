package main.syntaxtree.nodes.expr.binExpr;

import main.syntaxtree.nodes.expr.Expr;
import main.syntaxtree.visitor.Visitor;

public class EqOp extends BinaryExpr{
    public EqOp(Expr leftNode, Expr rightNode) {
        super("EqOp",leftNode, rightNode);
    }

    @Override
    public Object accept(Visitor v) {
        return v.visit(this);
    }
}
