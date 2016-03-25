package ru.ok.java.api.json;

import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.exceptions.ResultParsingException;
import ru.ok.java.api.utils.Logger;

public abstract class JsonResultParser<T> implements JsonParser<T> {
    protected Logger logger;
    protected JsonHttpResult result;

    public abstract T parse() throws ResultParsingException;

    public JsonResultParser(JsonHttpResult result) {
        this.logger = new Logger(getClass());
        this.result = result;
    }
}
