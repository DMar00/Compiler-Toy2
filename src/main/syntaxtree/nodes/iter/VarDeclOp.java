package main.syntaxtree.nodes.iter;

import main.syntaxtree.enums.Type;
import main.syntaxtree.nodes.Node;
import main.syntaxtree.nodes.expr.Id;
import main.syntaxtree.nodes.expr.constNodes.ConstNode;
import main.syntaxtree.visitor.Visitor;

import java.util.HashMap;

public class VarDeclOp extends Node implements IterOp {
    public Type type;
    public HashMap<Id, ConstNode> ids;

    public VarDeclOp(Type type, HashMap<Id, ConstNode> ids) {
        super("VarDeclOp");
        this.type = type;
        this.ids = ids;
    }

    @Override
    public Object accept(Visitor v) {
        return v.visit(this);
    }


}
