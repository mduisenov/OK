package ru.ok.java.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonHttpResult extends HttpResult {
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private String string;

    public JsonHttpResult(int httpStatus, String httpResponse) {
        super(httpStatus, httpResponse);
    }

    public JsonHttpResult(HttpResult httpResult) {
        super(httpResult.getHttpStatus(), httpResult.getHttpResponse());
    }

    public JSONObject getResultAsObject() throws JSONException {
        if (this.jsonObject == null) {
            this.jsonObject = new JSONObject(this.httpResponse);
        }
        return this.jsonObject;
    }

    public JSONArray getResultAsArray() throws JSONException {
        if (this.jsonArray == null) {
            this.jsonArray = new JSONArray(this.httpResponse);
        }
        return this.jsonArray;
    }

    public String getResultAsString() throws JSONException {
        if (this.string == null) {
            this.string = new JSONTokener(this.httpResponse).nextValue().toString();
        }
        return this.string;
    }
}
