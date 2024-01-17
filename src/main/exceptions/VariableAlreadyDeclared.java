package main.exceptions;

public class VariableAlreadyDeclared extends RuntimeException{
    public VariableAlreadyDeclared(String id) {
        super("Variable '" + id + "'is already declared!");
    }
}
