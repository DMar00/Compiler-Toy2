package main.typecheck;

import main.syntaxtree.enums.Type;
import main.syntaxtree.nodes.expr.binExpr.*;
import main.syntaxtree.nodes.expr.unExpr.MinusOp;
import main.syntaxtree.nodes.expr.unExpr.NotOp;
import main.syntaxtree.nodes.expr.unExpr.UnaryExpr;

public class CompType {
    //typeSystem

    public static boolean areCompatibleTypes(Type t1, Type t2){
        if(t1 == t2) return true;
        else{
            if(t1 == Type.INTEGER && t2 == Type.REAL) return true;
            else if (t1 == Type.REAL && t2 == Type.INTEGER) return true;
            else return false;
        }
    }

    public static Type getTypeFromBinaryExpr(BinaryExpr expr){
        if(expr instanceof AddOp || expr instanceof DiffOp || expr instanceof MulOp || expr instanceof DivOp)
            return getTypeFromNumericExpr(expr);
        else if (expr instanceof AndOp || expr instanceof  OrOp)
            return getTypeFromBooleanExpr(expr);
        else if (expr instanceof EqOp || expr instanceof  GeOp || expr instanceof  GtOp
                ||expr instanceof  LeOp || expr instanceof  LtOp || expr instanceof NeOp)
            return getTypeFromLogicExpr(expr);

        return null;
    }

    public static Type getTypeFromUnaryExpr(UnaryExpr expr){
        if (expr instanceof MinusOp)
            return getTypeFromMinusExpr(expr);
        else if (expr instanceof NotOp)
            return getTypeFromNotExpr(expr);

        return null;
    }

    /**************************************************************************/


    private static Type getTypeFromNumericExpr(BinaryExpr expr){
        Type leftType = expr.leftNode.getNodeType();
        Type rightType = expr.rightNode.getNodeType();

        if(leftType == Type.INTEGER && rightType == Type.INTEGER) return Type.INTEGER;
        if(leftType == Type.INTEGER && rightType == Type.REAL) return Type.REAL;
        if(leftType == Type.REAL && rightType == Type.INTEGER) return Type.REAL;
        if(leftType == Type.REAL && rightType == Type.REAL) return Type.REAL;

        //concatenazione stringhe
        if((expr instanceof AddOp) && leftType == Type.STRING && rightType == Type.STRING)
            return Type.STRING;

        return null;
    }

    private static Type getTypeFromBooleanExpr(BinaryExpr expr){
        Type leftType = expr.leftNode.getNodeType();
        Type rightType = expr.rightNode.getNodeType();

        if(leftType == Type.BOOLEAN && rightType == Type.BOOLEAN) return Type.BOOLEAN;

        return null;
    }

    private static Type getTypeFromLogicExpr(BinaryExpr expr){
        Type leftType = expr.leftNode.getNodeType();
        Type rightType = expr.rightNode.getNodeType();

        if(leftType == Type.INTEGER && rightType == Type.INTEGER) return Type.BOOLEAN;
        if(leftType == Type.REAL && rightType == Type.INTEGER) return Type.BOOLEAN;
        if(leftType == Type.INTEGER && rightType == Type.REAL) return Type.BOOLEAN;
        if(leftType == Type.REAL && rightType == Type.REAL) return Type.BOOLEAN;
        return null;
    }

    private static Type getTypeFromMinusExpr(UnaryExpr expr){
        Type rightType = expr.rightNode.getNodeType();

        if(rightType == Type.INTEGER) return Type.INTEGER;
        if(rightType == Type.REAL) return Type.REAL;
        return null;
    }

    private static Type getTypeFromNotExpr(UnaryExpr expr){
        Type rightType = expr.rightNode.getNodeType();

        if(rightType == Type.BOOLEAN) return Type.BOOLEAN;
        return null;
    }
}
