package main.exceptions;

public class IdAlreadyDeclared extends RuntimeException{
    public IdAlreadyDeclared(String type, String id) {
        super(type+" '" + id + "' is already declared!");
    }
}
