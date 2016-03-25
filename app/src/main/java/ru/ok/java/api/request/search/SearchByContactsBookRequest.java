package ru.ok.java.api.request.search;

import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.ok.java.api.HttpMethodType;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.model.ContactInfo;

@HttpPreamble(httpType = HttpMethodType.POST)
public class SearchByContactsBookRequest extends BaseRequest {
    private ArrayList<ContactInfo> contacts;

    public String getMethodName() {
        return "search.byContactsBook";
    }

    public SearchByContactsBookRequest(ArrayList<ContactInfo> contacts) {
        this.contacts = contacts;
    }

    public String getQueryString() {
        String credentials = "";
        if (this.contacts == null) {
            return credentials;
        }
        JSONArray jsonArray = new JSONArray();
        Iterator i$ = this.contacts.iterator();
        while (i$.hasNext()) {
            jsonArray.put(((ContactInfo) i$.next()).getJsonObject());
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("credentials", jsonArray);
            credentials = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return credentials;
    }

    protected void serializeInternal(RequestSerializer<?> serializer) throws SerializeException {
        serializer.add(SerializeParamName.QUERY, getQueryString());
        serializer.add(SerializeParamName.FIELDS, "user.uid,user.first_name,user.last_name,user.gender,user.online,user.pic128x128,user.pic190x190");
    }
}
