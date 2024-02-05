package main.exceptions.func;

public class InvalidReturnValue  extends RuntimeException{
    public InvalidReturnValue(String funcName) {
        super("Function '"+funcName+"' must return one or more values, not zero !");
    }
}
