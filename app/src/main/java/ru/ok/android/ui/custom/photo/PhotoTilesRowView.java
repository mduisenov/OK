package ru.ok.android.ui.custom.photo;

import android.content.Context;
import android.net.Uri;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import java.util.List;
import ru.ok.android.ui.custom.photo.PhotoTileView.OnPhotoTileClickListener;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.model.photo.PhotoInfo;

public final class PhotoTilesRowView extends ViewGroup {
    private PhotoInfo bigPhotoInfo;
    private int columnCount;
    private final int minTileWidth;
    protected OnPhotoTileClickListener onPhotoTileClickListener;
    private PhotoInfo[] photoInfos;

    /* renamed from: ru.ok.android.ui.custom.photo.PhotoTilesRowView.1 */
    class C07261 implements Runnable {
        final /* synthetic */ PhotoTileView val$tileView;

        C07261(PhotoTileView photoTileView) {
            this.val$tileView = photoTileView;
        }

        public void run() {
            this.val$tileView.invalidate();
        }
    }

    public PhotoTilesRowView(Context context, int tileSize) {
        super(context);
        this.minTileWidth = tileSize;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int tileSize = width / this.columnCount;
        int tileSizeSpec = MeasureSpec.makeMeasureSpec(tileSize, 1073741824);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            PhotoTileView tileView = (PhotoTileView) getChildAt(i);
            if (tileView.getPhotoInfo() == this.bigPhotoInfo) {
                int bigTileSizeSpec = MeasureSpec.makeMeasureSpec(tileSize * 2, 1073741824);
                tileView.measure(bigTileSizeSpec, bigTileSizeSpec);
            } else {
                tileView.measure(tileSizeSpec, tileSizeSpec);
            }
        }
        if (this.bigPhotoInfo == null) {
            setMeasuredDimension(width, tileSize);
        } else {
            setMeasuredDimension(width, tileSize * 2);
        }
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (this.bigPhotoInfo == null) {
            layoutSingleRow();
        } else {
            layoutDoubleRow();
        }
    }

    private void layoutSingleRow() {
        int childCount = getChildCount();
        int totalWidth = getMeasuredWidth();
        int tileSize = (int) Math.floor((double) (totalWidth / this.columnCount));
        int reminder = (totalWidth - (this.columnCount * tileSize)) / 2;
        int tileLeft = reminder;
        int tileRight = tileSize + reminder;
        int tileBottom = tileSize;
        for (int i = 0; i < childCount; i++) {
            ((PhotoTileView) getChildAt(i)).layout(tileLeft, 0, tileRight, tileBottom);
            tileLeft = tileRight;
            tileRight = tileLeft + tileSize;
        }
    }

    private void layoutDoubleRow() {
        int childCount = getChildCount();
        int totalWidth = getMeasuredWidth();
        int tileSize = (int) Math.floor((double) (totalWidth / this.columnCount));
        int doubleTileSize = tileSize * 2;
        int reminder = (totalWidth - (this.columnCount * tileSize)) / 2;
        int tileLeft = reminder;
        int tileTop = 0;
        int nextRow = this.columnCount - 1;
        boolean bigPhotoPassed = false;
        int i = 0;
        while (i < childCount) {
            int tileRight;
            int tileBottom;
            PhotoTileView tileView = (PhotoTileView) getChildAt(i);
            if (i == nextRow) {
                tileTop += tileSize;
                tileLeft = reminder;
            }
            if (!bigPhotoPassed && i >= nextRow) {
                int potentialBig = (i - this.columnCount) + 1;
                if (this.photoInfos[potentialBig] == this.bigPhotoInfo) {
                    bigPhotoPassed = true;
                    tileLeft += doubleTileSize;
                }
            }
            if (tileView.isDisplayDoubleSize()) {
                tileRight = tileLeft + doubleTileSize;
                tileBottom = tileTop + doubleTileSize;
            } else {
                tileRight = tileLeft + tileSize;
                tileBottom = tileTop + tileSize;
            }
            tileView.layout(tileLeft, tileTop, tileRight, tileBottom);
            tileLeft = tileRight;
            i++;
        }
    }

    private void setPhotoInfo(PhotoTileView tileView, PhotoInfo photoInfo) {
        if (tileView.getPhotoInfo() != photoInfo) {
            tileView.setPhotoInfo(photoInfo);
            if (photoInfo != null) {
                fulfillTileViewFields(tileView, photoInfo, this.minTileWidth);
            }
        } else if (tileView.getHolder() == null) {
            fulfillTileViewFields(tileView, photoInfo, this.minTileWidth);
        }
    }

    private void fulfillTileViewFields(PhotoTileView tileView, PhotoInfo photoInfo, int size) {
        if (this.bigPhotoInfo == photoInfo) {
            size *= 2;
        }
        tileView.setDisplayDoubleSize(this.bigPhotoInfo == photoInfo);
        tileView.setImageUri(photoInfo.getClosestSizeUri(size, size));
        ThreadUtil.queueOnMain(new C07261(tileView));
    }

    public final void update(int columnCount, PhotoInfo bigPhotoInfo, List<PhotoInfo> deletedItems, boolean darkenBlocked, PhotoInfo... photoInfos) {
        this.bigPhotoInfo = bigPhotoInfo;
        this.photoInfos = photoInfos;
        this.columnCount = columnCount;
        updateViews(deletedItems, darkenBlocked);
        updateImages();
    }

    private void updateViews(List<PhotoInfo> deletedItems, boolean darkenBlocked) {
        int i;
        int reqViewsCount = this.photoInfos.length;
        if (getChildCount() != reqViewsCount) {
            int diff = getChildCount() - reqViewsCount;
            if (diff > 0) {
                for (i = 0; i < diff; i++) {
                    removeViewAt(0);
                }
            } else {
                int count = Math.abs(diff);
                for (i = 0; i < count; i++) {
                    PhotoTileView tileView = new PhotoTileView(getContext());
                    tileView.setOnPhotoTileClickListener(this.onPhotoTileClickListener);
                    addView(tileView);
                }
            }
        }
        for (i = 0; i < getChildCount(); i++) {
            boolean z;
            PhotoInfo photoInfo = this.photoInfos[i];
            tileView = (PhotoTileView) getChildAt(i);
            setPhotoInfo(tileView, photoInfo);
            if (deletedItems.contains(photoInfo) || (darkenBlocked && photoInfo.isBlocked())) {
                z = true;
            } else {
                z = false;
            }
            tileView.setDarken(z);
        }
        requestLayout();
    }

    private void updateImages() {
        for (int i = 0; i < getChildCount(); i++) {
            PhotoTileView view = (PhotoTileView) getChildAt(i);
            PhotoInfo photoInfo = view.photoInfo;
            if (photoInfo != null) {
                int size = this.minTileWidth;
                if (photoInfo == this.bigPhotoInfo) {
                    size *= 2;
                }
                String url = photoInfo.getClosestSizeUrl(size, size);
                if (url != null) {
                    view.setImageUri(Uri.parse(url));
                } else {
                    return;
                }
            }
        }
    }

    public final void setOnPhotoTileClickListener(OnPhotoTileClickListener onPhotoTileClickListener) {
        this.onPhotoTileClickListener = onPhotoTileClickListener;
    }
}
