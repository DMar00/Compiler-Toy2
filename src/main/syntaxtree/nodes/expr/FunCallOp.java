package main.syntaxtree.nodes.expr;

import main.syntaxtree.visitor.Visitor;

import java.util.List;

public class FunCallOp extends Expr{
    public Id funName;
    public List<Expr> exprList;
    public FunCallOp(Id funName, List<Expr> exprList) {
        super("FunCallOp");
        this.funName = funName;
        this.exprList = exprList;
    }

    public Object accept(Visitor visitor){
        return visitor.visit(this);
    }

}
