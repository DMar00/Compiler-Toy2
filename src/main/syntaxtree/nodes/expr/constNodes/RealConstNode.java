package main.syntaxtree.nodes.expr.constNodes;


import main.syntaxtree.visitor.Visitor;

public class RealConstNode extends ConstNode {
    public float value;
    public RealConstNode(float value) {
        super("float_const");
        this.value = value;
    }

    @Override
    public Object accept(Visitor v) {
        return v.visit(this);
    }
}
