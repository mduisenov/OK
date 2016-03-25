package ru.ok.java.api.json;

import ru.ok.java.api.exceptions.ResultParsingException;

public interface JsonParser<T> {
    T parse() throws ResultParsingException;
}
