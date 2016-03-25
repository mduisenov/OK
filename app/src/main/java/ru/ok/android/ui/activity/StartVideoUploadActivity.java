package ru.ok.android.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat.IntentReader;
import android.text.TextUtils;
import java.io.File;
import ru.ok.android.onelog.AppLaunchLog;
import ru.ok.android.onelog.AppLaunchLogHelper;
import ru.ok.android.services.processors.video.FileLocation;
import ru.ok.android.services.processors.video.MediaInfo;
import ru.ok.android.services.processors.video.MediaInfoTempFile;
import ru.ok.android.services.processors.video.VideoUploadController;
import ru.ok.android.ui.fragments.SaveToFileFragment;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.MediaUploadUtils;

public class StartVideoUploadActivity extends StartMediaUploadActivity {
    private MediaInfo mediaInfo;

    public static void startVideoUpload(Context context, String groupId) {
        Intent intent = new Intent(context, StartVideoUploadActivity.class);
        intent.putExtra("groupId", groupId);
        context.startActivity(intent);
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        setContentView(2130903196);
        if (AppLaunchLogHelper.isShareIntent(getIntent())) {
            AppLaunchLog.shareVideo();
        }
        initSate(savedInstanceState);
        performAction();
    }

    private void performAction() {
        if (this.mediaInfo != null) {
            startMediaUpload(getIntent());
        } else {
            startPickerActivity();
        }
    }

    private void initSate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.mediaInfo = (MediaInfo) savedInstanceState.getParcelable("saveMedia");
        } else {
            this.mediaInfo = getOrCreateMediaInfoFromIntent();
        }
    }

    @Nullable
    private MediaInfo getOrCreateMediaInfoFromIntent() {
        MediaInfo mediaInfo = (MediaInfo) getIntent().getParcelableExtra("media_info");
        if (mediaInfo == null) {
            return getMediaInfoFromShareIntent();
        }
        return mediaInfo;
    }

    private MediaInfo getMediaInfoFromShareIntent() {
        IntentReader intentReader = IntentReader.from(this);
        int streamCount = intentReader.getStreamCount();
        if (streamCount == 0) {
            return null;
        }
        MediaInfo mediaInfo = null;
        for (int i = 0; i < streamCount; i++) {
            mediaInfo = MediaInfo.fromUri(this, intentReader.getStream(i), "video-" + System.currentTimeMillis());
        }
        return mediaInfo;
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.mediaInfo != null) {
            outState.putParcelable("saveMedia", this.mediaInfo);
        }
    }

    protected void onResume() {
        super.onResume();
        setProgressBarIndeterminateVisibility(false);
        setProgressBarVisibility(false);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1310) {
            MediaInfo mediaInfo = MediaInfo.fromUri(this, data == null ? null : data.getData(), "video-" + System.currentTimeMillis());
            Logger.m173d("videoUri=%s", videoUri);
            Logger.m173d("mediaInfo=%s", mediaInfo);
            if (resultCode != -1 || mediaInfo == null) {
                finish();
                return;
            }
            this.mediaInfo = mediaInfo;
            startMediaUpload(data);
        }
    }

    protected boolean shouldCopyMediaForUpload(@NonNull Intent data) {
        return super.shouldCopyMediaForUpload(data) || !this.mediaInfo.isPersistent();
    }

    protected void copyMediaForUpload() {
        MediaUploadUtils.startCopyFile(this, null, true, 2, MediaUploadUtils.createSaveToFileVideoFragment(this, this.mediaInfo, null), this);
    }

    protected void doStartMediaUpload() {
        startUploadVideo(this.mediaInfo);
    }

    private void startPickerActivity() {
        Intent startChooseVideo = new Intent("android.intent.action.GET_CONTENT");
        startChooseVideo.setType("video/*");
        startActivityForResult(Intent.createChooser(startChooseVideo, getString(2131166354)), 1310);
    }

    private void startUploadVideo(MediaInfo mediaInfo) {
        if (!startLoginIfNeeded()) {
            String groupIdStr = getIntent().getStringExtra("groupId");
            if (TextUtils.isEmpty(groupIdStr)) {
                VideoUploadController.startVideoUploadTaskForUser(this, mediaInfo);
            } else {
                VideoUploadController.startVideoUploadTaskForGroup(this, mediaInfo, groupIdStr);
            }
        }
    }

    public void onSaveToFileFinished(SaveToFileFragment fragment, boolean successful, Bundle additionalArgs) {
        if (successful) {
            File videoFile = fragment.getDestFile(0);
            this.mediaInfo = new MediaInfoTempFile(FileLocation.createFromExternalFile(videoFile), FileLocation.createFromExternalFile(fragment.getDestFile(1)), this.mediaInfo.getDisplayName(), videoFile.length());
            getIntent().putExtra("media_info", this.mediaInfo);
            MediaUploadUtils.hideDialogs(getSupportFragmentManager(), fragment);
            doStartMediaUpload();
            finish();
            return;
        }
        MediaUploadUtils.showAlert(this, null, 2131166095, 2131166832, 1);
    }
}
