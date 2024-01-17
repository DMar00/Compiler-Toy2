package main.syntaxtree.nodes.expr.constNodes;

import main.syntaxtree.visitor.Visitor;

public class StringConstNode extends ConstNode {
    public String value;
    public StringConstNode(String value) {
        super("string_const");
        this.value = value;
    }

    @Override
    public Object accept(Visitor v) {
        return v.visit(this);
    }
}
