package main.syntaxtree.visitor;

import main.exceptions.VariableAlreadyDeclared;
import main.syntaxtree.enums.Type;
import main.syntaxtree.nodes.BodyOp;
import main.syntaxtree.nodes.ProcFunParamOp;
import main.syntaxtree.nodes.ProgramOp;
import main.syntaxtree.nodes.expr.*;
import main.syntaxtree.nodes.expr.binExpr.*;
import main.syntaxtree.nodes.expr.constNodes.*;
import main.syntaxtree.nodes.iter.FunDeclOp;
import main.syntaxtree.nodes.iter.IterOp;
import main.syntaxtree.nodes.iter.ProcOp;
import main.syntaxtree.nodes.iter.VarDeclOp;
import main.syntaxtree.nodes.stat.*;
import main.table.SymbolItem;
import main.table.SymbolTable;

import java.util.Map;

public class SematicVisitor implements Visitor{
    private SymbolTable symbolTable;

    public SematicVisitor(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public Object visit(ProgramOp programOp) {
        //entriamo nello scope quindi attiviamo tabella per quello scope
        symbolTable.enterScope();
        //verifico se l'iterList (lista di variabili, funzioni o procedure) in ProgramOp è non vuota
        if(programOp.itersList!=null){
            for(IterOp i : programOp.itersList){
                i.accept(this);
            }
        }
        return null;
    }

    @Override
    public Object visit(VarDeclOp varDeclOp) {
        //var n, n1, ..... : integer;\
        //var n, n1, ..... : integer;     s, s1: string\
        Type varType = varDeclOp.type;
        for (Map.Entry<Id, ConstNode> entry : varDeclOp.ids.entrySet()) {
            Id idName = entry.getKey();
            String idString = idName.idName;
            //Controllo se la variabile è già presente nella tabella corrente
            if(!symbolTable.probe(idString)){
                //se la variabile non è presente, quindi non ci sono usi precedenti, la dichiaro con il marker a false
                SymbolItem item = new SymbolItem(idString, varType);
                item.setMarker(false);
                symbolTable.addId(item);
            }else{
                //se invece è già presente,
                SymbolItem item = symbolTable.lookup(idString);
                //se il marker = true, la variabile è già stata usata in precedenza ma non era dichiarata
                //quindi metto il marker a false perchè ora l'ho dichiarata
                if(item.isMarker())
                    item.setMarker(false);
                else{ //se il marker = false, significa che è stata già dichiarata una var con lo stesso id
                    throw new VariableAlreadyDeclared(idString);
                }
            }
        }
        return null;
    }

    @Override
    public Object visit(Id id) {
        return null;
    }



    @Override
    public Object visit(FunDeclOp funDeclOp) {
        return null;
    }

    @Override
    public Object visit(ProcOp procOp) {
        return null;
    }

    @Override
    public Object visit(IntConstNode constNode) {
        return null;
    }

    @Override
    public Object visit(RealConstNode constNode) {
        return null;
    }

    @Override
    public Object visit(StringConstNode constNode) {
        return null;
    }

    @Override
    public Object visit(BoolConstNode constNode) {
        return null;
    }

    @Override
    public Object visit(ProcFunParamOp procFunParamOp) {
        return null;
    }

    @Override
    public Object visit(BodyOp bodyOp) {
        return null;
    }

    @Override
    public Object visit(AssignOp assignOp) {
        return null;
    }

    @Override
    public Object visit(MinusOp minusOp) {
        return null;
    }

    @Override
    public Object visit(NotOp notOp) {
        return null;
    }

    @Override
    public Object visit(ProcExpr procExpr) {
        return null;
    }

    @Override
    public Object visit(FunCallOp funCallOp) {
        return null;
    }

    @Override
    public Object visit(AddOp addOp) {
        return null;
    }

    @Override
    public Object visit(AndOp andOp) {
        return null;
    }

    @Override
    public Object visit(DiffOp diffOp) {
        return null;
    }

    @Override
    public Object visit(DivOp divOp) {
        return null;
    }

    @Override
    public Object visit(EqOp eqOp) {
        return null;
    }

    @Override
    public Object visit(GeOp geOp) {
        return null;
    }

    @Override
    public Object visit(GtOp gtOp) {
        return null;
    }

    @Override
    public Object visit(LeOp leOp) {
        return null;
    }

    @Override
    public Object visit(LtOp ltOp) {
        return null;
    }

    @Override
    public Object visit(MulOp mulOp) {
        return null;
    }

    @Override
    public Object visit(NeOp neOp) {
        return null;
    }

    @Override
    public Object visit(OrOp orOp) {
        return null;
    }

    @Override
    public Object visit(BracketsOp bracketsOp) {
        return null;
    }

    @Override
    public Object visit(ProcCallOp procCallOp) {
        return null;
    }

    @Override
    public Object visit(ReturnOp returnOp) {
        return null;
    }

    @Override
    public Object visit(ElifOp elifOp) {
        return null;
    }

    @Override
    public Object visit(IfOp ifOp) {
        return null;
    }

    @Override
    public Object visit(WhileOp whileOp) {
        return null;
    }

    @Override
    public Object visit(IOArgsOp ioArgsOp) {
        return null;
    }

    @Override
    public Object visit(ConstNode constNode) {
        return null;
    }
}
