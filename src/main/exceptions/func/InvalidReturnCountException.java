package main.exceptions.func;

public class InvalidReturnCountException extends RuntimeException{
    public InvalidReturnCountException(String funcName) {
        super("Error: function '"+funcName+"' must have exactly one 'return' statement !");
    }
}
