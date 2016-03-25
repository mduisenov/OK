package ru.ok.android.ui.custom.online;

import android.graphics.drawable.Drawable;
import ru.ok.android.model.cache.ImageLoader.HandleBlocker;
import ru.ok.android.ui.custom.WrapperDrawable;
import ru.ok.android.ui.custom.animations.OnlineAnimationManager;
import ru.ok.android.ui.custom.animations.OnlineAnimationObserver;

public final class OnlineDrawable extends WrapperDrawable implements OnlineAnimationObserver {
    private HandleBlocker animationBlocker;

    public OnlineDrawable(Drawable baseDrawable) {
        super(baseDrawable);
        OnlineAnimationManager.getInstance().addObserver(this);
    }

    public void handleAlpha(int alpha) {
        setAlpha(alpha);
        if (this.animationBlocker == null || !this.animationBlocker.isBlocking()) {
            invalidateSelf();
        }
    }

    public void setAnimationBlocker(HandleBlocker blocker) {
        this.animationBlocker = blocker;
    }
}
