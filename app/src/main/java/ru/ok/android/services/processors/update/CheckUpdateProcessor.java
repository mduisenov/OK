package ru.ok.android.services.processors.update;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.JsonReader;
import io.github.eterverda.playless.common.Dist;
import io.github.eterverda.playless.common.Link;
import io.github.eterverda.playless.common.json.JsonDistFactory;
import io.github.eterverda.playless.lib.DistReplacementPicker;
import io.github.eterverda.playless.lib.json.AndroidJsonReader;
import java.io.StringReader;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import ru.ok.android.services.processors.settings.StartSettingsGetProcessor.SettingHandler;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.request.TranslationsRequest;

public class CheckUpdateProcessor implements SettingHandler {
    private final Context context;

    public CheckUpdateProcessor(Context context) {
        this.context = context.getApplicationContext();
    }

    public String getSettingsKey() {
        return "check.update.*";
    }

    public boolean isSettingsTimeRequestValid() {
        return isTimeToCheck();
    }

    public void handleResult(JSONObject json) {
        save(check(json));
        CheckUpdatePreferences.touchLastCheckDate(this.context);
        Logger.m173d("<<< %s", info);
    }

    private AvailableUpdateInfo check(JSONObject pmsJson) {
        Logger.m172d(">>>");
        AvailableUpdateInfo info = null;
        if (pmsJson.optBoolean("check.update.enabled", false)) {
            Dist dist = getReplacementDist(pmsJson);
            if (dist != null) {
                int versionCode = dist.version.versionCode;
                info = AvailableUpdateInfo.available(this.context, dist.version.versionCode, getText(pmsJson, versionCode), getAppUrl(dist), getRemindInterval(pmsJson, versionCode), getForceUpdate(pmsJson, versionCode));
            }
        } else {
            Logger.m172d("check update is disabled in PMS");
        }
        if (info == null) {
            info = AvailableUpdateInfo.notAvailable();
        }
        Logger.m173d("<<< %s", info);
        return info;
    }

    private int getRemindInterval(JSONObject pmsSettings, int versionCode) {
        Logger.m173d(">>> versionCode=%d", Integer.valueOf(versionCode));
        int interval = pmsSettings.optInt("check.update.remind.interval." + versionCode, -1);
        if (interval < 0) {
            interval = pmsSettings.optInt("check.update.remind.interval.default", -1);
        }
        Logger.m173d("<<< interval=%d", Integer.valueOf(interval));
        return interval;
    }

    private boolean getForceUpdate(JSONObject pmsSettings, int versionCode) {
        boolean force;
        Logger.m173d(">>> versionCode=%d", Integer.valueOf(versionCode));
        String versionKey = "check.update.force." + versionCode;
        if (pmsSettings.has(versionKey)) {
            force = pmsSettings.optBoolean(versionKey, false);
        } else {
            force = pmsSettings.optBoolean("check.update.force.default", false);
        }
        Logger.m173d("<<< force=%s", Boolean.valueOf(force));
        return force;
    }

    private String getText(JSONObject pmsSettings, int versionCode) {
        Logger.m173d(">>> versionCode=%d", Integer.valueOf(versionCode));
        String text = pmsSettings.optString("check.update.text." + versionCode, null);
        if (TextUtils.isEmpty(text)) {
            String keyForVersion = pmsSettings.optString("check.update.text.pts." + versionCode);
            String keyDeafult = pmsSettings.optString("check.update.text.pts.default");
            ArrayList<String> keys = getNonEmpty(keyForVersion, keyDeafult);
            if (keys != null) {
                try {
                    JSONObject texts = getTextsFromPts(keys);
                    if (!TextUtils.isEmpty(keyForVersion)) {
                        text = texts.optString(keyForVersion, null);
                    }
                    if (TextUtils.isEmpty(text)) {
                        text = pmsSettings.optString("check.update.text.default", null);
                    }
                    if (TextUtils.isEmpty(text) && !TextUtils.isEmpty(keyDeafult)) {
                        text = texts.optString(keyDeafult, null);
                    }
                } catch (Exception e) {
                    Logger.m180e(e, "Failed to get texts from PTS: %s", e);
                }
            }
        }
        Logger.m173d("<<< text=%s", text);
        return text;
    }

    private static ArrayList<String> getNonEmpty(String key1, String key2) {
        ArrayList<String> keys = null;
        if (!TextUtils.isEmpty(key1)) {
            if (null == null) {
                keys = new ArrayList();
            }
            keys.add(key1);
        }
        if (!TextUtils.isEmpty(key2)) {
            if (keys == null) {
                keys = new ArrayList();
            }
            keys.add(key2);
        }
        return keys;
    }

    private JSONObject getTextsFromPts(ArrayList<String> keys) throws BaseApiException, JSONException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append((String) keys.get(i));
        }
        return JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new TranslationsRequest(ConfigurationPreferences.getInstance().getLocalePackage(), null, sb)).getResultAsObject();
    }

    private void save(AvailableUpdateInfo info) {
        Editor editor = CheckUpdatePreferences.getPreferences(this.context).edit();
        info.toSharedPreferences(editor);
        editor.apply();
    }

    private boolean isTimeToCheck() {
        return System.currentTimeMillis() - CheckUpdatePreferences.getLastCheckDate(this.context) >= 86400000;
    }

    private Dist getReplacementDist(JSONObject pmsSettings) {
        Dist[] dists = getAvailableDists(pmsSettings);
        if (dists == null) {
            Logger.m184w("Information about available APKs not found in PMS settings.");
            return null;
        }
        Logger.m173d("Found %d available update APKs", Integer.valueOf(dists.length));
        Dist dist = null;
        try {
            dist = new DistReplacementPicker(this.context).bestMyReplacement(dists);
        } catch (Throwable e) {
            Logger.m180e(e, "Failed to pick best replacement APK: %s", e);
        }
        if (dist == null) {
            Logger.m172d("No replacement APK found");
            return dist;
        }
        Logger.m173d("Found replacement APK: versionCode=%d", Integer.valueOf(dist.version.versionCode));
        return dist;
    }

    private static Dist[] getAvailableDists(JSONObject pmsSettings) {
        String availableDistsJson = pmsSettings.optString("check.update.dists");
        if (!TextUtils.isEmpty(availableDistsJson)) {
            return parseDists(availableDistsJson);
        }
        Logger.m184w("check.update.dists setting is missing in PMS.");
        return null;
    }

    private static Dist[] parseDists(String jsonFromPms) {
        try {
            return new JsonDistFactory().loadDecorated(new AndroidJsonReader(new JsonReader(new StringReader(jsonFromPms))));
        } catch (Throwable e) {
            Logger.m180e(e, "Failed to parse %s setting from PMS", "check.update.dists");
            return null;
        }
    }

    private static String getAppUrl(Dist dist) {
        for (Link link : dist.links) {
            if ("store".equals(link.rel())) {
                return link.href();
            }
        }
        return null;
    }
}
