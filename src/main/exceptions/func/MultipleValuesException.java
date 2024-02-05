package main.exceptions.func;

public class MultipleValuesException extends RuntimeException{
    public MultipleValuesException(String id) {
        super("The function "+id+" returns multiple values when only one is expected !");
    }
}
