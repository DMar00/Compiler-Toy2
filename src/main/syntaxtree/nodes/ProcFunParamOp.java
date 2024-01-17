package main.syntaxtree.nodes;

import main.syntaxtree.enums.Mode;
import main.syntaxtree.enums.Type;
import main.syntaxtree.nodes.expr.Id;
import main.syntaxtree.visitor.Visitor;

public class ProcFunParamOp extends Node{
    public Mode mode;
    public Id id;
    public Type type;
    public ProcFunParamOp(Mode mode, Id id, Type type) {
        super("ProcFunParamOp");
        this.type = type;
        this.mode = mode;
        this.id = id;
    }

    @Override
    public Object accept(Visitor v) {
        return v.visit(this);
    }
}
