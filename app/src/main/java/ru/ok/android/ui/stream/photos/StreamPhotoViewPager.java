package ru.ok.android.ui.stream.photos;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import ru.ok.android.C0206R;
import ru.ok.android.ui.custom.AspectRatioViewPager;

public class StreamPhotoViewPager extends AspectRatioViewPager {
    private final int leftOffset;
    private final float pageWidthFactor;
    private final int rightOffset;
    private final boolean verticalPhotosSquared;

    public StreamPhotoViewPager(Context context) {
        this(context, null);
    }

    public StreamPhotoViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public StreamPhotoViewPager(Context context, AttributeSet attrs, int defAttrId, int defStyleId) {
        super(context, attrs, defAttrId, defStyleId);
        TypedArray a = context.obtainStyledAttributes(attrs, C0206R.styleable.StreamPhotoViewPager, defAttrId, defStyleId);
        int pageMargin = a.getDimensionPixelOffset(0, 0);
        this.pageWidthFactor = a.getFloat(1, 1.0f);
        this.verticalPhotosSquared = a.getBoolean(2, false);
        this.leftOffset = a.getDimensionPixelOffset(3, 0);
        this.rightOffset = a.getDimensionPixelOffset(4, 0);
        a.recycle();
        setPageMargin(pageMargin);
        setAspectRatio(getInitialAspectRatio() / this.pageWidthFactor);
    }

    public void setAdapter(PagerAdapter adapter) {
        if (adapter instanceof PhotosFeedAdapter) {
            PhotosFeedAdapter photoAdapter = (PhotosFeedAdapter) adapter;
            photoAdapter.setPageWidthFactor(this.pageWidthFactor);
            photoAdapter.setIsVerticalSquared(this.verticalPhotosSquared);
            photoAdapter.setLeftOffset(this.leftOffset);
        } else if (adapter != null) {
        }
        super.setAdapter(adapter);
    }
}
