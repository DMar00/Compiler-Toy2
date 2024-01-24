package main.syntaxtree.nodes.expr.unExpr;

import main.syntaxtree.nodes.expr.Expr;

public abstract class UnaryExpr extends Expr {
    public Expr rightNode;
    public UnaryExpr(String name, Expr rightNode) {
        super(name);
        this.rightNode = rightNode;
    }
}
