package ru.ok.java.api.request.paging;

public enum PagingDirection {
    FORWARD("FORWARD"),
    BACKWARD("BACKWARD");
    
    private final String _value;

    private PagingDirection(String value) {
        this._value = value;
    }

    public String getValue() {
        return this._value;
    }
}
