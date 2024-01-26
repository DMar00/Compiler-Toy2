package main.syntaxtree.nodes.stat;

import main.syntaxtree.nodes.BodyOp;
import main.syntaxtree.nodes.Node;
import main.syntaxtree.nodes.expr.Expr;
import main.syntaxtree.visitor.Visitor;

import java.util.List;

public class IfOp extends Node implements Stat{
    public Expr expr;
    public BodyOp ifBody;
    public List<ElifOp> elifs;
    //public BodyOp elseBody;
    public ElseOp elseBody;

    public IfOp(Expr expr, BodyOp ifBody, List<ElifOp> elifs, ElseOp elseBody) {
        super("IfOp");
        this.expr = expr;
        this.ifBody = ifBody;
        this.elifs = elifs;
        this.elseBody = elseBody;
    }

    @Override
    public Object accept(Visitor v) {
        return v.visit(this);
    }
}
