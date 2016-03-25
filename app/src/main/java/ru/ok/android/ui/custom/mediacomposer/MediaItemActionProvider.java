package ru.ok.android.ui.custom.mediacomposer;

import android.content.Intent;
import android.os.Bundle;
import ru.ok.android.statistics.mediacomposer.MediaComposerStats;
import ru.ok.android.ui.custom.mediacomposer.FragmentBridge.ResultCallback;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerView.MediaComposerController;
import ru.ok.android.ui.fragments.MediaTopicEditorFragment;
import ru.ok.android.ui.quickactions.ActionItem;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.request.mediatopic.MediaTopicType;

public abstract class MediaItemActionProvider implements ResultCallback {
    protected final int callbackId;
    protected final FragmentBridge fragmentBridge;
    protected final MediaComposerController mediaComposerController;
    protected final MediaTopicType mediaTopicType;
    protected final String statsMode;

    protected MediaItemActionProvider(FragmentBridge fragmentBridge, MediaComposerController mediaComposerController, MediaTopicType mediaTopicType) {
        this.fragmentBridge = fragmentBridge;
        this.mediaComposerController = mediaComposerController;
        this.mediaTopicType = mediaTopicType;
        this.callbackId = fragmentBridge.addCallback(this);
        this.statsMode = fragmentBridge.getFragment() instanceof MediaTopicEditorFragment ? "new" : "edit";
    }

    protected boolean onPrepareAction(ActionItem actionItem, MediaItem item, Bundle extras) {
        if (actionItem.getActionId() != 2131624281) {
            return true;
        }
        boolean hasPlaceToInsert;
        if (this.mediaComposerController.getBlockCount() + 2 <= this.mediaComposerController.getMaxAllowedBlockCount()) {
            hasPlaceToInsert = true;
        } else {
            hasPlaceToInsert = false;
        }
        if (!hasPlaceToInsert) {
            return false;
        }
        if (extras == null || !extras.getBoolean("insert_text_before")) {
            return false;
        }
        return true;
    }

    public void onActionSelected(int actionId, MediaItem mediaItem, Bundle extras) {
        String statsMode = extras.getString("mode");
        switch (actionId) {
            case 2131624280:
                MediaComposerStats.popupEdit(mediaItem, statsMode);
                startMediaEdit(mediaItem, extras);
            case 2131624281:
                MediaComposerStats.popupInsertText(mediaItem, statsMode);
                insertText(mediaItem, extras);
            case 2131624283:
                MediaComposerStats.popupDelete(mediaItem, statsMode);
                remove(mediaItem, extras);
            default:
        }
    }

    public void startMediaAdd(Bundle extras) {
    }

    public void startMediaEdit(MediaItem mediaItem, Bundle extras) {
    }

    public void insertText(MediaItem mediaItem, Bundle extras) {
        Logger.m173d("position=%d mediaItem=%s", Integer.valueOf(this.mediaComposerController.getItemPosition(mediaItem)), mediaItem);
        this.mediaComposerController.insert(new TextItem(), position, true);
    }

    public void remove(MediaItem mediaItem, Bundle extras) {
        this.mediaComposerController.remove(mediaItem, true, false);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    protected final void startActivityForResult(Intent intent, int requestCode) {
        this.fragmentBridge.startActivityForResult(intent, requestCode, this.callbackId);
    }
}
