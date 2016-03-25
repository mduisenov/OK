package ru.ok.android.ui.activity.main;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import ru.ok.android.fragments.web.hooks.WebLinksProcessor.ManageCallback;
import ru.ok.android.fragments.web.hooks.WebLinksProcessor.ValidateTask;
import ru.ok.android.onelog.AppLaunchLog;
import ru.ok.android.ui.activity.BaseActivity;
import ru.ok.android.utils.Logger;

public class LinksActivity extends BaseActivity implements OnCancelListener, ManageCallback {
    private final Handler handler;
    private ProgressDialog progressDialog;
    private ValidateTask task;

    /* renamed from: ru.ok.android.ui.activity.main.LinksActivity.1 */
    class C05741 implements Runnable {
        C05741() {
        }

        public void run() {
            LinksActivity.this.showProgressDialog();
        }
    }

    public LinksActivity() {
        this.handler = new Handler();
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        Logger.m172d("");
        super.onCreateLocalized(savedInstanceState);
        getIntent().putExtra("LinksActivity.EXTRA_SHORTLINK", true);
        if (savedInstanceState == null && !startLoginIfNeeded()) {
            onIntent(getIntent());
        }
    }

    protected void onDestroy() {
        Logger.m172d("");
        super.onDestroy();
        this.handler.removeCallbacksAndMessages(null);
    }

    public void onContinueProcess() {
        Logger.m172d("");
        this.handler.postDelayed(new C05741(), 500);
    }

    public void onFinishProcess() {
        Logger.m172d("calling finish()");
        if (!(this.task == null || this.task.isCancel() || getIntent().hasExtra("extra_push_delivery_type"))) {
            AppLaunchLog.shortLinkInternal();
        }
        if (this.progressDialog != null) {
            this.progressDialog.dismiss();
        }
        finish();
    }

    public void onCreateTask(ValidateTask task) {
        Logger.m172d("");
        this.task = task;
    }

    public void onCancel(DialogInterface dialogInterface) {
        Logger.m172d("calling finish()");
        this.progressDialog = null;
        if (this.task != null) {
            this.task.cancel();
        }
        finish();
    }

    private void onIntent(Intent intent) {
        Logger.m172d("");
        String url = intent.getData() != null ? String.valueOf(intent.getData()) : null;
        if (TextUtils.isEmpty(url)) {
            finish();
        } else {
            handleUrl(url);
        }
    }

    private void handleUrl(String url) {
        Logger.m173d("url=%s", url);
        url = url.replace("odnoklassniki://", "http://");
        try {
            String linkUrl = Uri.parse(url).getQueryParameter("st.link");
            String stCmd = Uri.parse(url).getQueryParameter("st.cmd");
            if (!(linkUrl == null || stCmd == null || !stCmd.equals("logExternal"))) {
                url = linkUrl;
            }
        } catch (Exception e) {
            Logger.m176e(e.toString());
        }
        getWebLinksProcessor().processUrl(url, this);
    }

    private void showProgressDialog() {
        if (this.progressDialog == null) {
            Logger.m172d("");
            this.progressDialog = new ProgressDialog(this);
            this.progressDialog.setMessage(getStringLocalized(2131165914));
            this.progressDialog.setOnCancelListener(this);
            this.progressDialog.show();
        }
    }
}
