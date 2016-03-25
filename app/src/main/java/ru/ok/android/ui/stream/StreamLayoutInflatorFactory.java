package ru.ok.android.ui.stream;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater.Factory;
import android.view.View;
import com.noundla.centerviewpagersample.comps.StreamCenterLockViewPager;
import ru.ok.android.C0206R;
import ru.ok.android.ui.stream.photos.StreamPhotoViewPager;
import ru.ok.android.ui.stream.view.FeedFooterView;
import ru.ok.android.ui.stream.view.FeedHeaderView;

public class StreamLayoutInflatorFactory implements Factory {
    private final int feedFooterStyle;
    private final int feedHeaderStyle;
    private final int photoPagerStyle;

    public StreamLayoutInflatorFactory(Context context, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(null, C0206R.styleable.Stream, defStyleAttr, defStyleRes);
        this.feedHeaderStyle = a.getResourceId(0, 0);
        this.feedFooterStyle = a.getResourceId(1, 0);
        this.photoPagerStyle = a.getResourceId(2, 0);
        a.recycle();
    }

    public View onCreateView(String name, Context context, AttributeSet attrs) {
        if (FeedHeaderView.class.getName().equals(name)) {
            return new FeedHeaderView(context, attrs, 2130771974, this.feedHeaderStyle);
        }
        if (FeedFooterView.class.getName().equals(name)) {
            return new FeedFooterView(context, attrs, 2130771973, this.feedFooterStyle);
        }
        if (StreamPhotoViewPager.class.getName().equals(name)) {
            return new StreamPhotoViewPager(context, attrs, 0, this.photoPagerStyle);
        }
        if (StreamCenterLockViewPager.class.getName().equals(name)) {
            return new StreamCenterLockViewPager(context, attrs, 0, this.photoPagerStyle);
        }
        return null;
    }
}
