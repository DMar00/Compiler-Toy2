package main.syntaxtree.nodes.expr;

import main.syntaxtree.nodes.Node;
import main.syntaxtree.visitor.Visitor;

public abstract class Expr extends Node {
    private boolean inPrintOrReadOrCall = false;

    public boolean isInPrintOrReadOrCall() {
        return inPrintOrReadOrCall;
    }

    public void setInPrintOrReadOrCall(boolean inPrintOrReadOrCall) {
        this.inPrintOrReadOrCall = inPrintOrReadOrCall;
    }

    public Expr(String name) {
        super(name);
    }

}
