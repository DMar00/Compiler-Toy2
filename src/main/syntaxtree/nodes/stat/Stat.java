package main.syntaxtree.nodes.stat;

import main.syntaxtree.visitor.Visitor;

public interface Stat{
    Object accept(Visitor v);
}
