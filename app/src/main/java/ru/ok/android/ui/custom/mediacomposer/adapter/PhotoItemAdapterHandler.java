package ru.ok.android.ui.custom.mediacomposer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.ui.custom.mediacomposer.EditablePhotoItem;
import ru.ok.android.ui.custom.mediacomposer.EditablePhotoItemView;
import ru.ok.android.ui.custom.mediacomposer.FragmentBridge;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerStyleParams;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerView.MediaComposerController;
import ru.ok.android.ui.custom.mediacomposer.MediaItemActionProvider;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.request.mediatopic.MediaTopicType;

public class PhotoItemAdapterHandler extends MediaItemAdapterHandler<EditablePhotoItem> {
    protected PhotoItemAdapterHandler(Context context, LocalizationManager localizationManager, FragmentBridge fragmentBridge, MediaComposerController mediaComposerController, MediaTopicType mediaTopicType, MediaComposerStyleParams styleParams, ImageHandler imageHandler) {
        super(context, localizationManager, styleParams, mediaComposerController, fragmentBridge, mediaTopicType, imageHandler);
    }

    MediaItemActionProvider createActionProvider() {
        return new PhotoItemActionProvider(this.fragmentBridge, this.mediaComposerController, this.mediaTopicType, this.imageHandler);
    }

    public View createView(EditablePhotoItem photoItem, ViewGroup parent, boolean isEditable, int viewId) {
        LocalizationManager localizationManager = this.localizationManager;
        EditablePhotoItemView editablePhotoItemView = (EditablePhotoItemView) LocalizationManager.inflate(this.context, 2130903303, parent, false);
        this.imageHandler.setImage(photoItem.getImageUri(), editablePhotoItemView, photoItem.getOrientation());
        editablePhotoItemView.bindItem(photoItem);
        View itemView = createDecoratedViewWithActions(photoItem, editablePhotoItemView, parent, null);
        itemView.setTag(2131624333, editablePhotoItemView);
        updateViewIsEditable(itemView, photoItem, parent, isEditable);
        itemView.setId(viewId);
        return itemView;
    }

    public void updateViewIsEditable(View view, EditablePhotoItem mediaItem, ViewGroup parent, boolean isEditable) {
        super.updateViewIsEditable(view, mediaItem, parent, isEditable);
        view.setClickable(isEditable);
    }

    public void disposeView(View view, EditablePhotoItem mediaItem) {
        this.imageHandler.onViewRemoved(view);
        super.disposeView(view, mediaItem);
    }
}
