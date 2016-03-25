package ru.ok.android.fragments.web.hooks;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import java.util.HashMap;
import java.util.Map;
import ru.ok.android.utils.Logger;

public class ShortLinkParser {
    private final Map<String, String> map;

    public ShortLinkParser(@NonNull String url, @NonNull String anchor) {
        this.map = new HashMap();
        try {
            parse(url, anchor);
        } catch (Throwable e) {
            Logger.m179e(e, "can't parse short link parameters");
        }
    }

    @Nullable
    public String getValue(@NonNull String key) {
        return (String) this.map.get(key);
    }

    private void parse(@NonNull String url, @NonNull String anchor) {
        int anchorInd = url.indexOf(anchor);
        if (anchorInd != -1) {
            String[] params = url.substring(anchorInd, url.length()).split("/");
            for (int i = 0; i < params.length; i += 2) {
                String key = params[i];
                String value = params[i + 1];
                if (!(TextUtils.isEmpty(key) || TextUtils.isEmpty(value) || value.equals("null"))) {
                    this.map.put(key, value);
                }
            }
        }
    }
}
