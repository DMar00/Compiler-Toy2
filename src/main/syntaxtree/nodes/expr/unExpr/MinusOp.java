package main.syntaxtree.nodes.expr.unExpr;

import main.syntaxtree.nodes.expr.Expr;
import main.syntaxtree.visitor.Visitor;

public class MinusOp extends UnaryExpr {
    public MinusOp(Expr rightNode) {
        super("MinusOp", rightNode);
    }
    public Object accept(Visitor visitor){
        return visitor.visit(this);
    }

}
