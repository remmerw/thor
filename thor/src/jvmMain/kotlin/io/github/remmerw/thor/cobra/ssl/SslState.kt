package io.github.remmerw.thor.cobra.ssl;

public enum SslState {
    NONE("None"),
    VALID("Valid"),
    INVALID("Invalid");

    private final String name;

    SslState(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
