package main.exceptions;

public class MainNotFound extends RuntimeException{
    public MainNotFound() {
        super("Undefined reference to procedure 'main'");
    }
}
