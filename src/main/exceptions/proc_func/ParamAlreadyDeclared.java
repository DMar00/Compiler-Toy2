package main.exceptions.proc_func;

public class ParamAlreadyDeclared extends RuntimeException{
    public ParamAlreadyDeclared(String param, String id, String type) {
        super("Error: redefinition of parameter " + param + " for "+type+" "+id+"!");
    }
}
