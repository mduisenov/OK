package ru.ok.android.services.processors.xmpp;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;
import org.json.JSONObject;
import ru.ok.android.services.processors.settings.StartSettingsGetProcessor.SettingHandler;
import ru.ok.android.utils.Logger;

public final class XmppSettingsHandler implements SettingHandler {
    private final Context context;

    public XmppSettingsHandler(Context context) {
        this.context = context.getApplicationContext();
    }

    public String getSettingsKey() {
        return "xmpp.*";
    }

    public boolean isSettingsTimeRequestValid() {
        return isTimeToCheck();
    }

    public void handleResult(JSONObject json) {
        try {
            save(createXmppSettingsContainer(json));
            XmppSettingsPreferences.touchLastCheckDate(this.context);
        } catch (Throwable e) {
            Logger.m186w(e, "Can't get XMPP settings");
        }
    }

    @NonNull
    private XmppSettingsContainer createXmppSettingsContainer(JSONObject pmsJson) {
        Logger.m173d("<<< %s", XmppSettingsContainer.create(pmsJson.optBoolean("xmpp.enabled", false), pmsJson.optBoolean("xmpp.push.composing", false), pmsJson.optInt("xmpp.delay.composing.composing", 10000), pmsJson.optInt("xmpp.delay.composing.paused", 5000), pmsJson.optInt("xmpp.delay.reset.paused", 3000), pmsJson.optBoolean("xmpp.push.newmessage", false), pmsJson.optBoolean("xmpp.push.messageread", false)));
        return XmppSettingsContainer.create(pmsJson.optBoolean("xmpp.enabled", false), pmsJson.optBoolean("xmpp.push.composing", false), pmsJson.optInt("xmpp.delay.composing.composing", 10000), pmsJson.optInt("xmpp.delay.composing.paused", 5000), pmsJson.optInt("xmpp.delay.reset.paused", 3000), pmsJson.optBoolean("xmpp.push.newmessage", false), pmsJson.optBoolean("xmpp.push.messageread", false));
    }

    private void save(XmppSettingsContainer info) {
        Editor editor = XmppSettingsPreferences.getPreferences(this.context).edit();
        info.toSharedPreferences(editor);
        editor.apply();
    }

    private boolean isTimeToCheck() {
        return System.currentTimeMillis() - XmppSettingsPreferences.getLastCheckDate(this.context) >= 3600000;
    }
}
