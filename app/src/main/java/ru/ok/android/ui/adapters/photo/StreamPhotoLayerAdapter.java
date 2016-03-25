package ru.ok.android.ui.adapters.photo;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import ru.ok.android.app.GifAsMp4PlayerHelper;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.model.image.PhotoOwner;
import ru.ok.android.services.marks.MarksManager;
import ru.ok.android.storage.Storages;
import ru.ok.android.ui.adapters.photo.PhotoLayerAdapter.PhotoAdapterListItem;
import ru.ok.android.ui.adapters.photo.PhotoLayerAdapter.PhotoListItem;
import ru.ok.android.ui.custom.photo.AbstractPhotoInfoView;
import ru.ok.android.ui.custom.photo.AbstractPhotoInfoView.OnPhotoActionListener;
import ru.ok.android.ui.custom.photo.AbstractPhotoInfoView.PhotoControlsState;
import ru.ok.android.ui.custom.photo.GifAsMp4PhotoInfoView;
import ru.ok.android.ui.custom.photo.StaticPhotoInfoView;
import ru.ok.android.ui.image.PreviewDataHolder;
import ru.ok.android.ui.image.view.DecorHandler;
import ru.ok.android.ui.image.view.PhotoInfoProvider;
import ru.ok.android.ui.image.view.ProgressSyncHelper;
import ru.ok.android.utils.Logger;
import ru.ok.model.UserInfo;
import ru.ok.model.photo.PhotoAlbumInfo.OwnerType;
import ru.ok.model.photo.PhotoInfo;
import ru.ok.model.photo.PhotoInfo.PhotoContext;
import ru.ok.model.stream.LikeInfoContext;

public class StreamPhotoLayerAdapter extends PhotoLayerAdapter<AbstractPhotoInfoView> implements OnPhotoActionListener {
    private final PhotoViewStrategy gifAsMp4PhotoViewStrategy;
    protected OnPhotoActionListener mOnPhotoActionListener;
    private final MarksManager marksManager;
    protected final PhotoInfoProvider photoInfoProvider;
    protected final PhotoOwner photoOwner;
    @Nullable
    private final PreviewDataHolder previewDataHolder;
    private final PhotoViewStrategy staticPhotoViewStrategy;

    private interface PhotoViewStrategy {
        void bind(@NonNull AbstractPhotoInfoView abstractPhotoInfoView, @NonNull PhotoInfo photoInfo);

        AbstractPhotoInfoView createView(@NonNull Context context, PhotoInfo photoInfo);
    }

    private class GifAsMp4PhotoViewStrategy implements PhotoViewStrategy {
        private GifAsMp4PhotoViewStrategy() {
        }

        public AbstractPhotoInfoView createView(@NonNull Context context, PhotoInfo photoInfo) {
            return new GifAsMp4PhotoInfoView(context);
        }

        public void bind(@NonNull AbstractPhotoInfoView photoView, @NonNull PhotoInfo photoInfo) {
            ((GifAsMp4PhotoInfoView) photoView).bindPhotoInfo(photoInfo, StreamPhotoLayerAdapter.this.sizesHolder);
        }
    }

    public static class PhotoInfoListItem extends PhotoListItem {
        public static final Creator<PhotoInfoListItem> CREATOR;
        private PhotoInfo photoInfo;

        /* renamed from: ru.ok.android.ui.adapters.photo.StreamPhotoLayerAdapter.PhotoInfoListItem.1 */
        static class C05961 implements Creator<PhotoInfoListItem> {
            C05961() {
            }

            public PhotoInfoListItem createFromParcel(Parcel source) {
                PhotoInfoListItem photoItem = new PhotoInfoListItem();
                photoItem.readFromParcel(source);
                return photoItem;
            }

            public PhotoInfoListItem[] newArray(int size) {
                return new PhotoInfoListItem[size];
            }
        }

