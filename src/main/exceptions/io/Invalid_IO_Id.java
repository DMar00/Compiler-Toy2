package main.exceptions.io;

public class Invalid_IO_Id extends RuntimeException{
    public Invalid_IO_Id() {
        super("The value in the @ must be a variable ID!");
    }
}
