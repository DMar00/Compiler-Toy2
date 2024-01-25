package main.exceptions.func;

public class InvalidReturnValue  extends RuntimeException{
    public InvalidReturnValue() {
        super("Function must return one or more values, not zero !");
    }
}
