package main.exceptions;

import main.syntaxtree.enums.Type;

public class InvalidTypeForUnaryExpr extends RuntimeException{
    public InvalidTypeForUnaryExpr(String operator, Type t2) {
        super("Error: bad operand types ["+t2.toString()+"] for unary operator '"+operator+"'");
    }
}
