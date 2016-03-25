package ru.ok.android.fragments.web.hooks.hooklinks;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import ru.ok.android.fragments.web.hooks.ShortLinkParser;
import ru.ok.android.fragments.web.hooks.ShortLinkUtils;
import ru.ok.android.ui.presents.helpers.PresentSettingsHelper;

public class ShortLinkSendPresentProcessor extends ShortLinkBaseProcessor {
    private final OnSendPresentListener listener;

    public interface OnSendPresentListener {
        void onSendPresent(@NonNull String str, @Nullable String str2, @Nullable String str3, @Nullable String str4);
    }

    public ShortLinkSendPresentProcessor(@NonNull OnSendPresentListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "sendPresent";
    }

    protected boolean isUriMatches(@NonNull Uri uri) {
        return super.isUriMatches(uri) && PresentSettingsHelper.getSettings().nativeSendEnabled;
    }

    protected void onHookExecute(@NonNull Uri uri) {
        if (this.listener != null) {
            ShortLinkParser parser = new ShortLinkParser(uri.toString(), getHookName());
            String presentId = ShortLinkUtils.extractId(parser.getValue("sendPresent"), true);
            String userId = ShortLinkUtils.extractId(parser.getValue("user"), true);
            String holidayId = ShortLinkUtils.extractId(parser.getValue("holiday"), true);
            String token = parser.getValue("tkn");
            if (presentId != null) {
                this.listener.onSendPresent(presentId, userId, holidayId, token);
            }
        }
    }
}
