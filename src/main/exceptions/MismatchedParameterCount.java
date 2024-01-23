package main.exceptions;

public class MismatchedParameterCount extends RuntimeException{
    public MismatchedParameterCount() {
        super("The number of parameters on the left is less than the number of parameters on the right.");
    }
}
