package ru.ok.android.fragments.web.hooks;

import android.net.Uri;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import org.jivesoftware.smack.util.StringUtils;
import ru.ok.android.utils.Logger;

public class HookSessionFailedProcessor extends HookBaseProcessor {
    private OnSessionFailedListener onSessionFailedListener;

    public interface OnSessionFailedListener {
        void onSessionFailed(String str);
    }

    public HookSessionFailedProcessor(OnSessionFailedListener onSessionFailedListener) {
        this.onSessionFailedListener = onSessionFailedListener;
    }

    protected String getHookName() {
        return "/cdk/st.cmd/main/st.hookRedirect";
    }

    protected void onHookExecute(Uri uri) {
        Logger.m173d("uri=%s", uri);
        String reLogin = getReloginUrl(uri);
        if (reLogin != null) {
            notifySessionFail(reLogin);
        }
    }

    protected String getReloginUrl(Uri uri) {
        try {
            String reLoginUrl = URLDecoder.decode(HookRedirectProcessor.getRedirectUrlFromHook(uri, "st.hookRedirect"), StringUtils.UTF8);
            notifySessionFail(reLoginUrl);
            return reLoginUrl;
        } catch (UnsupportedEncodingException e) {
            Logger.m172d("Decode error");
            return null;
        }
    }

    private void notifySessionFail(String url) {
        if (this.onSessionFailedListener != null) {
            this.onSessionFailedListener.onSessionFailed(url);
        }
    }
}
