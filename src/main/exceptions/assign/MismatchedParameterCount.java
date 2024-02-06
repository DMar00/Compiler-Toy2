package main.exceptions.assign;

public class MismatchedParameterCount extends RuntimeException{
    public MismatchedParameterCount() {
        super("The number of parameters on the left is different from parameters on the right.");
    }
}
