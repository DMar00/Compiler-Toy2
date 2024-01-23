package main.exceptions;

public class MismatchedTypes extends RuntimeException{
    public MismatchedTypes(String type1, String type2) {
        super("Error: incompatible types: "+type1+" cannot be converted to "+type2);
    }
}
