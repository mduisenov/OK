package ru.ok.android.ui.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.Looper;
import ru.ok.android.utils.localization.LocalizationManager;

public class ErrorMusicDialog extends ErrorReplaceDialog {
    private Handler handler;
    private OnNextTrackListener listener;

    /* renamed from: ru.ok.android.ui.dialogs.ErrorMusicDialog.1 */
    class C07711 implements Runnable {
        C07711() {
        }

        public void run() {
            if (ErrorMusicDialog.this.dialog.isShowing()) {
                ErrorMusicDialog.this.dialog.dismiss();
            }
            if (ErrorMusicDialog.this.listener != null) {
                ErrorMusicDialog.this.listener.onNextTrack();
            }
        }
    }

    private class NextHandler implements OnClickListener {
        private NextHandler() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (ErrorMusicDialog.this.listener != null) {
                ErrorMusicDialog.this.listener.onNextTrack();
            }
            ErrorMusicDialog.this.handler.removeCallbacksAndMessages(null);
            ErrorMusicDialog.this.dialog.dismiss();
        }
    }

    public interface OnNextTrackListener {
        void onNextTrack();
    }

    public ErrorMusicDialog(Context context, String name) {
        super(context, name, LocalizationManager.getString(context, 2131165650), LocalizationManager.getString(context, 2131165476), LocalizationManager.getString(context, 2131166256), false, false);
        this.handler = new Handler(Looper.getMainLooper());
        setOnReplaceButtonClickListener(new NextHandler());
    }

    public void show() {
        super.show();
        this.handler.postDelayed(new C07711(), 5000);
    }

    public void dismiss() {
        this.dialog.dismiss();
        this.handler.removeCallbacksAndMessages(null);
    }

    public void setOnNextTrackListener(OnNextTrackListener onNextTrackListener) {
        this.listener = onNextTrackListener;
    }
}
