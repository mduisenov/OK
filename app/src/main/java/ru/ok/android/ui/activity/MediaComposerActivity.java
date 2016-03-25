package ru.ok.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.persistent.PersistentTaskService;
import ru.ok.android.services.processors.mediatopic.PostMediaTopicTask;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerData;
import ru.ok.android.ui.custom.mediacomposer.MediaTopicMessage;
import ru.ok.android.ui.fragments.MediaComposerFragment;
import ru.ok.android.ui.fragments.MediaComposerFragment.MediaComposerFragmentListener;
import ru.ok.android.ui.fragments.MediaComposerFragment.OnToStatusChangedListener;
import ru.ok.android.ui.fragments.MediaComposerPreferences;
import ru.ok.android.ui.fragments.MediaTopicEditorFragment;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.localization.base.LocalizedActivity;
import ru.ok.android.utils.settings.ServicesSettingsHelper;
import ru.ok.java.api.request.mediatopic.MediaTopicType;
import ru.ok.model.UserInfo;
import ru.ok.model.settings.MediaComposerSettings;

public final class MediaComposerActivity extends LocalizedActivity implements MediaComposerFragmentListener, OnToStatusChangedListener {
    private boolean isGroupSuggestedTopic;
    private MediaComposerPreferences mediaComposerPreferences;
    private String titleNotToStatus;
    private String titleToStatus;

    public MediaComposerActivity() {
        this.titleToStatus = "";
        this.titleNotToStatus = "";
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        MediaComposerFragment mediaComposerFragment;
        Logger.m172d("savedInstanceState=" + savedInstanceState);
        setContentView(2130903291);
        int titleResourceId = 2131166087;
        int titleToStatusResId = 0;
        int hintResourceId = 0;
        int sendActionLabelId = 0;
        String classAlias = getIntent().getComponent().getClassName();
        Object obj = -1;
        switch (classAlias.hashCode()) {
            case -1065764333:
                if (classAlias.equals("ru.ok.android.ui.activity.MediaComposerUserActivity")) {
                    obj = null;
                    break;
                }
                break;
            case -450941003:
                if (classAlias.equals("ru.ok.android.ui.activity.MediaComposerGroupActivity")) {
                    obj = 1;
                    break;
                }
                break;
        }
        switch (obj) {
            case RECEIVED_VALUE:
                titleResourceId = 2131166087;
                titleToStatusResId = 2131166088;
                hintResourceId = 2131166183;
                sendActionLabelId = 2131166085;
                break;
            case Message.TEXT_FIELD_NUMBER /*1*/:
                boolean z = !getIntent().getExtras().getBoolean("media_topic_group_user_can_post") && getIntent().getExtras().getBoolean("media_topic_group_user_can_suggest");
                this.isGroupSuggestedTopic = z;
                titleResourceId = 2131166082;
                hintResourceId = 2131166182;
                if (this.isGroupSuggestedTopic) {
                    getSupportToolbar().setSubtitle(2131166081);
                }
                sendActionLabelId = this.isGroupSuggestedTopic ? 2131166080 : 2131166083;
                break;
            default:
                Logger.m185w("Can't set properly title for alias %s", classAlias);
                break;
        }
        if (titleResourceId != 0) {
            String str;
            this.titleNotToStatus = getStringLocalized(titleResourceId);
            if (titleToStatusResId == 0) {
                str = this.titleNotToStatus;
            } else {
                str = getStringLocalized(titleToStatusResId);
            }
            this.titleToStatus = str;
        }
        if (savedInstanceState == null) {
            MediaComposerData data;
            MediaComposerData blankData;
            Intent intent = getIntent();
            String groupId = intent.getStringExtra("media_topic_gid");
            MediaComposerSettings settings = MediaComposerSettings.fromSharedPreferences(ServicesSettingsHelper.getPreferences(this));
            boolean toStatus = getToStatus(intent);
            MediaTopicMessage mediaTopicMessage = (MediaTopicMessage) intent.getParcelableExtra("media_topic");
            int maxBlockCount;
            if (groupId == null) {
                data = MediaComposerData.user(mediaTopicMessage, toStatus);
                blankData = MediaComposerData.user(toStatus);
                maxBlockCount = settings.maxBlockCount;
            } else {
                data = this.isGroupSuggestedTopic ? MediaComposerData.groupSuggested(groupId, mediaTopicMessage) : MediaComposerData.group(groupId, mediaTopicMessage);
                blankData = this.isGroupSuggestedTopic ? MediaComposerData.groupSuggested(groupId) : MediaComposerData.group(groupId);
                maxBlockCount = settings.maxGroupBlockCount;
            }
            Bundle extras = new Bundle();
            if (hintResourceId != 0) {
                extras.putString("blank_text_hint", getStringLocalized(hintResourceId));
            }
            if (sendActionLabelId != 0) {
                extras.putString("send_action_label", getStringLocalized(sendActionLabelId));
            }
            mediaComposerFragment = MediaTopicEditorFragment.newInstance(data, blankData, extras);
            getSupportFragmentManager().beginTransaction().add(C0263R.id.container, mediaComposerFragment, "media_composer_fragment").commit();
        } else {
            mediaComposerFragment = (MediaComposerFragment) getSupportFragmentManager().findFragmentByTag("media_composer_fragment");
        }
        mediaComposerFragment.setListener(this);
    }

