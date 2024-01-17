package main.syntaxtree.nodes;

import main.syntaxtree.nodes.iter.IterOp;
import main.syntaxtree.visitor.Visitor;

import java.util.List;

public class ProgramOp extends Node{
    public List<IterOp> itersList;

    public ProgramOp(List<IterOp> itersList) {
        super("ProgramOp");
        this.itersList = itersList;
    }

    @Override
    public Object accept(Visitor v) {
        return v.visit(this);
    }
}
