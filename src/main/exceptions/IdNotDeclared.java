package main.exceptions;

public class IdNotDeclared extends RuntimeException{
    public IdNotDeclared(String type, String id) {
        super(type+" '" + id + "' is NOT declared!");
    }
}
