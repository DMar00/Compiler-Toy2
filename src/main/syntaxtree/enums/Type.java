package main.syntaxtree.enums;

public enum Type {
    REAL("real"),
    INTEGER("integer"),
    STRING("string"),
    BOOLEAN("boolean");

    public String name;

    Type(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
