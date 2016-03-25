package ru.ok.android.ui.custom.mediacomposer.adapter;

import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.custom.mediacomposer.EditablePhotoItem;
import ru.ok.android.ui.custom.mediacomposer.FragmentBridge;
import ru.ok.android.ui.custom.mediacomposer.LinkItem;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerStyleParams;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerView.MediaComposerController;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerView.ScaleToInsertInfo;
import ru.ok.android.ui.custom.mediacomposer.MediaItem;
import ru.ok.android.ui.custom.mediacomposer.MediaItemActionProvider;
import ru.ok.android.ui.custom.mediacomposer.MusicItem;
import ru.ok.android.ui.custom.mediacomposer.PhotoBlockItem;
import ru.ok.android.ui.custom.mediacomposer.PlaceItem;
import ru.ok.android.ui.custom.mediacomposer.PollItem;
import ru.ok.android.ui.custom.mediacomposer.TextItem;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.request.mediatopic.MediaTopicType;
import ru.ok.model.mediatopics.MediaItemType;

public class MediaItemAdapter {
    private final Context context;
    private final ImageHandler imageHandler;
    private final LinkItemAdapterHandler linkHandler;
    private final LocalizationManager localizationManager;
    private final MusicItemAdapterHandler musicHandler;
    private final PhotoBlockItemAdapterHandler photoBlockHandler;
    private final PhotoItemAdapterHandler photoHandler;
    private final PlaceItemAdapterHandler placeHandler;
    private final PollItemAdapterHandler pollHandler;
    private final MediaComposerStyleParams styleParams;
    private final TextItemAdapterHandler textHandler;
    private final UnsupportedItemAdapterHandler unsupportedHandler;

    /* renamed from: ru.ok.android.ui.custom.mediacomposer.adapter.MediaItemAdapter.1 */
    static /* synthetic */ class C06871 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$model$mediatopics$MediaItemType;

