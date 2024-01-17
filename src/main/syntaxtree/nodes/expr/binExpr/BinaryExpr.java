package main.syntaxtree.nodes.expr.binExpr;

import main.syntaxtree.nodes.expr.Expr;

public abstract class BinaryExpr extends Expr {
    public Expr leftNode;
    public Expr rightNode;
    public BinaryExpr(String name, Expr leftNode, Expr rightNode) {
        super(name);
        this.leftNode = leftNode;
        this.rightNode = rightNode;
    }
}
