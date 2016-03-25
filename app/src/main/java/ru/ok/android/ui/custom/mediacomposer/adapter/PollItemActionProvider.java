package ru.ok.android.ui.custom.mediacomposer.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import ru.ok.android.statistics.mediacomposer.MediaComposerStats;
import ru.ok.android.ui.activity.PollActivity;
import ru.ok.android.ui.custom.mediacomposer.FragmentBridge;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerView.MediaComposerController;
import ru.ok.android.ui.custom.mediacomposer.MediaItem;
import ru.ok.android.ui.custom.mediacomposer.MediaItemActionProvider;
import ru.ok.android.ui.custom.mediacomposer.PollItem;
import ru.ok.java.api.request.mediatopic.MediaTopicType;

public class PollItemActionProvider extends MediaItemActionProvider {
    protected PollItemActionProvider(FragmentBridge fragmentBridge, MediaComposerController mediaComposerController, MediaTopicType mediaTopicType) {
        super(fragmentBridge, mediaComposerController, mediaTopicType);
    }

    public void startMediaAdd(Bundle extras) {
        startMediaEdit(null, extras);
    }

    public void startMediaEdit(MediaItem mediaItem, Bundle extras) {
        Context context = this.fragmentBridge.getContext();
        if (context != null) {
            Intent intent = new Intent(context, PollActivity.class);
            intent.putExtra("key_poll", mediaItem);
            intent.putExtra("mt_type", this.mediaTopicType);
            startActivityForResult(intent, 3);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1 && data != null && data.hasExtra("key_poll")) {
            PollItem pollItem = (PollItem) data.getParcelableExtra("key_poll");
            if (pollItem.getViewId() == 0) {
                MediaComposerStats.addPoll(pollItem, this.statsMode);
                this.mediaComposerController.insertAtCursor(pollItem);
                return;
            }
            MediaItem originalItem = this.mediaComposerController.findItem(pollItem);
            if (originalItem instanceof PollItem) {
                MediaComposerStats.editPoll((PollItem) originalItem, pollItem, this.statsMode);
            }
            this.mediaComposerController.update(pollItem);
        }
    }
}
