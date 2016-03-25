package ru.ok.android.fresco;

import android.graphics.drawable.Animatable;
import android.view.View;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import java.lang.ref.WeakReference;

public class InvalidateControllerListener extends BaseControllerListener<ImageInfo> {
    private final WeakReference<View> weakReference;

    public InvalidateControllerListener(View view) {
        this.weakReference = new WeakReference(view);
    }

    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
        View view = (View) this.weakReference.get();
        if (view != null) {
            view.invalidate();
        }
    }
}
