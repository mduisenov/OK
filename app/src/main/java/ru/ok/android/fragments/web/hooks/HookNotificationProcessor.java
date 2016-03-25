package ru.ok.android.fragments.web.hooks;

import android.net.Uri;
import android.text.TextUtils;
import ru.ok.android.utils.Logger;

public class HookNotificationProcessor extends HookBaseProcessor {
    private OnNotificationCountUpdateListener listener;

    public interface OnNotificationCountUpdateListener {
        void onNotificationCountUpdate(int i);

        void onNotificationTotalUpdate(int i);
    }

    public HookNotificationProcessor(OnNotificationCountUpdateListener listener) {
        this.listener = listener;
    }

    protected String getHookName() {
        return "/apphook/notifications";
    }

    protected void onHookExecute(Uri uri) {
        onHookExecuteCount(uri);
        onHookExecuteTotal(uri);
    }

    private void onHookExecuteCount(Uri uri) {
        String paramCount = uri.getQueryParameter("count");
        if (!TextUtils.isEmpty(paramCount)) {
            try {
                notifyUpdateCount(Integer.valueOf(paramCount).intValue());
            } catch (NumberFormatException ex) {
                Logger.m172d("NFE: " + ex);
            }
        }
    }

    private void onHookExecuteTotal(Uri uri) {
        String paramTotal = uri.getQueryParameter("total");
        if (!TextUtils.isEmpty(paramTotal)) {
            try {
                notifyUpdateTotal(Integer.valueOf(paramTotal).intValue());
            } catch (NumberFormatException ex) {
                Logger.m172d("NFE: " + ex);
            }
        }
    }

    private void notifyUpdateCount(int count) {
        if (this.listener != null) {
            this.listener.onNotificationCountUpdate(count);
        }
    }

    private void notifyUpdateTotal(int total) {
        if (this.listener != null) {
            this.listener.onNotificationTotalUpdate(total);
        }
    }
}
