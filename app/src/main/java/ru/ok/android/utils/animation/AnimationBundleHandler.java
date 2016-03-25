package ru.ok.android.utils.animation;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import java.lang.ref.WeakReference;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.ui.image.view.PhotoLayerAnimationHelper;

public final class AnimationBundleHandler {
    private WeakReference<View> lastInvisibleImageView;
    @NonNull
    private final PhotoIdExtractor photoIdExtractor;
    @NonNull
    private final ViewGroup rootView;

    public interface PhotoIdExtractor {
        String getViewPhotoId(View view);
    }

    public AnimationBundleHandler(@NonNull ViewGroup rootView, @NonNull PhotoIdExtractor photoIdExtractor) {
        this.rootView = rootView;
        this.photoIdExtractor = photoIdExtractor;
    }

    private View findImageViewWithId(@NonNull ViewGroup viewGroup, String photoId) {
        if (TextUtils.isEmpty(photoId)) {
            return null;
        }
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
                View subResult = findImageViewWithId((ViewGroup) child, photoId);
                if (subResult != null) {
                    return subResult;
                }
            } else if (TextUtils.equals(photoId, this.photoIdExtractor.getViewPhotoId(child))) {
                return child;
            }
        }
        return null;
    }

    public Bundle onMessage(Message message, String photoId) {
        View view;
        switch (message.what) {
            case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                if (this.lastInvisibleImageView != null) {
                    View prevImageView = (View) this.lastInvisibleImageView.get();
                    if (prevImageView != null) {
                        prevImageView.setVisibility(0);
                    }
                    this.lastInvisibleImageView = null;
                }
                view = findImageViewWithId(this.rootView, photoId);
                if (view == null) {
                    return null;
                }
                view.setVisibility(4);
                this.lastInvisibleImageView = new WeakReference(view);
                return null;
            case MessagesProto.Message.AUTHORID_FIELD_NUMBER /*2*/:
                view = findImageViewWithId(this.rootView, photoId);
                if (view != null) {
                    return PhotoLayerAnimationHelper.makeScaleDownAnimationBundle(view);
                }
                return null;
            case MessagesProto.Message.TYPE_FIELD_NUMBER /*3*/:
                view = findImageViewWithId(this.rootView, photoId);
                if (view == null) {
                    return null;
                }
                view.setVisibility(0);
                return null;
            default:
                return null;
        }
    }
}
