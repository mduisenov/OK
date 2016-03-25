package ru.ok.android.utils;

import android.content.Context;
import org.jivesoftware.smack.util.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class AdvertisingUtils {

    static class AdvertisingInformation {
        public static JSONObject getInfoObj(Context context) throws JSONException {
            JSONObject infoObj = new JSONObject();
            JSONObject dataObj = new JSONObject();
            JSONObject osObj = new JSONObject();
            osObj.put("name", "Android");
            osObj.put("version", DeviceUtils.getOSVersion());
            dataObj.put("deviceId", DeviceUtils.getSystemDeviceId(context));
            dataObj.put("trackId", DeviceUtils.getTrackId(context));
            dataObj.put("os", osObj);
            infoObj.put("devData", dataObj);
            return infoObj;
        }
    }

    public static String getInfo(Context context) {
        try {
            Logger.m173d("information value = %s", AdvertisingInformation.getInfoObj(context).toString());
            return Base64.encodeBytes(AdvertisingInformation.getInfoObj(context).toString().getBytes(StringUtils.UTF8));
        } catch (Exception e) {
            return null;
        }
    }
}
