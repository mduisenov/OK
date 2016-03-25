package ru.ok.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import java.util.concurrent.atomic.AtomicInteger;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.services.processors.mediatopic.MediaTopicPostState;
import ru.ok.android.services.processors.mediatopic.PostMediaTopicTask;
import ru.ok.android.statistics.mediacomposer.MediaComposerStats;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerData;
import ru.ok.android.ui.fragments.MediaTopicStatusFragment;
import ru.ok.android.ui.fragments.MediaTopicStatusFragment.MediaTopicStatusFragmentListener;
import ru.ok.android.utils.ActivityUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.localization.base.LocalizedActivity;
import ru.ok.android.utils.settings.ServicesSettingsHelper;
import ru.ok.java.api.request.mediatopic.MediaTopicType;
import ru.ok.java.api.response.ServicesSettings;

public class MediaTopicStatusActivity extends LocalizedActivity implements MediaTopicStatusFragmentListener {
    private static final AtomicInteger instanceCount;
    private final int instanceNum;
    protected MediaTopicStatusFragment mediaTopicStatusFragment;

    public MediaTopicStatusActivity() {
        this.instanceNum = instanceCount.incrementAndGet();
    }

    static {
        instanceCount = new AtomicInteger(0);
    }

    public void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(2);
        super.onCreate(savedInstanceState);
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        Logger.m173d("[%d]", Integer.valueOf(this.instanceNum));
        setContentView(2130903308);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            PostMediaTopicTask mediaTopicTask = (PostMediaTopicTask) intent.getParcelableExtra("media_topic_post");
            boolean doCancel = intent.getBooleanExtra("cancel", false);
            if (mediaTopicTask == null) {
                Logger.m176e("Required extra not specified: media_topic_post");
                finish();
                return;
            }
            this.mediaTopicStatusFragment = addMediaTopicStatusFragment(getSupportFragmentManager(), mediaTopicTask, doCancel);
        }
        if (this.mediaTopicStatusFragment == null) {
            this.mediaTopicStatusFragment = (MediaTopicStatusFragment) getSupportFragmentManager().findFragmentByTag("mediatopic_status_fragment");
        }
        this.mediaTopicStatusFragment.setListener(this);
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    protected void onResume() {
        super.onResume();
        setProgressBarIndeterminateVisibility(false);
        setProgressBarVisibility(false);
    }

    protected void onNewIntent(Intent intent) {
        Logger.m173d("[%d]", Integer.valueOf(this.instanceNum));
        super.onNewIntent(intent);
        PostMediaTopicTask mediaTopicTask = (PostMediaTopicTask) intent.getParcelableExtra("media_topic_post");
        boolean doCancel = intent.getBooleanExtra("cancel", false);
        if (mediaTopicTask == null) {
            Logger.m176e("Required extra not specified: " + mediaTopicTask);
            return;
        }
        reportStatEventOpen(intent, mediaTopicTask);
        FragmentManager fragmentManager = getSupportFragmentManager();
        MediaTopicStatusFragment fragment = (MediaTopicStatusFragment) fragmentManager.findFragmentByTag("mediatopic_status_fragment");
        if (fragment == null) {
            fragment = addMediaTopicStatusFragment(fragmentManager, mediaTopicTask, doCancel);
            return;
        }
        PostMediaTopicTask previousTask = fragment.getTask();
        if (previousTask == null || previousTask.getId() == mediaTopicTask.getId()) {
            if (fragment.getMode() != 1) {
                fragment.resetExtraErrorIsAcknowledged();
            }
        } else if (fragment.getMode() != 1) {
            fragmentManager.beginTransaction().replace(C0263R.id.container, MediaTopicStatusFragment.newInstance(mediaTopicTask, doCancel, ActivityUtils.getMeta(this, getComponentName())), "mediatopic_status_fragment").commit();
        }
    }

    private void reportStatEventOpen(Intent intent, PostMediaTopicTask task) {
        MediaTopicPostState state = (MediaTopicPostState) intent.getParcelableExtra("upload_state");
        if (state != null) {
            MediaComposerStats.openStatus(state);
        }
    }

    private MediaTopicStatusFragment addMediaTopicStatusFragment(FragmentManager fragmentManager, PostMediaTopicTask mediaTopicTask, boolean doCancel) {
        Bundle extras = ActivityUtils.getMeta(this, getComponentName());
        ServicesSettings settings = ServicesSettingsHelper.getServicesSettings();
        MediaTopicType type;
        if (mediaTopicTask == null) {
            type = MediaTopicType.USER;
        } else {
            type = mediaTopicTask.getMediaTopicType();
        }
        MediaTopicStatusFragment fragment = MediaTopicStatusFragment.newInstance(mediaTopicTask, doCancel, extras);
        fragmentManager.beginTransaction().replace(C0263R.id.container, fragment, "mediatopic_status_fragment").commit();
        return fragment;
    }

    public void onBackPressed() {
        Logger.m173d("[%d]", Integer.valueOf(this.instanceNum));
        if (this.mediaTopicStatusFragment.getMode() == 1) {
            this.mediaTopicStatusFragment.cancelEdit();
        } else {
            super.onBackPressed();
        }
    }

    public void onMediaComposerCompleted(MediaComposerData data) {
        Logger.m173d("%s", data);
    }

    public void onProgressChanged(int progress, int maxProgress) {
        if (maxProgress != 10000) {
            progress = (progress * 10000) / maxProgress;
        }
        setProgress(progress);
    }

    public void onCancelledUpload() {
        Logger.m172d("");
        finish();
    }

    public void onClose() {
        Logger.m172d("");
        finish();
    }
}
