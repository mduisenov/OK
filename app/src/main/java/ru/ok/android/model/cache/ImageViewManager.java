package ru.ok.android.model.cache;

import android.net.Uri;
import android.text.TextUtils;
import ru.ok.android.model.cache.ImageLoader.HandleBlocker;
import ru.ok.android.ui.custom.imageview.AvatarImageView;
import ru.ok.android.ui.custom.imageview.RoundAvatarImageView;
import ru.ok.android.ui.custom.imageview.UrlImageView;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.URLUtil;

public final class ImageViewManager {
    private static ImageViewManager _instance;

    static {
        _instance = new ImageViewManager();
    }

    public static ImageViewManager getInstance() {
        return _instance;
    }

    private void displayImageGeneral(String url, UrlImageView imageView, int defaultImageResourceId, HandleBlocker blocker, int size) {
        displayImageGeneral(url, null, imageView, defaultImageResourceId, blocker, size, true);
    }

    private void displayImageGeneral(String url, AvatarImageView imageView, int defaultImageResourceId, HandleBlocker blocker, int size) {
        displayImageGeneral(url, imageView, imageView.getImage(), defaultImageResourceId, blocker, size, true);
    }

    private void displayImageGeneral(String url, AvatarImageView avatarImageView, UrlImageView imageView, int defaultImageResourceId, HandleBlocker blocker, int size, boolean clearPrevious) {
        Logger.m173d(">>> url=%s", url);
        if (url != null && url.contains("/res/stub_")) {
            url = null;
        }
        if (TextUtils.equals(url, imageView.getImageUrl())) {
            if (TextUtils.isEmpty(url)) {
                if (defaultImageResourceId > 0) {
                    imageView.setImageResource(defaultImageResourceId);
                } else {
                    imageView.setUrl(null);
                }
            }
            Logger.m172d("<<< set default image resource id");
            return;
        }
        if (clearPrevious) {
            imageView.setUrl(null);
        }
        if (defaultImageResourceId > 0) {
            imageView.setImageResource(defaultImageResourceId);
        }
        imageView.setUrl(url);
        if (!TextUtils.isEmpty(url) && !URLUtil.isStubUrl(url) && avatarImageView != null) {
            avatarImageView.setImageUrl(Uri.parse(url));
        }
    }

    public void displayImage(String url, AvatarImageView imageView, boolean isMan, HandleBlocker blocker) {
        displayImage(url, imageView, isMan, blocker, -1);
    }

    public void displayImage(String url, AvatarImageView imageView, boolean isMan, HandleBlocker blocker, int size) {
        if (TextUtils.isEmpty(url) || url.contains("/res/stub_")) {
            if (isMan) {
                imageView.setAvatarMaleImage();
            } else {
                imageView.setAvatarFemaleImage();
            }
            imageView.getImage().setUrl(null);
            return;
        }
        displayImageGeneral(url, imageView, isMan ? 2130838321 : 2130837927, blocker, size == -1 ? imageView.getWidth() : size);
    }

    public void displayImage(String url, RoundAvatarImageView imageView, boolean isMan, HandleBlocker blocker) {
        if (url == null || url.contains("/res/stub_")) {
            if (isMan) {
                imageView.setAvatarMaleImage();
            } else {
                imageView.setAvatarFemaleImage();
            }
            imageView.setUrl(null);
            return;
        }
        displayImageGeneral(url, (UrlImageView) imageView, isMan ? 2130838321 : 2130837927, blocker, -1);
    }

    public void displayImage(String url, UrlImageView imageView, HandleBlocker blocker) {
        displayImageGeneral(url, imageView, 0, blocker, imageView.getWidth());
    }

    public void displayImage(String url, UrlImageView imageView, int defaultImageResourceId, HandleBlocker blocker) {
        displayImageGeneral(url, imageView, defaultImageResourceId, blocker, imageView.getWidth());
    }

    public void displayImage(String url, UrlImageView imageView, int defaultImageResourceId, HandleBlocker blocker, int size) {
        displayImageGeneral(url, imageView, defaultImageResourceId, blocker, size);
    }
}
