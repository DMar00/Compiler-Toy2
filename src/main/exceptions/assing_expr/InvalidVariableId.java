package main.exceptions.assing_expr;

public class InvalidVariableId extends RuntimeException{
    public InvalidVariableId(String id, String typeFound) {
        super("The provided ID '"+id+"' is a "+typeFound+" ID, not a variable ID!");
    }
}
