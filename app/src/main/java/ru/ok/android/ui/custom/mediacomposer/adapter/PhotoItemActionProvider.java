package ru.ok.android.ui.custom.mediacomposer.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.model.image.ImageEditInfo;
import ru.ok.android.services.app.IntentUtils;
import ru.ok.android.statistics.mediacomposer.MediaComposerStats;
import ru.ok.android.ui.custom.mediacomposer.EditablePhotoItem;
import ru.ok.android.ui.custom.mediacomposer.FragmentBridge;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerView.MediaComposerController;
import ru.ok.android.ui.custom.mediacomposer.MediaItem;
import ru.ok.android.ui.custom.mediacomposer.MediaItemActionProvider;
import ru.ok.android.ui.image.PrepareImagesActivity;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.request.mediatopic.MediaTopicType;
import ru.ok.model.photo.PhotoAlbumInfo;
import ru.ok.model.photo.PhotoAlbumInfo.OwnerType;

public class PhotoItemActionProvider extends MediaItemActionProvider {
    protected final ImageHandler imageHandler;
    private PhotoAlbumInfo mobileAlbum;

    /* renamed from: ru.ok.android.ui.custom.mediacomposer.adapter.PhotoItemActionProvider.1 */
    class C06921 extends ArrayList<ImageEditInfo> {
        final /* synthetic */ ImageEditInfo val$imageEditInfo;

        C06921(int x0, ImageEditInfo imageEditInfo) {
            this.val$imageEditInfo = imageEditInfo;
            super(x0);
            add(this.val$imageEditInfo);
        }
    }

    protected PhotoItemActionProvider(FragmentBridge fragmentBridge, MediaComposerController mediaComposerController, MediaTopicType mediaTopicType, ImageHandler imageHandler) {
        super(fragmentBridge, mediaComposerController, mediaTopicType);
        this.imageHandler = imageHandler;
    }

    public void startMediaAdd(Bundle extras) {
        Context context = this.fragmentBridge.getContext();
        if (context != null) {
            Intent intent = IntentUtils.createIntentToAddImages(context, getMediaTopicAlbum(), 0, 0, false, false, "media_topic");
            intent.putExtra("upload_btn_text", LocalizationManager.getString(context, 2131165355));
            intent.putExtra("can_create_album", false);
            intent.putExtra("can_select_album", false);
            intent.putExtra("actionbar_title", LocalizationManager.getString(context, 2131166557));
            intent.putExtra("silent_cancel_if_not_edited", true);
            intent.putExtras(extras);
            intent.putExtra("cancel_alert_text", LocalizationManager.getString(context, this.mediaTopicType == MediaTopicType.USER ? 2131166104 : 2131166103));
            startActivityForResult(intent, 1);
        }
    }

    public void startMediaEdit(MediaItem mediaItem, Bundle extras) {
        if (mediaItem == null) {
            Logger.m184w("null media item");
        } else if (mediaItem instanceof EditablePhotoItem) {
            Context context = this.fragmentBridge.getContext();
            if (context != null) {
                Intent editPhotoIntent = createPhotoEditIntent(context, (EditablePhotoItem) mediaItem);
                editPhotoIntent.putExtra("actionbar_title", LocalizationManager.getString(context, 2131166557));
                Logger.m173d("Start editing photo uri=%s", photoItem.getImageUri());
                startActivityForResult(editPhotoIntent, 2);
            }
        } else {
            Logger.m185w("Illegal media type: %s, %s", mediaItem.type, mediaItem.getClass());
        }
    }

