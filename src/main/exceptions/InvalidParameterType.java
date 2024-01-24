package main.exceptions;

import main.syntaxtree.enums.Type;

public class InvalidParameterType extends RuntimeException{
    public InvalidParameterType(String type, String id, String expected, String provided) {
        super("Error: "+type+" "+id+" expected parameter of type "+expected+", but received parameter of type "+provided+" !");
    }
}
