package main.exceptions.func;

public class MultipleValuesException extends RuntimeException{
    public MultipleValuesException(String id) {
        super("If a function call is included, it must return only a single value [function "+id+" returns multiple values] !");
    }
}
