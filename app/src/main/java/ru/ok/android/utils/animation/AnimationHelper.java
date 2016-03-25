package ru.ok.android.utils.animation;

import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.utils.animation.AnimationBundleHandler.PhotoIdExtractor;
import ru.ok.model.stream.entities.AbsFeedPhotoEntity;

public final class AnimationHelper {
    public static final boolean SHOULD_USE_SOFTWARE_LAYER_ON_ROTATION;
    public static final Interpolator hideInterpolator;
    public static final Interpolator showInterpolator;

    /* renamed from: ru.ok.android.utils.animation.AnimationHelper.1 */
    static class C14321 implements PhotoIdExtractor {
        C14321() {
        }

        public String getViewPhotoId(View view) {
            switch (view.getId()) {
                case C0263R.id.image /*2131624453*/:
                case 2131625362:
                case 2131625363:
                    AbsFeedPhotoEntity photoEntity = (AbsFeedPhotoEntity) view.getTag(2131624320);
                    return photoEntity != null ? photoEntity.getId() : null;
                default:
                    return null;
            }
        }
    }

    static {
        SHOULD_USE_SOFTWARE_LAYER_ON_ROTATION = "4.4.4".equals(VERSION.RELEASE);
        showInterpolator = new DecelerateInterpolator(1.5f);
        hideInterpolator = new AccelerateInterpolator(1.5f);
    }

    public static AnimationBundleHandler createStreamPhotoAnimationHandler(ViewGroup rootView) {
        return new AnimationBundleHandler(rootView, new C14321());
    }
}
