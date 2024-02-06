package main.utils;

import main.syntaxtree.enums.Type;
import main.syntaxtree.nodes.expr.Expr;
import main.syntaxtree.nodes.expr.binExpr.*;
import main.syntaxtree.nodes.expr.constNodes.*;
import main.syntaxtree.nodes.expr.unExpr.MinusOp;
import main.syntaxtree.nodes.expr.unExpr.NotOp;

public class Utils {
    public final static String rootNodeName = "Global";
    public static Type constToType(ConstNode cn){
        if(cn instanceof IntConstNode) return Type.INTEGER;
        else if(cn instanceof RealConstNode) return Type.REAL;
        else if(cn instanceof StringConstNode) return Type.STRING;
        else if(cn instanceof BoolConstNode) return Type.BOOLEAN;
        else {
            return null;   //exception ("A VAR declaration must have an assignment to a constant value!");
        }
    }

    public static String ExprToSign(Expr e){
        if(e instanceof AddOp) return "+";
        if(e instanceof DiffOp) return "-";
        if(e instanceof MulOp) return "*";
        if(e instanceof DivOp) return "/";
        if(e instanceof AndOp) return "&&";
        if(e instanceof OrOp) return "||";
        if(e instanceof MinusOp) return "-";
        if(e instanceof NotOp) return "!";
        if(e instanceof EqOp) return "=";
        if(e instanceof NeOp) return "<>";
        if(e instanceof LeOp) return "<=";
        if(e instanceof LtOp) return "<";
        if(e instanceof GeOp) return ">=";
        if(e instanceof GtOp) return ">";
        return null;
    }

}
