package main.syntaxtree.nodes.expr;

import main.syntaxtree.nodes.Node;
import main.syntaxtree.visitor.Visitor;

public abstract class Expr extends Node {
    public Expr(String name) {
        super(name);
    }

}
