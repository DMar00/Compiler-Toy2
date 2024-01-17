package main.syntaxtree.nodes.stat;

import main.syntaxtree.enums.IOMode;
import main.syntaxtree.nodes.Node;
import main.syntaxtree.nodes.expr.Expr;
import main.syntaxtree.visitor.Visitor;

import java.util.List;

public class IOArgsOp extends Node implements Stat{
    public IOMode mode;
    public List<Expr> exprList;
    public IOArgsOp(IOMode mode, List<Expr> exprList) {
        super("IOArgsOp");
        this.mode = mode;
        this.exprList = exprList;
    }

    @Override
    public Object accept(Visitor visitor){
        return visitor.visit(this);
    }

}
