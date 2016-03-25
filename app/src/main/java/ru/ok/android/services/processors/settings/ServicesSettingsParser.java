package ru.ok.android.services.processors.settings;

import android.content.SharedPreferences.Editor;
import java.util.Iterator;
import org.json.JSONObject;
import ru.ok.android.utils.Logger;

public final class ServicesSettingsParser {
    public static void parse(JSONObject obj, Editor out) {
        Iterator<String> names = obj.keys();
        if (names != null) {
            while (names.hasNext()) {
                String name = (String) names.next();
                if (name != null) {
                    if (name.startsWith("multi.chat.")) {
                        parseInt(name, obj, out);
                    } else if (name.startsWith("message.")) {
                        parseLong(name, obj, out);
                    } else if ("audio.attach.recording.max.duration".equals(name) || "video.attach.recording.max.duration".equals(name)) {
                        parseInt(name, obj, out);
                    }
                }
            }
        }
    }

    private static void parseInt(String name, JSONObject obj, Editor out) {
        if (!obj.isNull(name)) {
            try {
                out.putInt(name, Integer.parseInt(obj.optString(name)));
            } catch (NumberFormatException e) {
                Logger.m185w("Unsupported setting: %s=%s", name, strValue);
                out.remove(name);
            }
        }
    }

    private static void parseLong(String name, JSONObject obj, Editor out) {
        if (!obj.isNull(name)) {
            try {
                out.putLong(name, Long.parseLong(obj.optString(name)));
            } catch (NumberFormatException e) {
                Logger.m185w("Unsupported setting: %s=%s", name, strValue);
                out.remove(name);
            }
        }
    }
}
