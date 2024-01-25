package main.exceptions.proc;

public class UnexpectedReturn extends RuntimeException{
    public UnexpectedReturn(String name) {
        super("Error: unexpected 'return' statement encountered in '"+name+"' !");
    }
}
