package main.exceptions;

public class InvalidParameterReference extends RuntimeException{
    public InvalidParameterReference(String type, String id, String param) {
            super("Parameter '"+param+"' cannot be passed by reference to " +type+" "+id+" !");
    }
}
