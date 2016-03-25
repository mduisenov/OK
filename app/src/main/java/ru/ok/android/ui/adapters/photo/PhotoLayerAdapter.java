package ru.ok.android.ui.adapters.photo;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import java.util.List;
import java.util.Map;
import ru.ok.android.ui.custom.photo.ThrowAwayViewTouchHelper.OnDragListener;
import ru.ok.android.ui.custom.photo.ThrowAwayViewTouchHelper.OnThrowAwayListener;
import ru.ok.android.ui.image.PreviewUriCache;
import ru.ok.android.ui.image.view.DecorHandler;
import ru.ok.android.ui.image.view.PhotoLayerHelper;
import ru.ok.android.ui.image.view.ProgressSyncHelper;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.LruCache;
import ru.ok.android.utils.UIUtils;
import ru.ok.model.Identifiable;

public abstract class PhotoLayerAdapter<CONTAINER extends View> extends PagerAdapter implements OnDragListener, OnThrowAwayListener {
    protected boolean childLayoutListenerSet;
    private View currentView;
    protected final DecorHandler decorViewsHandler;
    protected Map<String, Integer> imageIdToVirtualPositionMap;
    protected List<PhotoAdapterListItem> images;
    protected OnThrowAwayListener mOnThrowAwayListener;
    protected OnDragListener onDragListener;
    protected OnFirstChildLayoutListener onFirstChildLayoutListener;
    protected ProgressSyncHelper progressSyncHelper;
    protected final int[] sizesHolder;
    private final LruCache<String, CONTAINER> viewCache;

    public interface PhotoAdapterListItem extends Parcelable {
        int getType();
    }

    public static abstract class PhotoListItem implements PhotoAdapterListItem, Identifiable {
        public int getType() {
            return 2;
        }
    }

    /* renamed from: ru.ok.android.ui.adapters.photo.PhotoLayerAdapter.1 */
    class C05931 implements OnGlobalLayoutListener {
        final /* synthetic */ View val$fView;

        C05931(View view) {
            this.val$fView = view;
        }

        public void onGlobalLayout() {
            UIUtils.removeOnGlobalLayoutListener(this.val$fView, this);
            if (PhotoLayerAdapter.this.onFirstChildLayoutListener != null) {
                PhotoLayerAdapter.this.onFirstChildLayoutListener.onFirstChildLayout();
                PhotoLayerAdapter.this.onFirstChildLayoutListener = null;
            }
        }
    }

    public interface OnFirstChildLayoutListener {
        void onFirstChildLayout();
    }

    public static final class TearListItem implements PhotoAdapterListItem {
        public static final Creator<TearListItem> CREATOR;

        /* renamed from: ru.ok.android.ui.adapters.photo.PhotoLayerAdapter.TearListItem.1 */
        static class C05941 implements Creator<TearListItem> {
            C05941() {
            }

            public TearListItem createFromParcel(Parcel source) {
                return new TearListItem();
            }

            public TearListItem[] newArray(int size) {
                return new TearListItem[size];
            }
        }

        public int getType() {
            return 1;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
        }

        static {
            CREATOR = new C05941();
        }
    }

    protected abstract void bindPhotoView(@NonNull CONTAINER container, @NonNull PhotoAdapterListItem photoAdapterListItem);

    protected abstract CONTAINER createPhotoView(@NonNull View view, @NonNull PhotoListItem photoListItem);

    public PhotoLayerAdapter(Context context, @NonNull DecorHandler decorViewsHandler, @NonNull List<PhotoAdapterListItem> images, @NonNull ProgressSyncHelper syncHelper) {
        this.viewCache = new LruCache(5);
        this.sizesHolder = new int[2];
        this.images = images;
        this.decorViewsHandler = decorViewsHandler;
        this.progressSyncHelper = syncHelper;
        updateImageIdToVirtualPositionMap();
        PhotoLayerHelper.getSizesForPhotos(context, this.sizesHolder);
    }

    public int getCount() {
        if (getRealCount() == 1) {
            return 1;
        }
        return 500;
    }

    public int getItemPosition(Object object) {
        PhotoAdapterListItem listItem = (PhotoAdapterListItem) ((View) object).getTag();
        if (2 != listItem.getType()) {
            return -2;
        }
        Integer virtualPosition = (Integer) this.imageIdToVirtualPositionMap.get(((PhotoListItem) listItem).getId());
        if (virtualPosition != null) {
            return virtualPosition.intValue();
        }
        return -2;
    }

    public int getRealCount() {
        return this.images.size();
    }

    public int getRealPosition(int virtualPosition) {
        int realCount = getRealCount();
        if (realCount <= 0) {
            return -1;
        }
        if (realCount == 1) {
            return 0;
        }
        int pos = (virtualPosition - 250) % realCount;
        if (pos < 0) {
            return pos + realCount;
        }
        return pos;
    }

