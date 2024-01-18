package main.table;

public enum SymbolItemType {
    FUNCTION("function"),
    VARIABLE("variable"),
    PROCEDURE("procedure");

    private String name;

    SymbolItemType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
