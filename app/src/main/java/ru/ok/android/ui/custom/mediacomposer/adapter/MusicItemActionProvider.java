package ru.ok.android.ui.custom.mediacomposer.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import ru.ok.android.statistics.mediacomposer.MediaComposerStats;
import ru.ok.android.ui.activity.ChoiceMusicActivity;
import ru.ok.android.ui.custom.mediacomposer.FragmentBridge;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerView.MediaComposerController;
import ru.ok.android.ui.custom.mediacomposer.MediaItem;
import ru.ok.android.ui.custom.mediacomposer.MediaItemActionProvider;
import ru.ok.android.ui.custom.mediacomposer.MusicItem;
import ru.ok.java.api.request.mediatopic.MediaTopicType;

public class MusicItemActionProvider extends MediaItemActionProvider {
    protected MusicItemActionProvider(FragmentBridge fragmentBridge, MediaComposerController mediaComposerController, MediaTopicType mediaTopicType) {
        super(fragmentBridge, mediaComposerController, mediaTopicType);
    }

    public void startMediaAdd(Bundle extras) {
        startMediaEdit(null, extras);
    }

    public void startMediaEdit(MediaItem mediaItem, Bundle extras) {
        Context context = this.fragmentBridge.getContext();
        if (context != null) {
            Intent intent = new Intent(context, ChoiceMusicActivity.class);
            intent.putExtra("music_item_key", mediaItem);
            startActivityForResult(intent, 4);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 4 && resultCode == -1 && data != null && data.hasExtra("music_item_key")) {
            MusicItem musicItem = (MusicItem) data.getParcelableExtra("music_item_key");
            if (musicItem.isEmpty()) {
                if (musicItem.getViewId() != 0) {
                    this.mediaComposerController.remove(musicItem, false, false);
                }
            } else if (musicItem.getViewId() == 0) {
                this.mediaComposerController.insertAtCursor(musicItem);
            } else {
                this.mediaComposerController.update(musicItem);
            }
            MediaItem originalItem = this.mediaComposerController.findItem(musicItem);
            if (originalItem instanceof MusicItem) {
                MediaComposerStats.editMusic((MusicItem) originalItem, musicItem, this.statsMode);
            } else {
                MediaComposerStats.addMusic(musicItem, this.statsMode);
            }
        }
    }
}
