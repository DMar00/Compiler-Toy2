package main.exceptions.proc;

public class MainNotFound extends RuntimeException{
    public MainNotFound() {
        super("Undefined reference to procedure 'main'");
    }
}
