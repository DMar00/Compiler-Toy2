package main.syntaxtree.nodes;

import main.syntaxtree.enums.Type;
import main.syntaxtree.visitor.Visitor;

public abstract class Node {
    public String name;
    private Type nodeType = null;

    public Node(String name) {
        this.name = name;
    }

    public void setNodeType(Type nodeType) {
        this.nodeType = nodeType;
    }

    public Type getNodeType() {
        return nodeType;
    }

    abstract public Object accept(Visitor v);
}
