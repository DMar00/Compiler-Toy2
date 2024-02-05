package main.syntaxtree.nodes;

import main.syntaxtree.enums.Type;
import main.syntaxtree.visitor.Visitor;

import java.util.List;

public abstract class Node {
    public String name;

    //Per semantic Visitor
    private Type nodeType;   //mi serve per aggiungere info su tipi in Semantic Visitor
    private String funProcName; //mi serve per fare controlli sul corpo di funzioni o procedure / id in SemanticVisitor
    private List<Type> nodeTypes; //per i body di if , else ... in funzioni
    private boolean hasReturnTypes;

    public Node(String name) {
        this.name = name;
        this.funProcName = null;
        this.nodeType = null;
        this.hasReturnTypes = false;
    }

    public void setNodeType(Type nodeType) {
        this.nodeType = nodeType;
    }

    public Type getNodeType() {
        return nodeType;
    }

    public String getFunProcName() {
        return funProcName;
    }

    public void setFunProcName(String funProcName) {
        this.funProcName = funProcName;
    }

    public boolean isHasReturnTypes() {
        return hasReturnTypes;
    }

    public void setHasReturnTypes(boolean hasReturnTypes) {
        this.hasReturnTypes = hasReturnTypes;
    }

    public List<Type> getNodeTypes() {
        return nodeTypes;
    }

    public void setNodeTypes(List<Type> nodeTypes) {
        this.nodeTypes = nodeTypes;
    }

    abstract public Object accept(Visitor v);
}
