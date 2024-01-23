package main.typecheck;

import main.syntaxtree.enums.Type;
import main.syntaxtree.nodes.expr.binExpr.*;

public class CompType {
    //typeSystem

    public static Type getTypeFromBinaryExpr(BinaryExpr expr){
        if(expr instanceof AddOp || expr instanceof DiffOp || expr instanceof MulOp || expr instanceof DivOp)
            return getTypeFromNumericExpr(expr);

        /*switch(binaryOpNode.name) {
            case "AddOp", "DiffOp", "MulOp", "DivOp", "PowOp":
                return getTypeFromClassicNumericOps(binaryOpNode);
            case "DivIntOp":
                return getDivIntType(binaryOpNode);
            case "AndOp", "OrOp":
                return getTypeFromClassicBooleanOps(binaryOpNode);
            case "EQOp", "GEOp", "GTOp", "LEOp", "LTOp", "NEOp":
                return getTypeFromLogicOps(binaryOpNode);
            case "StrCatOp":
                if(binaryOpNode.left.getNodeType() != null && binaryOpNode.right.getNodeType() != null)
                    return Type.STRING;
        }
        return null;*/
        return null;
    }

    private static Type getTypeFromNumericExpr(BinaryExpr expr){
        Type leftType = expr.leftNode.getNodeType();
        Type rightType = expr.rightNode.getNodeType();

        if(leftType == Type.INTEGER && rightType == Type.INTEGER) return Type.INTEGER;
        if(leftType == Type.INTEGER && rightType == Type.REAL) return Type.REAL;
        if(leftType == Type.REAL && rightType == Type.INTEGER) return Type.REAL;
        if(leftType == Type.REAL && rightType == Type.REAL) return Type.REAL;

        return null;
    }
}
