package main.syntaxtree.nodes.expr.binExpr;

import main.syntaxtree.nodes.expr.Expr;
import main.syntaxtree.visitor.Visitor;

public class AddOp extends BinaryExpr{
    public AddOp(Expr leftNode, Expr rightNode) {
        super("AddOp",leftNode, rightNode);
    }

    @Override
    public Object accept(Visitor v) {
        return v.visit(this);
    }
}
