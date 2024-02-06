package main.syntaxtree.nodes.expr.constNodes;


import main.syntaxtree.nodes.expr.Expr;
import main.syntaxtree.visitor.Visitor;

public class ConstNode extends Expr {
    //private String stringValue;

    public ConstNode(String name) {
        super(name);
    }

    public Object accept(Visitor v) {
            return v.visit(this);
    }

    /*public String getValue() {
        return stringValue;
    }

    public void setValue(String value) {
        this.stringValue = value;
    }*/
}
