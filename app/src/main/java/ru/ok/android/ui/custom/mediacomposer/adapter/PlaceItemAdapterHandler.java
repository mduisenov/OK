package ru.ok.android.ui.custom.mediacomposer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.ok.android.ui.custom.mediacomposer.FragmentBridge;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerStyleParams;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerView.MediaComposerController;
import ru.ok.android.ui.custom.mediacomposer.MediaItemActionProvider;
import ru.ok.android.ui.custom.mediacomposer.PlaceItem;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.request.mediatopic.MediaTopicType;

public class PlaceItemAdapterHandler extends MediaItemAdapterHandler<PlaceItem> {
    protected PlaceItemAdapterHandler(Context context, LocalizationManager localizationManager, FragmentBridge fragmentBridge, MediaComposerController mediaComposerController, MediaTopicType mediaTopicType, MediaComposerStyleParams styleParams, ImageHandler imageHandler) {
        super(context, localizationManager, styleParams, mediaComposerController, fragmentBridge, mediaTopicType, imageHandler);
    }

    MediaItemActionProvider createActionProvider() {
        return new PlaceItemActionProvider(this.fragmentBridge, this.mediaComposerController, this.mediaTopicType);
    }

    public View createView(PlaceItem placeItem, ViewGroup parent, boolean isEditable, int viewId) {
        LocalizationManager localizationManager = this.localizationManager;
        View mainView = LocalizationManager.inflate(this.context, 2130903305, parent, false);
        View textViewPlace = (TextView) mainView.findViewById(2131625044);
        textViewPlace.setText(placeItem.getPlace().category.in);
        updateViewIsEditable(textViewPlace, placeItem, parent, isEditable);
        mainView.setId(viewId);
        return mainView;
    }

    public void updateViewIsEditable(View view, PlaceItem placeItem, ViewGroup parent, boolean isEditable) {
        super.updateViewIsEditable(view, placeItem, parent, isEditable);
        view.setFocusableInTouchMode(isEditable);
    }

    protected boolean canHaveInsertTextAction() {
        return false;
    }
}
