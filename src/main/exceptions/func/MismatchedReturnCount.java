package main.exceptions.func;

public class MismatchedReturnCount extends RuntimeException{
    public MismatchedReturnCount(String funcName, int nExpected, int nProvided) {
        super("Error: function '"+funcName+"' must return "+nExpected+" value(s), not "+nProvided+" !");
    }
}
