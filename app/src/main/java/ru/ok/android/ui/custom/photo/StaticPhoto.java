package ru.ok.android.ui.custom.photo;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import ru.ok.android.fresco.FrescoOdkl.ProgressCallback;

public interface StaticPhoto extends ProgressCallback {
    int getPlaceholderHeight();

    int getPlaceholderWidth();

    boolean hasPlaceholder();

    void setImageDrawable(Drawable drawable);

    void setProgressVisible(boolean z);

    void setReadyForAnimation(boolean z);

    void setStubViewImage(@DrawableRes int i);

    void setStubViewSubtitle(@StringRes int i);

    void setStubViewTitle(@StringRes int i);

    void setStubViewVisible(boolean z);
}
