package ru.ok.android.ui.custom.mediacomposer.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import ru.ok.android.ui.custom.mediacomposer.FragmentBridge;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerView.MediaComposerController;
import ru.ok.android.ui.custom.mediacomposer.MediaItem;
import ru.ok.android.ui.custom.mediacomposer.MediaItemActionProvider;
import ru.ok.android.ui.custom.mediacomposer.PlaceItem;
import ru.ok.android.ui.places.PlacesActivity;
import ru.ok.java.api.request.mediatopic.MediaTopicType;
import ru.ok.model.places.Place;

public class PlaceItemActionProvider extends MediaItemActionProvider {
    protected PlaceItemActionProvider(FragmentBridge fragmentBridge, MediaComposerController mediaComposerController, MediaTopicType mediaTopicType) {
        super(fragmentBridge, mediaComposerController, mediaTopicType);
    }

    public void startMediaAdd(Bundle extras) {
        startMediaEdit(null, extras);
    }

    public void startMediaEdit(MediaItem mediaItem, Bundle extras) {
        Context context = this.fragmentBridge.getContext();
        if (context != null) {
            startActivityForResult(new Intent(context, PlacesActivity.class), 7);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1 && data != null && data.hasExtra("place_result")) {
            this.mediaComposerController.insertAtCursor(new PlaceItem((Place) data.getParcelableExtra("place_result")));
        }
    }
}