    public int getVirtualPosition(int realPosition) {
        if (getRealCount() == 1) {
            return 0;
        }
        return realPosition + 250;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        int realPosition = getRealPosition(position);
        PhotoAdapterListItem listItem = (PhotoAdapterListItem) this.images.get(realPosition);
        View view = doInstantiateView(container, position, realPosition, listItem);
        view.setTag(listItem);
        setChildLayoutListener(view);
        LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams != null) {
            ViewPager.LayoutParams viewPagerLayoutParams = new ViewPager.LayoutParams();
            viewPagerLayoutParams.width = layoutParams.width;
            viewPagerLayoutParams.height = layoutParams.height;
            view.setLayoutParams(viewPagerLayoutParams);
        }
        container.addView(view);
        return view;
    }

    private View doInstantiateView(@NonNull ViewGroup container, int virtualPosition, int realPosition, @NonNull PhotoAdapterListItem listItem) {
        if (isTearListItem(listItem)) {
            return createTearView(container);
        }
        View photoView = createPhotoView(container, virtualPosition, realPosition, listItem);
        bindPhotoView(photoView, listItem);
        return photoView;
    }

    private CONTAINER createPhotoView(@NonNull ViewGroup container, int virtualPosition, int realPosition, @NonNull PhotoAdapterListItem photoListItem) {
        String id = ((Identifiable) photoListItem).getId();
        View photoView = (View) this.viewCache.get(id);
        if (photoView == null || photoView.getParent() != null) {
            CONTAINER photoView2 = createPhotoView(container, (PhotoListItem) photoListItem);
            Logger.m173d("Create view at pos = %d, realPos = %d with id = %s, viewId = %d", Integer.valueOf(virtualPosition), Integer.valueOf(realPosition), id, Integer.valueOf(System.identityHashCode(photoView2)));
            this.viewCache.put(id, photoView2);
            return photoView2;
        }
        Logger.m173d("PhotoLayer: Reuse view at pos = %d, realPos = %d with id = %s", Integer.valueOf(virtualPosition), Integer.valueOf(realPosition), id);
        return photoView;
    }

    public void notifyDataSetChanged() {
        updateImageIdToVirtualPositionMap();
        super.notifyDataSetChanged();
    }

    public void notifyItemChanged(String id) {
        View view = (View) this.viewCache.get(id);
        if (view != null) {
            Integer virtualPosition = (Integer) this.imageIdToVirtualPositionMap.get(id);
            if (virtualPosition != null) {
                bindPhotoView(view, (PhotoAdapterListItem) this.images.get(getRealPosition(virtualPosition.intValue())));
            }
        }
    }

    private void updateImageIdToVirtualPositionMap() {
        ArrayMap<String, Integer> idToVirtualPositionMap = new ArrayMap(this.images.size());
        int n = this.images.size();
        for (int i = 0; i < n; i++) {
            PhotoAdapterListItem item = (PhotoAdapterListItem) this.images.get(i);
            if (2 == item.getType()) {
                idToVirtualPositionMap.put(((PhotoListItem) item).getId(), Integer.valueOf(getVirtualPosition(i)));
            }
        }
        this.imageIdToVirtualPositionMap = idToVirtualPositionMap;
    }

    private void setChildLayoutListener(View view) {
        if (!this.childLayoutListenerSet) {
            this.childLayoutListenerSet = true;
            view.getViewTreeObserver().addOnGlobalLayoutListener(new C05931(view));
        }
    }

    private boolean isTearListItem(@NonNull PhotoAdapterListItem photoListItem) {
        return photoListItem.getType() != 2;
    }

    private View createTearView(ViewGroup container) {
        return LayoutInflater.from(container.getContext()).inflate(2130903386, container, false);
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        Logger.m173d("PhotoLayer: Destroy view at pos = %d, realPos = %d, viewId = %d", Integer.valueOf(position), Integer.valueOf(getRealPosition(position)), Integer.valueOf(System.identityHashCode(view)));
    }

    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        this.currentView = (View) object;
        super.setPrimaryItem(container, position, object);
    }

    public void onThrowAway(boolean up) {
        if (this.mOnThrowAwayListener != null) {
            this.mOnThrowAwayListener.onThrowAway(up);
        }
    }

    public void onStartDrag() {
        if (this.onDragListener != null) {
            this.onDragListener.onStartDrag();
        }
    }

    public void onFinishDrag() {
        if (this.onDragListener != null) {
            this.onDragListener.onFinishDrag();
        }
    }

    public final View getCurrentView() {
        return this.currentView;
    }

    public void setOnThrowAwayListener(OnThrowAwayListener onThrowAwayListener) {
        this.mOnThrowAwayListener = onThrowAwayListener;
    }

    public void setOnFirstChildLayoutListener(OnFirstChildLayoutListener onFirstChildLayoutListener) {
        this.onFirstChildLayoutListener = onFirstChildLayoutListener;
    }

    public void setOnDragListener(OnDragListener onDragListener) {
        this.onDragListener = onDragListener;
    }

    protected Uri processPreviewUri(Uri imageUri, @Nullable Uri previewUri) {
        if (previewUri == null) {
            return PreviewUriCache.getInstance().get(imageUri);
        }
        PreviewUriCache.getInstance().put(imageUri, previewUri);
        return previewUri;
    }
}
