package ru.ok.android.ui.video.player.cast;

import android.content.Context;
import android.support.v7.app.MediaRouteButton;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.mediarouter.C0029R;
import android.util.AttributeSet;
import com.google.android.libraries.cast.companionlibrary.C0158R;
import ru.mail.libverify.C0176R;

public class MediaRouteButtonHoloDark extends MediaRouteButton {
    public MediaRouteButtonHoloDark(Context context) {
        this(context, null);
    }

    public MediaRouteButtonHoloDark(Context context, AttributeSet attrs) {
        this(context, attrs, C0029R.attr.mediaRouteButtonStyle);
    }

    public MediaRouteButtonHoloDark(Context context, AttributeSet attrs, int defStyleAttr) {
        super(getThemedContext(context), attrs, defStyleAttr);
    }

    private static Context getThemedContext(Context context) {
        return new ContextThemeWrapper(new ContextThemeWrapper(context, C0176R.style.Theme_AppCompat), C0158R.style.Theme_MediaRouter);
    }
}