    private boolean getToStatus(Intent intent) {
        String groupId = intent.getStringExtra("media_topic_gid");
        int toStatusValue = getMediaComposerPreferences().getLastUsedToStatus(OdnoklassnikiApplication.getCurrentUser(), groupId != null ? MediaTopicType.GROUP_THEME : MediaTopicType.USER, groupId);
        if (toStatusValue == 3) {
            String str = "to_status";
            if (groupId == null) {
            }
            return intent.getBooleanExtra(str, false);
        } else if (toStatusValue != 1) {
            return false;
        } else {
            return true;
        }
    }

    protected void onStart() {
        super.onStart();
        MediaComposerFragment fragment = findMediaComposerFragment();
        if (fragment != null) {
            setTitle(fragment.isToStatusChecked() ? this.titleToStatus : this.titleNotToStatus);
        }
    }

    public void onBackPressed() {
        Logger.m172d("");
        super.onBackPressed();
        MediaComposerFragment mediaComposerFragment = findMediaComposerFragment();
        if (mediaComposerFragment != null && !mediaComposerFragment.isMediaTopicEmpty()) {
            Toast.makeText(this, getStringLocalized(2131166119), 0).show();
        }
    }

    private MediaComposerFragment findMediaComposerFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager == null) {
            return null;
        }
        Fragment fragment = fragmentManager.findFragmentByTag("media_composer_fragment");
        return fragment instanceof MediaComposerFragment ? (MediaComposerFragment) fragment : null;
    }

    protected void onResume() {
        super.onResume();
        setProgressBarIndeterminateVisibility(false);
        setProgressBarVisibility(false);
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    public void onMediaComposerCompleted(MediaComposerData mediaComposerData) {
        Logger.m172d("media composer completed, submitting upload task...");
        UserInfo userInfo = OdnoklassnikiApplication.getCurrentUser();
        String uid = userInfo == null ? null : userInfo.uid;
        if (uid == null) {
            Logger.m176e("Not currently logged in, cannot post media topic!");
        } else {
            getMediaComposerPreferences().setLastUsedToStatus(userInfo, mediaComposerData.mediaTopicType, mediaComposerData.groupId, mediaComposerData.toStatus ? 1 : 2);
            try {
                PersistentTaskService.submit(this, new PostMediaTopicTask(uid, mediaComposerData));
            } catch (Throwable e) {
                Logger.m177e("Failed to submit upload task: %s", e);
                Logger.m178e(e);
            }
        }
        Intent data = new Intent();
        data.fillIn(getIntent(), 11);
        setResult(-1, data);
        finish();
    }

    public void onToStatusChanged(boolean toStatusNewValue) {
        setTitle(toStatusNewValue ? this.titleToStatus : this.titleNotToStatus);
    }

    private MediaComposerPreferences getMediaComposerPreferences() {
        if (this.mediaComposerPreferences == null) {
            this.mediaComposerPreferences = new MediaComposerPreferences(this);
        }
        return this.mediaComposerPreferences;
    }

    protected boolean isToolbarLocked() {
        return true;
    }
}
