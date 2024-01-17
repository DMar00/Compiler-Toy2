package main.syntaxtree.nodes.expr.binExpr;

import main.syntaxtree.nodes.expr.Expr;
import main.syntaxtree.visitor.Visitor;

public class AndOp extends BinaryExpr{
    public AndOp(Expr leftNode, Expr rightNode) {
        super("AndOp",leftNode, rightNode);
    }

    @Override
    public Object accept(Visitor v) {
        return v.visit(this);
    }
}
