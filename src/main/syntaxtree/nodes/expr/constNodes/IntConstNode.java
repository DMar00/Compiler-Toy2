package main.syntaxtree.nodes.expr.constNodes;


import main.syntaxtree.visitor.Visitor;

public class IntConstNode extends ConstNode {
    public int value;
    public IntConstNode(int value) {
        super("int_const");
        this.value = value;
    }

    @Override
    public Object accept(Visitor v) {
        return v.visit(this);
    }
}
