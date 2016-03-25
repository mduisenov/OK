package ru.ok.android.ui.fragments;

import android.os.Bundle;
import android.view.View;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.fragments.FriendsListWithPrivacyFilterFragment;
import ru.ok.android.statistics.mediacomposer.MediaComposerStats;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerData;
import ru.ok.android.ui.dialogs.ConfirmationDialog;
import ru.ok.android.ui.dialogs.ConfirmationDialog.OnConfirmationDialogListener;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.request.friends.FriendsFilter;
import ru.ok.java.api.request.mediatopic.MediaTopicType;
import ru.ok.model.UserInfo;

public class MediaTopicEditorFragment extends MediaComposerFragment implements OnConfirmationDialogListener {
    private boolean isCompleted;
    private MediaComposerPreferences prefs;

    public MediaTopicEditorFragment() {
        this.isCompleted = false;
    }

    public static MediaTopicEditorFragment newInstance(MediaComposerData newComposerData, MediaComposerData blankData, Bundle extras) {
        MediaTopicEditorFragment fragment = new MediaTopicEditorFragment();
        Bundle args = MediaComposerFragment.createArgs(blankData, extras);
        args.putParcelable("new_mediatopic", newComposerData);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.prefs = new MediaComposerPreferences(getActivity());
        FriendsListWithPrivacyFilterFragment.sendFriendsFilterRequest(FriendsFilter.MARK_IN_TOPICS);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            MediaComposerData restoredDraft;
            if (this.mediaTopicType == MediaTopicType.USER) {
                restoredDraft = this.prefs.getUserMediaTopicDraft(OdnoklassnikiApplication.getCurrentUser());
            } else if (this.mediaTopicType == MediaTopicType.GROUP_THEME) {
                restoredDraft = this.prefs.getGroupMediaTopicDraft(OdnoklassnikiApplication.getCurrentUser(), this.groupId);
            } else {
                restoredDraft = null;
            }
            if (restoredDraft != null) {
                this.mediaComposerController.reset(restoredDraft.mediaTopicMessage);
                this.isToStatusChecked = restoredDraft.toStatus;
                ConfirmationDialog dialog = ConfirmationDialog.newInstance(2131166116, this.mediaTopicType == MediaTopicType.USER ? 2131166115 : 2131166114, 2131166118, 2131166117, 1);
                dialog.show(getFragmentManager(), "confirm_restore_draft");
                dialog.setTargetFragment(this, 1);
            } else {
                MediaComposerData newMediaTopic = (MediaComposerData) getArguments().getParcelable("new_mediatopic");
                if (newMediaTopic != null) {
                    if (newMediaTopic.mediaTopicMessage != null) {
                        this.mediaComposerController.reset(newMediaTopic.mediaTopicMessage);
                    }
                    this.isToStatusChecked = newMediaTopic.toStatus;
                }
            }
        }
        super.onViewCreated(view, savedInstanceState);
    }

    public void onPause() {
        super.onPause();
        UserInfo currentUser = OdnoklassnikiApplication.getCurrentUser();
        if (this.isCompleted || this.mediaComposerController.isEmpty()) {
            if (this.mediaTopicType == MediaTopicType.USER) {
                this.prefs.deleteUserMediaTopicDraft(currentUser);
            } else if (this.mediaTopicType == MediaTopicType.GROUP_THEME || this.mediaTopicType == MediaTopicType.GROUP_SUGGESTED) {
                this.prefs.deleteGroupMediaTopicDraft(currentUser, this.groupId);
            }
        } else if (this.mediaTopicType == MediaTopicType.USER) {
            this.prefs.saveUserMediaTopicDraft(currentUser, MediaComposerData.user(this.mediaComposerController.getMediaTopicMessage(), this.isToStatusChecked));
        } else if (this.mediaTopicType == MediaTopicType.GROUP_THEME || this.mediaTopicType == MediaTopicType.GROUP_SUGGESTED) {
            this.prefs.saveGroupMediaTopicDarft(currentUser, this.groupId, MediaComposerData.group(this.groupId, this.mediaComposerController.getMediaTopicMessage()));
        }
    }

    protected void complete() {
        this.isCompleted = true;
        super.complete();
    }

    protected void onRestoreDraft(boolean doRestore) {
        if (!doRestore) {
            MediaComposerData newMediaTopic = (MediaComposerData) getArguments().getParcelable("new_mediatopic");
            if (newMediaTopic == null || newMediaTopic.mediaTopicMessage == null) {
                this.mediaComposerController.restartFromBlankPage();
            } else {
                this.mediaComposerController.reset(newMediaTopic.mediaTopicMessage);
                this.isToStatusChecked = newMediaTopic.toStatus;
            }
            updateWithFriendsCounter();
            updateWithPlace();
            UserInfo currentUser = OdnoklassnikiApplication.getCurrentUser();
            if (this.mediaTopicType == MediaTopicType.USER) {
                this.prefs.deleteUserMediaTopicDraft(currentUser);
            } else if (this.mediaTopicType == MediaTopicType.GROUP_THEME) {
                this.prefs.deleteGroupMediaTopicDraft(currentUser, this.groupId);
            }
        }
    }

    public void onConfirmationDialogResult(boolean isPositive, int requestCode) {
        if (requestCode == 1) {
            onRestoreDraft(isPositive);
            MediaComposerStats.draft(isPositive ? "ok" : "cancel");
        }
    }

    public void onConfirmationDialogDismissed(int requestCode) {
        Logger.m173d("requestCode=%d", Integer.valueOf(requestCode));
    }
}
