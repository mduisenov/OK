package ru.ok.android.ui.presents.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.widget.Toast;
import com.google.android.libraries.cast.companionlibrary.C0158R;
import ru.ok.android.ui.activity.BaseActivity;
import ru.ok.android.ui.custom.imageview.CircledBorderDrawable;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;

public class BasePresentActivity extends BaseActivity implements OnCancelListener {
    private final Handler handler;
    private ProgressDialog progressDialog;

    /* renamed from: ru.ok.android.ui.presents.activity.BasePresentActivity.1 */
    class C11461 implements Runnable {
        C11461() {
        }

        public void run() {
            BasePresentActivity.this.showProgressDialog();
        }
    }

    public BasePresentActivity() {
        this.handler = new Handler();
    }

    protected boolean isSupportToolbarVisible() {
        return false;
    }

    public void onCancel(DialogInterface dialogInterface) {
        finish();
    }

    protected void onDestroy() {
        super.onDestroy();
        this.handler.removeCallbacksAndMessages(null);
    }

    protected void setCircledBackground() {
        Bitmap repeatBitmap = BitmapFactory.decodeResource(getResources(), 2130838730);
        CircledBorderDrawable drawable = new CircledBorderDrawable(this);
        drawable.setBackground(repeatBitmap);
        findViewById(2131624511).setBackgroundDrawable(drawable);
    }

    protected void showProgressDialogDelayed() {
        this.handler.postDelayed(new C11461(), 500);
    }

    protected void showProgressDialog() {
        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setMessage(getStringLocalized(2131166047));
        this.progressDialog.setIndeterminate(true);
        this.progressDialog.setCancelable(true);
        this.progressDialog.setOnCancelListener(this);
        this.progressDialog.show();
    }

    protected void hideProgressDialog() {
        this.handler.removeCallbacksAndMessages(null);
        if (this.progressDialog != null) {
            this.progressDialog.dismiss();
            this.progressDialog = null;
        }
    }

    protected void showToast(int textResId) {
        Toast.makeText(this, textResId, 0).show();
    }

    protected void showButtonsProgress() {
        findViewById(C0158R.id.buttons).setVisibility(4);
        findViewById(2131624520).setVisibility(0);
    }

    protected void hideButtonsProgress() {
        findViewById(2131624520).setVisibility(4);
    }

    protected void showButtons() {
        findViewById(C0158R.id.buttons).setVisibility(0);
        findViewById(2131624520).setVisibility(4);
    }

    protected int getUserAvatarStub(@NonNull UserInfo userInfo) {
        if (userInfo.genderType == UserGenderType.MALE) {
            return 2130837654;
        }
        return 2130837657;
    }
}
