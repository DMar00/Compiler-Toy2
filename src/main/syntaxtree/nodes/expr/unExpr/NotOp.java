package main.syntaxtree.nodes.expr.unExpr;

import main.syntaxtree.nodes.expr.Expr;
import main.syntaxtree.visitor.Visitor;

public class NotOp extends UnaryExpr {
    public NotOp(Expr rightNode) {
        super("NotOp", rightNode);
    }

    public Object accept(Visitor visitor){
        return visitor.visit(this);
    }
}
