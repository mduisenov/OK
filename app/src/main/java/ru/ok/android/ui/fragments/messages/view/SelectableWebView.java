package ru.ok.android.ui.fragments.messages.view;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.webkit.WebView;
import com.google.android.gms.plus.PlusShare;

public class SelectableWebView extends WebView implements OnLongClickListener {
    private final Handler handler;

    /* renamed from: ru.ok.android.ui.fragments.messages.view.SelectableWebView.1 */
    class C09031 extends Handler {
        C09031() {
        }

        public void handleMessage(Message msg) {
            if (msg.getData().getString(PlusShare.KEY_CALL_TO_ACTION_URL) == null) {
                SelectableWebView.this.selectAndCopyText();
            }
        }
    }

    public SelectableWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.handler = new C09031();
        setOnLongClickListener(this);
    }

    public boolean onLongClick(View v) {
        Message hrefMsg = Message.obtain(this.handler);
        if (hrefMsg != null) {
            requestImageRef(hrefMsg);
        }
        return VERSION.SDK_INT < 11;
    }

    public void selectAndCopyText() {
        try {
            if (VERSION.SDK_INT < 11) {
                WebView.class.getMethod("emulateShiftHeld", null).invoke(this, null);
            }
        } catch (Exception e) {
            new KeyEvent(0, 0, 0, 59, 0, 0).dispatch(this);
        }
    }
}
