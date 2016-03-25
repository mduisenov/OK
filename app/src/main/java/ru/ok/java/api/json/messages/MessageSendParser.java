package ru.ok.java.api.json.messages;

import org.jivesoftware.smack.packet.Stanza;
import org.json.JSONException;
import org.json.JSONObject;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.exceptions.ResultParsingException;
import ru.ok.java.api.response.MessageSendResponse;
import ru.ok.java.api.utils.JsonUtil;

public class MessageSendParser {
    public static MessageSendResponse parse(JsonHttpResult response) throws ResultParsingException {
        try {
            JSONObject result = response.getResultAsObject();
            String serverId = JsonUtil.getStringSafely(result, "id");
            String text = null;
            if (result.has(Stanza.TEXT)) {
                text = !result.isNull(Stanza.TEXT) ? result.getString(Stanza.TEXT) : "";
            }
            return new MessageSendResponse(serverId, text, result.optLong("create_date_ms"));
        } catch (JSONException e) {
            throw new ResultParsingException(e);
        }
    }
}
