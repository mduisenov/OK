package ru.ok.model;

import org.json.JSONArray;
import org.json.JSONObject;

public class Relative {
    public final String subtypeId;
    public final String typeId;
    public final String[] uids;

    public Relative(JSONObject jsonObject) {
        this.typeId = jsonObject.optString("type_id");
        this.subtypeId = jsonObject.optString("subtype_id");
        JSONArray jsonArray = jsonObject.optJSONArray("uids");
        if (jsonArray != null) {
            this.uids = new String[jsonArray.length()];
            for (int i = 0; i < this.uids.length; i++) {
                this.uids[i] = jsonArray.optString(i);
            }
            return;
        }
        this.uids = new String[0];
    }

    public Relative(String uid, String typeId, String subTypeId) {
        this.uids = new String[]{uid};
        this.typeId = typeId;
        this.subtypeId = subTypeId;
    }
}
