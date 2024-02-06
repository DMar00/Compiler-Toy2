package main.exceptions.return_;

public class MismatchedReturnCount extends RuntimeException{
    public MismatchedReturnCount(String funcName, int expected, int provided) {
        super("The function '"+funcName+"' expected "+expected+" return parameters, but "+provided+" were provided.");
    }
}
