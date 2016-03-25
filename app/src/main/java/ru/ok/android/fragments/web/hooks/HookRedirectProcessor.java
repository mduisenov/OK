package ru.ok.android.fragments.web.hooks;

import android.net.Uri;
import android.support.annotation.NonNull;
import java.util.List;
import ru.ok.android.utils.Logger;

public class HookRedirectProcessor extends HookBaseProcessor {
    private OnRedirectUrlLoadingListener onRedirectUrlLoadingListener;

    public interface OnRedirectUrlLoadingListener {
        void onRedirectUrlLoading(String str);
    }

    public HookRedirectProcessor(OnRedirectUrlLoadingListener onRedirectUrlLoadingListener) {
        this.onRedirectUrlLoadingListener = onRedirectUrlLoadingListener;
    }

    protected String getHookName() {
        return "/cdk/st.cmd/main/st.redirect";
    }

    protected void onHookExecute(Uri uri) {
        Logger.m173d("uri=%s", uri);
        notifyLoadingListenerOnRedirectUrl(getRedirectUrl(uri));
    }

    private static String getRedirectUrl(Uri uri) {
        return getRedirectUrlFromHook(uri, "st.redirect");
    }

    private void notifyLoadingListenerOnRedirectUrl(String redirectUrl) {
        if (this.onRedirectUrlLoadingListener != null) {
            this.onRedirectUrlLoadingListener.onRedirectUrlLoading(redirectUrl);
        }
    }

    @NonNull
    static String getRedirectUrlFromHook(Uri uri, String lastHookSegment) {
        String redirectUrl = "";
        List<String> segments = uri.getPathSegments();
        if (segments == null) {
            return redirectUrl;
        }
        int i = 0;
        int size = segments.size();
        while (i < size) {
            if (!lastHookSegment.equals(segments.get(i))) {
                i++;
            } else if (i + 1 < size) {
                return (String) segments.get(i + 1);
            } else {
                return redirectUrl;
            }
        }
        return redirectUrl;
    }
}
