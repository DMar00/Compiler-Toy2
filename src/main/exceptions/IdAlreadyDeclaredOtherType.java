package main.exceptions;

public class IdAlreadyDeclaredOtherType extends RuntimeException{
    public IdAlreadyDeclaredOtherType(String type, String id , String type2) {
        super(type +" '" + id + "' is already declared  with type "+type2+"!");
    }
}
