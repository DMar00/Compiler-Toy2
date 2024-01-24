package main.exceptions;

public class InvalidParameterNotReference extends RuntimeException{
    public InvalidParameterNotReference(String type, String id, String param) {
            super("Parameter '"+param+"' must be passed by reference to " +type+" '"+id+"' !");
    }
}
