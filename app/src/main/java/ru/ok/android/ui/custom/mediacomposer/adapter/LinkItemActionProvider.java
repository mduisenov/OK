package ru.ok.android.ui.custom.mediacomposer.adapter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import ru.ok.android.ui.custom.mediacomposer.FragmentBridge;
import ru.ok.android.ui.custom.mediacomposer.LinkItem;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerView.MediaComposerController;
import ru.ok.android.ui.custom.mediacomposer.MediaItem;
import ru.ok.android.ui.custom.mediacomposer.MediaItemActionProvider;
import ru.ok.java.api.request.mediatopic.MediaTopicType;

public class LinkItemActionProvider extends MediaItemActionProvider {
    protected LinkItemActionProvider(FragmentBridge fragmentBridge, MediaComposerController mediaComposerController, MediaTopicType mediaTopicType) {
        super(fragmentBridge, mediaComposerController, mediaTopicType);
    }

    public void onActionSelected(int actionId, MediaItem mediaItem, Bundle extras) {
        if (actionId == 2131624282 && (mediaItem instanceof LinkItem)) {
            String url = ((LinkItem) mediaItem).getLinkUrl();
            if (!TextUtils.isEmpty(url)) {
                this.fragmentBridge.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
                return;
            }
            return;
        }
        super.onActionSelected(actionId, mediaItem, extras);
    }
}
