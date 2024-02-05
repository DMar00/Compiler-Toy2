package main.syntaxtree.nodes;

import main.syntaxtree.nodes.iter.VarDeclOp;
import main.syntaxtree.nodes.stat.Stat;
import main.syntaxtree.visitor.Visitor;
import main.table.SymbolItemType;

import java.util.LinkedList;
import java.util.List;

public class BodyOp extends Node{
    public List<VarDeclOp> varDeclOpList;
    public List<Stat> statList;

    private int returnCount;    //per controllo sul numero di return in body

    public BodyOp() {
        super("BodyOp");
        this.varDeclOpList = new LinkedList<>();
        this.statList = new LinkedList<>();
        this.returnCount = 0;
    }

    public int getReturnCount() {
        return returnCount;
    }

    public void setReturnCount(int returnCount) {
        this.returnCount = returnCount;
    }

    @Override
    public Object accept(Visitor v) {
        return v.visit(this);
    }
}
