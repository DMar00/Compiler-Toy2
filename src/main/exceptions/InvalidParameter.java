package main.exceptions;

public class InvalidParameter extends RuntimeException{
    public InvalidParameter(String type, String id, String param) {
        super("Error: Parameter '"+param+"' must be a variable in "+type+" "+id+" !");
    }
}
