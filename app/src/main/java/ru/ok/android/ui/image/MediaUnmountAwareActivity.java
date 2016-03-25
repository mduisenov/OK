package ru.ok.android.ui.image;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import ru.ok.android.ui.activity.BaseActivity;

public abstract class MediaUnmountAwareActivity extends BaseActivity {
    private final BroadcastReceiver receiver;
    protected boolean unmountAwareEnabled;

    /* renamed from: ru.ok.android.ui.image.MediaUnmountAwareActivity.1 */
    class C09581 extends BroadcastReceiver {
        C09581() {
        }

        public void onReceive(Context context, Intent intent) {
            if (MediaUnmountAwareActivity.this.unmountAwareEnabled && "android.intent.action.MEDIA_UNMOUNTED".equals(intent.getAction())) {
                MediaUnmountAwareActivity.this.showErrorDialog(MediaUnmountAwareActivity.this.getStringLocalized(2131166093));
            }
        }
    }

    public MediaUnmountAwareActivity() {
        this.unmountAwareEnabled = true;
        this.receiver = new C09581();
    }

    protected void onResume() {
        super.onResume();
        if (Environment.getExternalStorageState().equals("mounted")) {
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.MEDIA_UNMOUNTED");
            registerReceiver(this.receiver, filter);
        } else if (this.unmountAwareEnabled) {
            showErrorDialog(getStringLocalized(2131166093));
        }
    }

    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(this.receiver);
        } catch (Exception e) {
        }
    }

    public final void setUnmountAware(boolean enabled) {
        this.unmountAwareEnabled = enabled;
    }
}
