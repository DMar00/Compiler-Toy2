package main.syntaxtree.nodes.stat;

import main.syntaxtree.nodes.BodyOp;
import main.syntaxtree.nodes.Node;
import main.syntaxtree.visitor.Visitor;

public class ElseOp extends Node implements Stat{
    public BodyOp elseBody;

    public ElseOp(BodyOp elseBody) {
        super("ElseOp");
        this.elseBody = elseBody;
    }

    @Override
    public Object accept(Visitor v) {
        return v.visit(this);
    }
}
