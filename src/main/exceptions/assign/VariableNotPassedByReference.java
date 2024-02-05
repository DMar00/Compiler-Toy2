package main.exceptions.assign;

public class VariableNotPassedByReference extends RuntimeException{
    public VariableNotPassedByReference(String id) {
        super("Attempted to modify the variable "+id+" not passed by reference!");
    }
}
