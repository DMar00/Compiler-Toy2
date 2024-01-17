package main.syntaxtree.nodes.expr;

import main.syntaxtree.nodes.Node;

public abstract class Expr extends Node {
    public Expr(String name) {
        super(name);
    }

    /*public Object accept(Visitor visitor){
        return visitor.visit(this);
    }*/
}
