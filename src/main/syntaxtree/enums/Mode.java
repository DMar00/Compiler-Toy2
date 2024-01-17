package main.syntaxtree.enums;

public enum Mode {
    OUT("out"),
    INOUT("inout"),
    IN("in");

    private String name;

    Mode(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
