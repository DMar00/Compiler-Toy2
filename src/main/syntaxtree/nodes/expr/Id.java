package main.syntaxtree.nodes.expr;

import main.syntaxtree.visitor.Visitor;

public class Id extends Expr {
    public String idName;

    public Id(String idName) {
        super("Id");
        this.idName = idName;
    }

    @Override
    public Object accept(Visitor v) {
        return v.visit(this);
    }
}
