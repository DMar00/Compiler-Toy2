package main.exceptions;

import main.syntaxtree.enums.Type;

public class InvalidTypeForBinaryExpr extends RuntimeException{
    public InvalidTypeForBinaryExpr (String operator, Type t1, Type t2) {
        super("Error: bad operand types ["+t1.toString()+", "+t2.toString()+"] for binary operator '"+operator+"'");
    }
}
