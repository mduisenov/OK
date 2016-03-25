package ru.ok.android.ui.adapters.photo;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import com.facebook.common.references.CloseableReference;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.app.GifAsMp4PlayerHelper;
import ru.ok.android.ui.adapters.photo.PhotoLayerAdapter.PhotoAdapterListItem;
import ru.ok.android.ui.adapters.photo.PhotoLayerAdapter.PhotoListItem;
import ru.ok.android.ui.custom.photo.AbstractAttachPhotoView;
import ru.ok.android.ui.custom.photo.GifAsMp4AttachPhotoView;
import ru.ok.android.ui.custom.photo.StaticAttachPhotoView;
import ru.ok.android.ui.image.PreviewDataHolder;
import ru.ok.android.ui.image.view.DecorHandler;
import ru.ok.android.ui.image.view.ProgressSyncHelper;
import ru.ok.android.utils.PhotoUtil;
import ru.ok.model.messages.Attachment;
import ru.ok.model.photo.PhotoSize;

public class AttachPhotoLayerAdapter extends PhotoLayerAdapter<AbstractAttachPhotoView> {
    private final AttachViewStrategy gifAsMp4AttachViewStrategy;
    @Nullable
    private final PreviewDataHolder previewDataHolder;
    private final AttachViewStrategy staticAttachViewStrategy;

    private interface AttachViewStrategy {
        void bind(@NonNull AbstractAttachPhotoView abstractAttachPhotoView, @NonNull Attachment attachment);

        AbstractAttachPhotoView createView(@NonNull Context context, Attachment attachment);
    }

    public static class AttachmentListItem extends PhotoListItem {
        public static final Creator<AttachmentListItem> CREATOR;
        private Attachment attachment;

        /* renamed from: ru.ok.android.ui.adapters.photo.AttachPhotoLayerAdapter.AttachmentListItem.1 */
        static class C05911 implements Creator<AttachmentListItem> {
            C05911() {
            }

            public AttachmentListItem createFromParcel(Parcel source) {
                AttachmentListItem attachment = new AttachmentListItem();
                attachment.readFromParcel(source);
                return attachment;
            }

            public AttachmentListItem[] newArray(int size) {
                return new AttachmentListItem[size];
            }
        }

        public AttachmentListItem(Attachment attachment) {
            this.attachment = attachment;
        }

        public Attachment getAttachment() {
            return this.attachment;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.attachment, 0);
        }

        public void readFromParcel(Parcel src) {
            this.attachment = (Attachment) src.readParcelable(Attachment.class.getClassLoader());
        }

        static {
            CREATOR = new C05911();
        }

        public String toString() {
            return "AttachmentItem[" + this.attachment + "]";
        }

        public String getId() {
            return this.attachment.getId();
        }
    }

    private class GifAsMp4AttachViewStrategy implements AttachViewStrategy {
        private GifAsMp4AttachViewStrategy() {
        }

        public AbstractAttachPhotoView createView(@NonNull Context context, Attachment attachment) {
            return new GifAsMp4AttachPhotoView(context);
        }

        public void bind(@NonNull AbstractAttachPhotoView attachView, @NonNull Attachment attachment) {
            ((GifAsMp4AttachPhotoView) attachView).bindAttachment(attachment, AttachPhotoLayerAdapter.this.sizesHolder);
        }
    }

    private class StaticAttachViewStrategy implements AttachViewStrategy {
        private StaticAttachViewStrategy() {
        }

        public AbstractAttachPhotoView createView(@NonNull Context context, Attachment attachment) {
            CloseableReference closeableReference = null;
            StaticAttachPhotoView photoView = new StaticAttachPhotoView(context);
            Uri uri = getAttachUri(attachment);
            if (uri != null) {
                Uri previewUri = AttachPhotoLayerAdapter.this.processPreviewUri(uri, attachment.getPreviewUri());
                if (AttachPhotoLayerAdapter.this.previewDataHolder != null) {
                    closeableReference = AttachPhotoLayerAdapter.this.previewDataHolder.getRefIfMatch(previewUri);
                }
                photoView.initHierarchy(closeableReference);
            } else {
                photoView.initHierarchy(null);
            }
            return photoView;
        }

        public void bind(@NonNull AbstractAttachPhotoView attachView, @NonNull Attachment attachment) {
            Uri uri = getAttachUri(attachment);
            if (uri != null) {
                ((StaticAttachPhotoView) attachView).setImageUri(uri, AttachPhotoLayerAdapter.this.processPreviewUri(uri, attachment.getPreviewUri()));
            }
        }

        private Uri getAttachUri(Attachment attachment) {
            if (attachment.getUri() != null) {
                return attachment.getUri();
            }
            PhotoSize toLoad = PhotoUtil.getClosestSize(AttachPhotoLayerAdapter.this.sizesHolder[0], AttachPhotoLayerAdapter.this.sizesHolder[1], attachment.sizes);
            if (toLoad != null) {
                return toLoad.getUri();
            }
            return null;
        }
    }

    public AttachPhotoLayerAdapter(@NonNull Context context, @NonNull DecorHandler decorViewsHandler, @NonNull List<Attachment> attachments, @NonNull ProgressSyncHelper syncHelper, @Nullable PreviewDataHolder previewDataHolder) {
        super(context, decorViewsHandler, toImageList(attachments), syncHelper);
        this.staticAttachViewStrategy = new StaticAttachViewStrategy();
        this.gifAsMp4AttachViewStrategy = new GifAsMp4AttachViewStrategy();
        this.previewDataHolder = previewDataHolder;
    }

    @NonNull
    private static List<PhotoAdapterListItem> toImageList(@NonNull List<Attachment> attachments) {
        List<PhotoAdapterListItem> images = new ArrayList();
        for (Attachment attachment : attachments) {
            images.add(new AttachmentListItem(attachment));
        }
        return images;
    }

    protected void bindPhotoView(@NonNull AbstractAttachPhotoView attachView, @NonNull PhotoAdapterListItem item) {
        Attachment attachment = ((AttachmentListItem) item).getAttachment();
        AttachViewStrategy viewStrategy = pickViewStrategy(attachment);
        attachView.setDecorViewsHandler(this.decorViewsHandler);
        attachView.setOnThrowAwayListener(this);
        attachView.setOnDragListener(this);
        viewStrategy.bind(attachView, attachment);
        attachView.setProgress(this.progressSyncHelper.getSpinProgress());
        this.progressSyncHelper.registerPivotView(attachView.getProgressView());
    }

    protected AbstractAttachPhotoView createPhotoView(@NonNull View container, @NonNull PhotoListItem item) {
        Attachment attachment = ((AttachmentListItem) item).getAttachment();
        return pickViewStrategy(attachment).createView(container.getContext(), attachment);
    }

    private AttachViewStrategy pickViewStrategy(@NonNull Attachment attachment) {
        return GifAsMp4PlayerHelper.shouldShowGifAsMp4(attachment) ? this.gifAsMp4AttachViewStrategy : this.staticAttachViewStrategy;
    }
}
