package main.syntaxtree.nodes.iter;

import main.syntaxtree.visitor.Visitor;

public interface IterOp {
    Object accept(Visitor v);
}
