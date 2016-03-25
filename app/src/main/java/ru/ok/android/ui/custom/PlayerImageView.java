package ru.ok.android.ui.custom;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import ru.ok.android.ui.custom.imageview.UrlImageView;

public class PlayerImageView extends UrlImageView {
    public PlayerImageView(Context context) {
        super(context);
        init();
    }

    public PlayerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlayerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        ((GenericDraweeHierarchy) getHierarchy()).setFadeDuration(0);
    }

    public static boolean isStubImageUrl(String url) {
        if (url == null) {
            return true;
        }
        return Uri.parse(url).getLastPathSegment().startsWith("stub_album");
    }
}
