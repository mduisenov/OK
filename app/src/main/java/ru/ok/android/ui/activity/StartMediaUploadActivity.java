package ru.ok.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import ru.ok.android.ui.dialogs.AlertFragmentDialog.OnAlertDismissListener;
import ru.ok.android.ui.fragments.SaveToFileFragment.SaveToFileFragmentListener;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.MediaUploadUtils;
import ru.ok.android.utils.Storage.External;

public abstract class StartMediaUploadActivity extends BaseActivity implements OnAlertDismissListener, SaveToFileFragmentListener {
    protected abstract void copyMediaForUpload();

    protected abstract void doStartMediaUpload();

    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        if (savedInstanceState == null && !External.externalMemoryAvailable()) {
            MediaUploadUtils.showAlert(this, null, 2131165874, 2131165873, 1);
        }
    }

    protected void onResumeFragments() {
        super.onResumeFragments();
        MediaUploadUtils.onResume(getSupportFragmentManager(), this);
    }

    public void onAlertDismiss(int requestCode) {
        Logger.m173d("requestCode=%d", Integer.valueOf(requestCode));
        if (requestCode == 1) {
            finish();
        } else if (requestCode == 2) {
            MediaUploadUtils.onCopyProgressCancelled(this, null, 1);
        }
    }

    protected final void startMediaUpload(@NonNull Intent data) {
        if (shouldCopyMediaForUpload(data)) {
            copyMediaForUpload();
            return;
        }
        doStartMediaUpload();
        finish();
    }

    protected boolean shouldCopyMediaForUpload(@NonNull Intent data) {
        return (data.getFlags() & 1) == 1;
    }
}
