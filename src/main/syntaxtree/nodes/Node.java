package main.syntaxtree.nodes;

import main.syntaxtree.visitor.Visitor;

public abstract class Node {
    public String name;
    public Node(String name) {
        this.name = name;
    }
    abstract public Object accept(Visitor v);
}
