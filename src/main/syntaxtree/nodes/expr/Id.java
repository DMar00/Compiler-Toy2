package main.syntaxtree.nodes.expr;

import main.syntaxtree.visitor.Visitor;
import main.table.SymbolItemType;

public class Id extends Expr {
    public String idName;
    private SymbolItemType idItemType;  //Per SemanticVisitor
    private Boolean isOut; //Per SemanticVisitor & CVisitor

    public Id(String idName) {
        super("Id");
        this.idName = idName;
        this.idItemType = null;
        this.isOut = true;
    }

    @Override
    public Object accept(Visitor v) {
        return v.visit(this);
    }

    public SymbolItemType getIdItemType() {
        return idItemType;
    }

    public void setIdItemType(SymbolItemType idItemType) {
        this.idItemType = idItemType;
    }

    public Boolean getOut() {
        return isOut;
    }

    public void setOut(Boolean out) {
        isOut = out;
    }
}
