package main.syntaxtree.nodes.expr.constNodes;


import main.syntaxtree.visitor.Visitor;

public class BoolConstNode extends ConstNode {
    public boolean value;
    public BoolConstNode(boolean value) {
        super("bool_const");
        this.value = value;
    }

    @Override
    public Object accept(Visitor v) {
        return v.visit(this);
    }
}
