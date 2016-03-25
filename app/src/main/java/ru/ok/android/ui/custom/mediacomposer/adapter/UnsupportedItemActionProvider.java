package ru.ok.android.ui.custom.mediacomposer.adapter;

import ru.ok.android.ui.custom.mediacomposer.FragmentBridge;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerView.MediaComposerController;
import ru.ok.android.ui.custom.mediacomposer.MediaItemActionProvider;
import ru.ok.java.api.request.mediatopic.MediaTopicType;

public class UnsupportedItemActionProvider extends MediaItemActionProvider {
    protected UnsupportedItemActionProvider(FragmentBridge fragmentBridge, MediaComposerController mediaComposerController, MediaTopicType mediaTopicType) {
        super(fragmentBridge, mediaComposerController, mediaTopicType);
    }
}
