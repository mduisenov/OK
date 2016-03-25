package ru.ok.java.api.json.messages;

import org.json.JSONException;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.exceptions.ResultParsingException;
import ru.ok.java.api.json.JsonResultParser;

public final class JsonMessageEditParser extends JsonResultParser<String> {
    public JsonMessageEditParser(JsonHttpResult result) {
        super(result);
    }

    public String parse() throws ResultParsingException {
        return parse(this.result);
    }

    public static String parse(JsonHttpResult response) throws ResultParsingException {
        try {
            return response.getResultAsObject().getString("id");
        } catch (JSONException e) {
            throw new ResultParsingException(e);
        }
    }
}
