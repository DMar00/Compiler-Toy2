package main.exceptions.if_elif_while;

public class NonBooleanExpression extends RuntimeException{
    public NonBooleanExpression(String stat) {
        super("The condition in the '"+stat+"' statement must be a boolean expression.");
    }
}
