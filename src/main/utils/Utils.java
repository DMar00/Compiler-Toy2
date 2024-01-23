package main.utils;

import main.exceptions.IdAlreadyDeclared;
import main.exceptions.IdAlreadyDeclaredOtherType;
import main.syntaxtree.enums.Type;
import main.syntaxtree.nodes.expr.constNodes.*;
import main.table.SymbolItemType;

public class Utils {
    public final static String rootNodeName = "Global";
    public static Type constToType(ConstNode cn){
        if(cn instanceof IntConstNode) return Type.INTEGER;
        else if(cn instanceof RealConstNode) return Type.REAL;
        else if(cn instanceof StringConstNode) return Type.STRING;
        else if(cn instanceof BoolConstNode) return Type.BOOLEAN;
        else return null;   //TODO exception ("A VAR declaration must have an assignment to a constant value!");
    }

}
