package main.syntaxtree.visitor;

/*
 * Nel pattern Visitor, l'interfaccia Visitor definisce i metodi di visita corrispondenti ai diversi tipi di nodi
 * presenti nell'albero sintattico. Questi metodi di visita vanno implementati da una classe concreta per eseguire
 * operazioni specifiche su ciascun tipo di nodo.
 * */

import main.syntaxtree.nodes.BodyOp;
import main.syntaxtree.nodes.ProcFunParamOp;
import main.syntaxtree.nodes.ProgramOp;
import main.syntaxtree.nodes.expr.*;
import main.syntaxtree.nodes.expr.binExpr.*;
import main.syntaxtree.nodes.expr.constNodes.*;
import main.syntaxtree.nodes.expr.unExpr.MinusOp;
import main.syntaxtree.nodes.expr.unExpr.NotOp;
import main.syntaxtree.nodes.iter.FunDeclOp;
import main.syntaxtree.nodes.iter.ProcOp;
import main.syntaxtree.nodes.iter.VarDeclOp;
import main.syntaxtree.nodes.stat.*;

public interface Visitor {
    Object visit(Id id);
    Object visit(ProgramOp programOp);
    Object visit(VarDeclOp varDeclOp);
    Object visit(FunDeclOp funDeclOp);
    Object visit(ProcOp procOp);
    Object visit(IntConstNode constNode);
    Object visit(RealConstNode constNode);
    Object visit(StringConstNode constNode);
    Object visit(BoolConstNode constNode);
    Object visit(ProcFunParamOp procFunParamOp);
    Object visit(BodyOp bodyOp);
    Object visit(AssignOp assignOp);
    Object visit(MinusOp minusOp);
    Object visit(NotOp notOp);
    Object visit(ProcExpr procExpr);
    Object visit(FunCallOp funCallOp);
    Object visit(AddOp addOp);
    Object visit(AndOp andOp);
    Object visit(DiffOp diffOp);
    Object visit(DivOp divOp);
    Object visit(EqOp eqOp);
    Object visit(GeOp geOp);
    Object visit(GtOp gtOp);
    Object visit(LeOp leOp);
    Object visit(LtOp ltOp);
    Object visit(MulOp mulOp);
    Object visit(NeOp neOp);
    Object visit(OrOp orOp);
    Object visit(ProcCallOp procCallOp);
    Object visit(ReturnOp returnOp);
    Object visit(ElifOp elifOp);
    Object visit(IfOp ifOp);
    Object visit(ElseOp elseOp);
    Object visit(WhileOp whileOp);
    Object visit(IOArgsOp ioArgsOp);
    Object visit(ConstNode constNode);
}
