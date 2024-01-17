package main.syntaxtree.enums;

public enum IOMode {
    WRITE("write"),
    WRITERETURN("writereturn"),
    READ("read");

    private String name;

    IOMode(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
