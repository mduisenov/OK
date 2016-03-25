package ru.ok.android.billingUtils;

import com.google.android.gms.plus.PlusShare;
import org.json.JSONException;
import org.json.JSONObject;

public class SkuDetails {
    String mDescription;
    String mItemType;
    String mJson;
    String mPrice;
    String mSku;
    String mTitle;
    String mType;

    public SkuDetails(String itemType, String jsonSkuDetails) throws JSONException {
        this.mItemType = itemType;
        this.mJson = jsonSkuDetails;
        JSONObject o = new JSONObject(this.mJson);
        this.mSku = o.optString("productId");
        this.mType = o.optString("type");
        this.mPrice = o.optString("price");
        this.mTitle = o.optString(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE);
        this.mDescription = o.optString(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_DESCRIPTION);
    }

    public String getSku() {
        return this.mSku;
    }

    public String toString() {
        return "SkuDetails:" + this.mJson;
    }
}
