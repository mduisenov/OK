package ru.ok.android.ui.custom.mediacomposer.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.Collection;
import java.util.List;
import ru.ok.android.statistics.mediacomposer.MediaComposerStats;
import ru.ok.android.ui.custom.mediacomposer.EditablePhotoItem;
import ru.ok.android.ui.custom.mediacomposer.FragmentBridge;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerView.MediaComposerController;
import ru.ok.android.ui.custom.mediacomposer.MediaItem;
import ru.ok.android.ui.custom.mediacomposer.PhotoBlockItem;
import ru.ok.android.ui.custom.mediacomposer.SimpleLayoutTransitionAnimator;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.request.mediatopic.MediaTopicType;

public class PhotoBlockItemActionProvider extends PhotoItemActionProvider {
    private final PhotoBlockItemAdapterHandler handler;

    protected PhotoBlockItemActionProvider(FragmentBridge fragmentBridge, MediaComposerController mediaComposerController, MediaTopicType mediaTopicType, ImageHandler imageHandler, PhotoBlockItemAdapterHandler handler) {
        super(fragmentBridge, mediaComposerController, mediaTopicType, imageHandler);
        this.handler = handler;
    }

    public void startMediaEdit(MediaItem mediaItem, Bundle extras) {
        Context context = this.fragmentBridge.getContext();
        if (context != null) {
            if (extras == null) {
                Logger.m184w("null extras");
            } else if (mediaItem == null) {
                Logger.m184w("null media item");
            } else {
                if (!(mediaItem instanceof EditablePhotoItem)) {
                    Logger.m185w("Invalid media item type: %s, %s", mediaItem.type, mediaItem.getClass().getName());
                }
                Intent editPhoto = super.createPhotoEditIntent(context, (EditablePhotoItem) mediaItem);
                editPhoto.putExtras(extras);
                Logger.m173d("Start editing photo uri=%s, with extras: ", photoItem.getImageUri(), extras);
                startActivityForResult(editPhoto, 2);
            }
        }
    }

    protected void onAddPhotos(List<EditablePhotoItem> photoItems, Intent data) {
        MediaComposerStats.addPhoto(photoItems.size(), this.statsMode);
        this.mediaComposerController.insertAtCursor(new PhotoBlockItem((Collection) photoItems));
    }

    protected void onEditPhoto(EditablePhotoItem updatedItem, EditablePhotoItem originalItem, Intent data) {
        PhotoBlockItem originalBlock = (PhotoBlockItem) data.getParcelableExtra("block");
        int positionInBlock = data.getIntExtra("pos_in_block", -1);
        if (originalBlock == null) {
            Logger.m184w("Extra block is missing");
        } else if (positionInBlock == -1) {
            Logger.m184w("Extra pos_in_block is missing");
        } else {
            ViewGroup photoBlockView = (ViewGroup) this.mediaComposerController.getItemView(originalBlock);
            if (photoBlockView == null) {
                Logger.m184w("Photo block view not found");
                return;
            }
            if (positionInBlock < 0 || positionInBlock >= originalBlock.size()) {
                Logger.m185w("Illegal position in block: %d, size=%d", Integer.valueOf(positionInBlock), Integer.valueOf(originalBlock.size()));
            }
            originalBlock.replace(positionInBlock, updatedItem);
            View itemView = photoBlockView.getChildAt(positionInBlock);
            ImageView imageView = (ImageView) itemView.getTag(2131624333);
            if (imageView != null) {
                this.imageHandler.setImage(updatedItem.getImageUri(), imageView, updatedItem.getOrientation());
            } else {
                Logger.m184w("Image view not found!");
            }
            ((Bundle) itemView.getTag(2131624309)).putParcelable("action_item", updatedItem);
            MediaComposerStats.editPhoto(this.statsMode);
            this.mediaComposerController.update(originalBlock, true);
        }
    }

