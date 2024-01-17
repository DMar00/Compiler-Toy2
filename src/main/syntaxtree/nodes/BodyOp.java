package main.syntaxtree.nodes;

import main.syntaxtree.nodes.iter.VarDeclOp;
import main.syntaxtree.nodes.stat.Stat;
import main.syntaxtree.visitor.Visitor;

import java.util.LinkedList;
import java.util.List;

public class BodyOp extends Node{
    public List<VarDeclOp> varDeclOpList;
    public List<Stat> statList;

    public BodyOp() {
        super("BodyOp");
        this.varDeclOpList = new LinkedList<>();
        this.statList = new LinkedList<>();
    }

    @Override
    public Object accept(Visitor v) {
        return v.visit(this);
    }
}
