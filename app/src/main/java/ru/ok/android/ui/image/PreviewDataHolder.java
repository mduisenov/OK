package ru.ok.android.ui.image;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.util.UriUtil;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import java.io.Closeable;
import ru.ok.android.fresco.FrescoOdkl;
import ru.ok.android.ui.image.view.PhotoLayerAnimationHelper;
import ru.ok.android.utils.Logger;

public class PreviewDataHolder implements Closeable {
    private final Uri previewImageUri;
    private final CloseableReference<CloseableImage> ref;

    PreviewDataHolder(@NonNull Uri previewImageUri, @NonNull CloseableReference<CloseableImage> closeableReference) {
        this.previewImageUri = previewImageUri;
        this.ref = closeableReference;
    }

    @Nullable
    public CloseableReference<CloseableImage> getPreviewRef() {
        return this.ref.isValid() ? this.ref : null;
    }

    public void close() {
        CloseableReference.closeSafely(this.ref);
    }

    @Nullable
    public static PreviewDataHolder createFrom(@Nullable Bundle animationBundle) {
        if (animationBundle == null) {
            return null;
        }
        Uri uri = (Uri) animationBundle.getParcelable("pla_image_uri");
        Logger.m173d("Uri: %s", uri);
        if (uri == null) {
            return null;
        }
        CloseableReference<CloseableImage> bitmapReference = FrescoOdkl.getCachedBitmapReference(uri, PhotoLayerAnimationHelper.bundleToResizeOptions(animationBundle));
        if (bitmapReference == null) {
            Logger.m172d("Ref is empty");
            return null;
        } else if (bitmapReference != null && (bitmapReference.get() instanceof CloseableBitmap)) {
            return new PreviewDataHolder(uri, bitmapReference);
        } else {
            Logger.m176e("Preview reference is not bitmap");
            return null;
        }
    }

    @Nullable
    public CloseableReference<CloseableImage> getRefIfMatch(@Nullable Uri previewUri) {
        Logger.m173d("previewUri is: %s", previewUri);
        if (previewUri == null) {
            return null;
        }
        if (UriUtil.isLocalFileUri(previewUri)) {
            Logger.m172d("previewUri is local");
            return null;
        } else if (previewUri.equals(this.previewImageUri) && this.ref.isValid()) {
            Logger.m172d("Found preview CloseableReference.");
            return this.ref;
        } else {
            Logger.m172d("Preview ref not found");
            return null;
        }
    }
}