    public void remove(MediaItem mediaItem, Bundle extras) {
        PhotoBlockItem originalBlock = (PhotoBlockItem) extras.getParcelable("block");
        int positionInBlock = extras.getInt("pos_in_block", -1);
        if (originalBlock == null) {
            Logger.m184w("Extra block is missing");
        } else if (positionInBlock == -1) {
            Logger.m184w("Extra pos_in_block is missing");
        } else {
            ViewGroup photoBlockView = (ViewGroup) this.mediaComposerController.getItemView(originalBlock);
            if (photoBlockView == null) {
                Logger.m184w("Photo block view not found");
                return;
            }
            if (positionInBlock < 0 || positionInBlock >= originalBlock.size()) {
                Logger.m185w("Illegal position in block: %d, size=%d", Integer.valueOf(positionInBlock), Integer.valueOf(originalBlock.size()));
            }
            if (originalBlock.size() == 1) {
                this.mediaComposerController.remove(originalBlock, true, false);
            } else {
                removePhotoInsideBlock(originalBlock, positionInBlock, photoBlockView);
            }
        }
    }

    private void removePhotoInsideBlock(PhotoBlockItem originalBlock, int positionInBlock, ViewGroup photoBlockView) {
        originalBlock.remove(positionInBlock);
        View itemView = photoBlockView.getChildAt(positionInBlock);
        if (((ImageView) itemView.getTag(2131624333)) != null) {
            this.imageHandler.onViewRemoved(itemView);
        } else {
            Logger.m184w("Image view not found!");
        }
        photoBlockView.removeViewAt(positionInBlock);
        new SimpleLayoutTransitionAnimator(photoBlockView, 200).startAnimation();
        int newPhotoBlockSize = photoBlockView.getChildCount();
        updatePositionsInExtras(photoBlockView, positionInBlock, newPhotoBlockSize - 1, -1);
        relayoutChildren(photoBlockView, positionInBlock, newPhotoBlockSize - 1);
        this.mediaComposerController.update(originalBlock, true);
    }

    public void insertText(MediaItem mediaItem, Bundle extras) {
        PhotoBlockItem originalBlock = (PhotoBlockItem) extras.getParcelable("block");
        int positionInBlock = extras.getInt("pos_in_block", -1);
        if (originalBlock == null) {
            Logger.m184w("Extra block is missing");
        } else if (positionInBlock == -1) {
            Logger.m184w("Extra pos_in_block is missing");
        } else if (positionInBlock == 0) {
            super.insertText(originalBlock, extras);
        } else {
            ViewGroup photoBlockView = (ViewGroup) this.mediaComposerController.getItemView(originalBlock);
            if (photoBlockView == null) {
                Logger.m184w("Photo block view not found");
                return;
            }
            int[][] locationsOnScreen = new int[originalBlock.size()][];
            PhotoBlockItemAdapterHandler.getChildrenLocationsOnScreent(photoBlockView, locationsOnScreen, 0);
            int remove = this.mediaComposerController.remove(originalBlock, false, true);
            Pair<PhotoBlockItem, View>[] newBlocks = this.handler.breakIntoTwoBlocks(originalBlock, photoBlockView, positionInBlock - 1, (ViewGroup) photoBlockView.getParent());
            int position = remove + 1;
            this.mediaComposerController.insert((MediaItem) newBlocks[0].first, remove, (View) newBlocks[0].second);
            remove = position + 1;
            this.mediaComposerController.insert(MediaItem.emptyText(), position);
            position = remove + 1;
            this.mediaComposerController.insert((MediaItem) newBlocks[1].first, remove, (View) newBlocks[1].second);
            new SimpleLayoutTransitionAnimator((ViewGroup) newBlocks[1].second, 200, locationsOnScreen, positionInBlock).startAnimation();
        }
    }

    private void updatePositionsInExtras(ViewGroup container, int from, int to, int positionDelta) {
        for (int i = from; i <= to; i++) {
            Bundle extras = (Bundle) container.getChildAt(i).getTag(2131624309);
            if (extras != null) {
                extras.putInt("pos_in_block", extras.getInt("pos_in_block") + positionDelta);
            }
        }
    }

    private void relayoutChildren(ViewGroup container, int from, int to) {
        int baseViewId = container.getId() * 10000;
        for (int i = from; i <= to; i++) {
            View childView = container.getChildAt(i);
            PhotoBlockItemAdapterHandler.clearChildLayoutParams(childView);
            this.handler.updateChildLayoutParams(i, childView, baseViewId);
        }
    }
}
