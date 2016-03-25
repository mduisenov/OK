package ru.ok.android.ui.custom.mediacomposer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.ok.android.ui.custom.mediacomposer.FragmentBridge;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerStyleParams;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerView.MediaComposerController;
import ru.ok.android.ui.custom.mediacomposer.MediaItem;
import ru.ok.android.ui.custom.mediacomposer.MediaItemActionProvider;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.request.mediatopic.MediaTopicType;

public class UnsupportedItemAdapterHandler extends MediaItemAdapterHandler<MediaItem> {
    protected UnsupportedItemAdapterHandler(Context context, LocalizationManager localizationManager, FragmentBridge fragmentBridge, MediaComposerController mediaComposerController, MediaTopicType mediaTopicType, MediaComposerStyleParams styleParams, ImageHandler imageHandler) {
        super(context, localizationManager, styleParams, mediaComposerController, fragmentBridge, mediaTopicType, imageHandler);
    }

    MediaItemActionProvider createActionProvider() {
        return new UnsupportedItemActionProvider(this.fragmentBridge, this.mediaComposerController, this.mediaTopicType);
    }

    public View createView(MediaItem mediaItem, ViewGroup parent, boolean isEditable, int viewId) {
        LocalizationManager localizationManager = this.localizationManager;
        TextView dummyText = (TextView) LocalizationManager.inflate(this.context, 2130903302, parent, false);
        dummyText.setText(getClass().getSimpleName() + " media type not yet supported");
        updateViewIsEditable(dummyText, mediaItem, parent, isEditable);
        dummyText.setId(viewId);
        return dummyText;
    }

    public void updateViewIsEditable(View view, MediaItem mediaItem, ViewGroup parent, boolean isEditable) {
        super.updateViewIsEditable(view, mediaItem, parent, isEditable);
    }
}