    protected Intent createPhotoEditIntent(Context context, EditablePhotoItem photoItem) {
        Intent editPhotoIntent = new Intent(context, PrepareImagesActivity.class).putExtra("comments_enabled", false).putExtra("file_uri", photoItem.getImageUri());
        if (photoItem instanceof EditablePhotoItem) {
            editPhotoIntent.putExtra("imgs", new C06921(1, photoItem.getImageEditInfo()));
        }
        editPhotoIntent.putExtra("rttn", photoItem.getOrientation()).putExtra("edited_item", photoItem).putExtra("choice_mode", 1).putExtra("upload_btn_text", LocalizationManager.getString(context, 2131166475)).putExtra("can_create_album", false).putExtra("can_select_album", false).putExtra("album", getMediaTopicAlbum()).putExtra("cancel_alert_text", LocalizationManager.getString(context, 2131166105)).putExtra("silent_cancel_if_not_edited", true);
        return editPhotoIntent;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == 1 || requestCode == 2) && resultCode == -1 && data != null) {
            List<EditablePhotoItem> photoItems = getPhotoItemsFromActivityResult(data);
            if (photoItems == null || photoItems.size() <= 0) {
                Logger.m184w("Empty result of add/edit photo request.");
            } else if (requestCode == 1) {
                onAddPhotos(photoItems, data);
            } else if (photoItems.size() != 1) {
                Logger.m184w("Unexpected result, should be exactly one edited image");
            } else {
                EditablePhotoItem originalItem = (EditablePhotoItem) data.getParcelableExtra("edited_item");
                Logger.m173d("Original edited item: %s", originalItem);
                if (originalItem == null) {
                    Logger.m184w("Original edited item not found in result intent");
                    return;
                }
                EditablePhotoItem updatedItem = (EditablePhotoItem) photoItems.get(0);
                updatedItem.setViewId(originalItem.getViewId());
                onEditPhoto(updatedItem, originalItem, data);
            }
        }
    }

    protected List<EditablePhotoItem> getPhotoItemsFromActivityResult(Intent data) {
        try {
            List<EditablePhotoItem> arrayList = new ArrayList();
            if (!data.hasExtra("imgs")) {
                return arrayList;
            }
            ArrayList<ImageEditInfo> editedImages = data.getParcelableArrayListExtra("imgs");
            if (editedImages == null) {
                return arrayList;
            }
            Iterator i$ = editedImages.iterator();
            while (i$.hasNext()) {
                ImageEditInfo editedImage = (ImageEditInfo) i$.next();
                if (this.mediaTopicType != MediaTopicType.USER) {
                    editedImage.setAlbumInfo(null);
                }
                arrayList.add(new EditablePhotoItem(editedImage));
            }
            return arrayList;
        } catch (Throwable e) {
            Logger.m177e("failed to handle pick from gallery result: " + e, e);
            return Collections.emptyList();
        }
    }

    protected void onAddPhotos(List<EditablePhotoItem> photoItems, Intent data) {
        MediaComposerStats.addPhoto(photoItems.size(), this.statsMode);
        for (EditablePhotoItem item : photoItems) {
            this.mediaComposerController.insertAtCursor(item);
        }
    }

    protected void onEditPhoto(EditablePhotoItem updatedItem, EditablePhotoItem originalItem, Intent data) {
        View originalView = this.mediaComposerController.getItemView(originalItem);
        if (originalView != null) {
            ImageView imageView = (ImageView) originalView.getTag(2131624333);
            if (imageView != null) {
                this.imageHandler.setImage(updatedItem.getImageUri(), imageView, updatedItem.getOrientation());
                ((Bundle) originalView.getTag(2131624309)).putParcelable("action_item", updatedItem);
                this.mediaComposerController.update(updatedItem, true);
                return;
            }
            Logger.m184w("Image view not found!");
        } else {
            Logger.m184w("Original item view not found!");
        }
        MediaComposerStats.editPhoto(this.statsMode);
        this.mediaComposerController.update(updatedItem);
    }

    protected PhotoAlbumInfo getMediaTopicAlbum() {
        if (this.mobileAlbum == null) {
            String mMobileAlbumTitle = LocalizationManager.getString(OdnoklassnikiApplication.getContext(), 2131166218);
            PhotoAlbumInfo photoAlbum = new PhotoAlbumInfo();
            photoAlbum.setId("other");
            photoAlbum.setTitle(mMobileAlbumTitle);
            photoAlbum.setOwnerType(OwnerType.USER);
            this.mobileAlbum = photoAlbum;
        }
        return this.mobileAlbum;
    }
}