        public PhotoInfoListItem(PhotoInfo photoInfo) {
            this.photoInfo = photoInfo;
        }

        public PhotoInfo getPhotoInfo() {
            return this.photoInfo;
        }

        public void setPhotoInfo(PhotoInfo photoInfo) {
            this.photoInfo = photoInfo;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.photoInfo, 0);
        }

        public void readFromParcel(Parcel src) {
            this.photoInfo = (PhotoInfo) src.readParcelable(PhotoInfo.class.getClassLoader());
        }

        static {
            CREATOR = new C05961();
        }

        public String getId() {
            return this.photoInfo.getId();
        }
    }

    private class StaticPhotoViewStrategy implements PhotoViewStrategy {
        private StaticPhotoViewStrategy() {
        }

        public AbstractPhotoInfoView createView(@NonNull Context context, PhotoInfo photoInfo) {
            StaticPhotoInfoView photoInfoView = new StaticPhotoInfoView(context);
            photoInfoView.initHierarchy(StreamPhotoLayerAdapter.this.previewDataHolder != null ? StreamPhotoLayerAdapter.this.previewDataHolder.getRefIfMatch(StreamPhotoLayerAdapter.this.processPreviewUri(Uri.parse(photoInfo.getClosestSizeUrl(StreamPhotoLayerAdapter.this.sizesHolder[0], StreamPhotoLayerAdapter.this.sizesHolder[1])), photoInfo.getPreviewUri())) : null);
            return photoInfoView;
        }

        public void bind(@NonNull AbstractPhotoInfoView photoView, @NonNull PhotoInfo photoInfo) {
            boolean z = true;
            String urlToLoad = photoInfo.getClosestSizeUrl(StreamPhotoLayerAdapter.this.sizesHolder[0], StreamPhotoLayerAdapter.this.sizesHolder[1]);
            if (urlToLoad != null) {
                StaticPhotoInfoView staticPhotoInfoView = (StaticPhotoInfoView) photoView;
                Uri uri = Uri.parse(urlToLoad);
                Uri previewUri = photoInfo.getPreviewUri();
                String str = "Bind. Has preview uri? %s";
                Object[] objArr = new Object[1];
                if (previewUri == null) {
                    z = false;
                }
                objArr[0] = Boolean.valueOf(z);
                Logger.m173d(str, objArr);
                staticPhotoInfoView.setPhotoUri(uri, StreamPhotoLayerAdapter.this.processPreviewUri(uri, previewUri));
            }
        }
    }

    public StreamPhotoLayerAdapter(@NonNull Context context, @NonNull PhotoInfoProvider photoInfoProvider, @NonNull DecorHandler decorViewsHandler, @NonNull List<PhotoAdapterListItem> images, @Nullable PhotoOwner photoOwner, @NonNull ProgressSyncHelper syncHelper, @Nullable PreviewDataHolder previewDataHolder) {
        super(context, decorViewsHandler, images, syncHelper);
        this.staticPhotoViewStrategy = new StaticPhotoViewStrategy();
        this.gifAsMp4PhotoViewStrategy = new GifAsMp4PhotoViewStrategy();
        this.photoInfoProvider = photoInfoProvider;
        this.photoOwner = photoOwner;
        this.previewDataHolder = previewDataHolder;
        this.marksManager = Storages.getInstance(context, OdnoklassnikiApplication.getCurrentUser().getId()).getMarksManager();
    }

    protected void bindPhotoView(@NonNull AbstractPhotoInfoView photoView, @NonNull PhotoAdapterListItem item) {
        boolean z;
        boolean z2 = true;
        PhotoInfo photoInfo = ((PhotoInfoListItem) item).getPhotoInfo();
        PhotoViewStrategy viewStrategy = pickViewStrategy(photoInfo);
        photoView.setDecorViewsHandler(this.decorViewsHandler);
        photoView.setOnThrowAwayListener(this);
        photoView.setOnPhotoActionListener(this);
        photoView.setOnDragListener(this);
        photoView.setPhotoInfo(photoInfo);
        viewStrategy.bind(photoView, photoInfo);
        photoView.setCommentsCount(photoInfo.getAnyCommentsCount());
        photoView.setComment(photoInfo.getComment());
        photoView.setUserMark(this.marksManager.getSyncedUserPhotoMark(photoInfo.getId(), photoInfo.getViewerMark()));
        LikeInfoContext likeInfo = photoInfo.getLikeInfo();
        if (likeInfo != null) {
            photoView.setLikeInfo(likeInfo, false);
        }
        if (photoInfo.getDiscussionSummary() != null) {
            z = true;
        } else {
            z = false;
        }
        if (likeInfo == null) {
            z2 = false;
        }
        photoView.setState(getPhotoInfoState(photoInfo), new PhotoControlsState(z, z2, photoInfo.isMarkAllowed()));
        photoView.setProgress(this.progressSyncHelper.getSpinProgress());
        this.progressSyncHelper.registerPivotView(photoView.getProgressView());
    }

    protected AbstractPhotoInfoView createPhotoView(@NonNull View container, @NonNull PhotoListItem item) {
        PhotoInfo photoInfo = ((PhotoInfoListItem) item).getPhotoInfo();
        return pickViewStrategy(photoInfo).createView(container.getContext(), photoInfo);
    }

    public static int getPhotoInfoState(PhotoInfo photoInfo) {
        PhotoContext photoContext = photoInfo.getPhotoContext();
        if (photoContext == null) {
            return 3;
        }
        if (photoContext != PhotoContext.MEDIATOPIC) {
            return 0;
        }
        return photoInfo.getOwnerType() == OwnerType.GROUP ? 1 : 2;
    }

    private PhotoViewStrategy pickViewStrategy(@NonNull PhotoInfo photoInfo) {
        return GifAsMp4PlayerHelper.shouldShowGifAsMp4(photoInfo) ? this.gifAsMp4PhotoViewStrategy : this.staticPhotoViewStrategy;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        View view = (View) object;
        if (view instanceof StaticPhotoInfoView) {
            this.photoInfoProvider.removeOnPhototagsReceivedListener((StaticPhotoInfoView) view);
        }
    }

    public void onMark(String photoId, int mark) {
        if (this.mOnPhotoActionListener != null) {
            this.mOnPhotoActionListener.onMark(photoId, mark);
        }
    }

    public void onLikesCountClicked(View view, String photoId, LikeInfoContext likeInfo) {
        if (this.mOnPhotoActionListener != null) {
            this.mOnPhotoActionListener.onLikesCountClicked(view, photoId, likeInfo);
        }
    }

    public void onLikeClicked(String pid, LikeInfoContext likeInfo) {
        if (this.mOnPhotoActionListener != null) {
            this.mOnPhotoActionListener.onLikeClicked(pid, likeInfo);
        }
    }

    public void onUnlikeClicked(String pid, LikeInfoContext likeInfo) {
        if (this.mOnPhotoActionListener != null) {
            this.mOnPhotoActionListener.onUnlikeClicked(pid, likeInfo);
        }
    }

    public void onUserClicked(UserInfo userInfo) {
        if (this.mOnPhotoActionListener != null) {
            this.mOnPhotoActionListener.onUserClicked(userInfo);
        }
    }

    public void onCommentsClicked(View view, String pid) {
        if (this.mOnPhotoActionListener != null) {
            this.mOnPhotoActionListener.onCommentsClicked(view, pid);
        }
    }

    public void setOnPhotoActionListener(OnPhotoActionListener onPhotoActionListener) {
        this.mOnPhotoActionListener = onPhotoActionListener;
    }

    public void setItems(@NonNull List<PhotoAdapterListItem> items) {
        this.images.clear();
        this.images.addAll(items);
        notifyDataSetChanged();
    }
}
