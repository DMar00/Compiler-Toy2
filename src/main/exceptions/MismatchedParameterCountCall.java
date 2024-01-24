package main.exceptions;

public class MismatchedParameterCountCall extends RuntimeException{
    public MismatchedParameterCountCall(String type, String id, int expected, int provided) {
        super("The "+type+" '"+id+"' expected "+expected+" parameters, but "+provided+" were provided.");
    }
}