        static {
            $SwitchMap$ru$ok$model$mediatopics$MediaItemType = new int[MediaItemType.values().length];
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.PHOTO_BLOCK.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.PHOTO.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.TEXT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.MUSIC.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.POLL.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.LINK.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$ru$ok$model$mediatopics$MediaItemType[MediaItemType.PLACE.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    public MediaItemAdapter(Context context, MediaComposerStyleParams styleParams, FragmentBridge fragmentBridge, MediaComposerController mediaComposerController, MediaTopicType mediaTopicType) {
        this.context = context;
        this.localizationManager = LocalizationManager.from(context);
        this.styleParams = styleParams;
        this.imageHandler = new ImageHandler(context, styleParams);
        this.textHandler = new TextItemAdapterHandler(context, this.localizationManager, fragmentBridge, mediaComposerController, mediaTopicType, styleParams, this.imageHandler);
        this.photoHandler = new PhotoItemAdapterHandler(context, this.localizationManager, fragmentBridge, mediaComposerController, mediaTopicType, styleParams, this.imageHandler);
        this.photoBlockHandler = new PhotoBlockItemAdapterHandler(context, this.localizationManager, fragmentBridge, mediaComposerController, mediaTopicType, styleParams, this.imageHandler);
        this.musicHandler = new MusicItemAdapterHandler(context, this.localizationManager, fragmentBridge, mediaComposerController, mediaTopicType, styleParams, this.imageHandler);
        this.pollHandler = new PollItemAdapterHandler(context, this.localizationManager, fragmentBridge, mediaComposerController, mediaTopicType, styleParams, this.imageHandler);
        this.linkHandler = new LinkItemAdapterHandler(context, this.localizationManager, fragmentBridge, mediaComposerController, mediaTopicType, styleParams, this.imageHandler);
        this.unsupportedHandler = new UnsupportedItemAdapterHandler(context, this.localizationManager, fragmentBridge, mediaComposerController, mediaTopicType, styleParams, this.imageHandler);
        this.placeHandler = new PlaceItemAdapterHandler(context, this.localizationManager, fragmentBridge, mediaComposerController, mediaTopicType, styleParams, this.imageHandler);
    }

    public View createView(MediaItem mediaItem, ViewGroup parent, boolean isEditable, int viewId) {
        switch (C06871.$SwitchMap$ru$ok$model$mediatopics$MediaItemType[mediaItem.type.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return this.photoBlockHandler.createView((PhotoBlockItem) mediaItem, parent, isEditable, viewId);
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return this.photoHandler.createView((EditablePhotoItem) mediaItem, parent, isEditable, viewId);
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return this.textHandler.createView((TextItem) mediaItem, parent, isEditable, viewId);
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                return this.musicHandler.createView((MusicItem) mediaItem, parent, isEditable, viewId);
            case Message.UUID_FIELD_NUMBER /*5*/:
                return this.pollHandler.createView((PollItem) mediaItem, parent, isEditable, viewId);
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                return this.linkHandler.createView((LinkItem) mediaItem, parent, isEditable, viewId);
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
                return this.placeHandler.createView((PlaceItem) mediaItem, parent, isEditable, viewId);
            default:
                return this.unsupportedHandler.createView(mediaItem, parent, isEditable, viewId);
        }
    }

    public void updateViewEdiatable(View view, MediaItem mediaItem, ViewGroup parent, boolean isEditable) {
        switch (C06871.$SwitchMap$ru$ok$model$mediatopics$MediaItemType[mediaItem.type.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                this.photoBlockHandler.updateViewIsEditable(view, (PhotoBlockItem) mediaItem, parent, isEditable);
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                this.photoHandler.updateViewIsEditable(view, (EditablePhotoItem) mediaItem, parent, isEditable);
            case Message.TYPE_FIELD_NUMBER /*3*/:
                this.textHandler.updateViewIsEditable(view, (TextItem) mediaItem, parent, isEditable);
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                this.musicHandler.updateViewIsEditable(view, (MusicItem) mediaItem, parent, isEditable);
            case Message.UUID_FIELD_NUMBER /*5*/:
                this.pollHandler.updateViewIsEditable(view, (PollItem) mediaItem, parent, isEditable);
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                this.linkHandler.updateViewIsEditable(view, (LinkItem) mediaItem, parent, isEditable);
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
                this.placeHandler.updateViewIsEditable(view, (PlaceItem) mediaItem, parent, isEditable);
            default:
                this.unsupportedHandler.updateViewIsEditable(view, mediaItem, parent, isEditable);
        }
    }

    public void disposeView(View view, MediaItem mediaItem) {
        switch (C06871.$SwitchMap$ru$ok$model$mediatopics$MediaItemType[mediaItem.type.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                this.photoBlockHandler.disposeView(view, (PhotoBlockItem) mediaItem);
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                this.photoHandler.disposeView(view, (EditablePhotoItem) mediaItem);
            case Message.TYPE_FIELD_NUMBER /*3*/:
                this.textHandler.disposeView(view, (TextItem) mediaItem);
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                this.musicHandler.disposeView(view, (MusicItem) mediaItem);
            case Message.UUID_FIELD_NUMBER /*5*/:
                this.pollHandler.disposeView(view, (PollItem) mediaItem);
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                this.linkHandler.disposeView(view, (LinkItem) mediaItem);
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
                this.placeHandler.disposeView(view, (PlaceItem) mediaItem);
            default:
                this.unsupportedHandler.disposeView(view, mediaItem);
        }
    }

    public void startMediaAdd(MediaItemType mediaType, Bundle extras) {
        MediaItemAdapterHandler handler = getHandler(mediaType);
        MediaItemActionProvider provider = handler == null ? null : handler.getActionProvider();
        if (provider != null) {
            provider.startMediaAdd(extras);
        }
    }

    private MediaItemAdapterHandler getHandler(MediaItemType type) {
        switch (C06871.$SwitchMap$ru$ok$model$mediatopics$MediaItemType[type.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return this.photoBlockHandler;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return this.photoHandler;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return this.textHandler;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                return this.musicHandler;
            case Message.UUID_FIELD_NUMBER /*5*/:
                return this.pollHandler;
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                return this.linkHandler;
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
                return this.placeHandler;
            default:
                return this.unsupportedHandler;
        }
    }

    public void onDestroy() {
    }

    public void onDetachedFromWindow() {
        this.imageHandler.stop();
    }

    public ScaleToInsertInfo canScaleToInsert(MediaItem mediaItem, View view, float x1, float y1, float x2, float y2) {
        MediaItemAdapterHandler handler = getHandler(mediaItem.type);
        if (handler != null) {
            return handler.canScaleToInsert(mediaItem, view, x1, y1, x2, y2);
        }
        return null;
    }

    public void translateChildren(ScaleToInsertInfo info, float beforeTranslationY, float afterTranslationY) {
        MediaItemAdapterHandler handler = getHandler(info.item1.type);
        if (handler != null) {
            handler.translateChildren(info, beforeTranslationY, afterTranslationY);
        }
    }

    public void animateChildren(ScaleToInsertInfo info, float finalTranlationY, AnimatorListener listener) {
        MediaItemAdapterHandler handler = getHandler(info.item1.type);
        if (handler != null) {
            handler.animateChildren(info, finalTranlationY, listener);
        }
    }

    public void insertItem(ScaleToInsertInfo info, MediaItem newItem) {
        MediaItemAdapterHandler handler = getHandler(info.item1.type);
        if (handler != null) {
            handler.insertItem(info, newItem);
        }
    }

    public void onNeighboursChanged(MediaItem mediaItem, View view, int position) {
        switch (C06871.$SwitchMap$ru$ok$model$mediatopics$MediaItemType[mediaItem.type.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                this.photoBlockHandler.onNeighboursChanged((PhotoBlockItem) mediaItem, view, position);
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                this.photoHandler.onNeighboursChanged((EditablePhotoItem) mediaItem, view, position);
            case Message.TYPE_FIELD_NUMBER /*3*/:
                this.textHandler.onNeighboursChanged((TextItem) mediaItem, view, position);
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                this.musicHandler.onNeighboursChanged((MusicItem) mediaItem, view, position);
            case Message.UUID_FIELD_NUMBER /*5*/:
                this.pollHandler.onNeighboursChanged((PollItem) mediaItem, view, position);
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                this.linkHandler.onNeighboursChanged((LinkItem) mediaItem, view, position);
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
                this.placeHandler.onNeighboursChanged((PlaceItem) mediaItem, view, position);
            default:
                this.unsupportedHandler.onNeighboursChanged(mediaItem, view, position);
        }
    }

    public void mergeItems(int itemPosition1, MediaItem item1, View itemView1, int itemPosition2, MediaItem item2, View itemView2, MediaItem mergedItem) {
        if (item1.type == item2.type && item1.type == mergedItem.type) {
            switch (C06871.$SwitchMap$ru$ok$model$mediatopics$MediaItemType[mergedItem.type.ordinal()]) {
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    this.photoBlockHandler.mergeItems(itemPosition1, (PhotoBlockItem) item1, itemView1, itemPosition2, (PhotoBlockItem) item2, itemView2, (PhotoBlockItem) mergedItem);
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    this.photoHandler.mergeItems(itemPosition1, (EditablePhotoItem) item1, itemView1, itemPosition2, (EditablePhotoItem) item2, itemView2, (EditablePhotoItem) mergedItem);
                case Message.TYPE_FIELD_NUMBER /*3*/:
                    this.textHandler.mergeItems(itemPosition1, (TextItem) item1, itemView1, itemPosition2, (TextItem) item2, itemView2, (TextItem) mergedItem);
                case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                    this.musicHandler.mergeItems(itemPosition1, (MusicItem) item1, itemView1, itemPosition2, (MusicItem) item2, itemView2, (MusicItem) mergedItem);
                case Message.UUID_FIELD_NUMBER /*5*/:
                    this.pollHandler.mergeItems(itemPosition1, (PollItem) item1, itemView1, itemPosition2, (PollItem) item2, itemView2, (PollItem) mergedItem);
                case Message.REPLYTO_FIELD_NUMBER /*6*/:
                    this.linkHandler.mergeItems(itemPosition1, (LinkItem) item1, itemView1, itemPosition2, (LinkItem) item2, itemView2, (LinkItem) mergedItem);
                case Message.ATTACHES_FIELD_NUMBER /*7*/:
                    this.placeHandler.mergeItems(itemPosition1, (PlaceItem) item1, itemView1, itemPosition2, (PlaceItem) item2, itemView2, (PlaceItem) mergedItem);
                default:
                    this.unsupportedHandler.mergeItems(itemPosition1, item1, itemView1, itemPosition2, item2, itemView2, mergedItem);
            }
        }
    }
}
