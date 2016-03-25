package ru.ok.android.utils;

import android.support.annotation.Nullable;

public final class MimeTypes {
    public static boolean isImage(@Nullable String mimeType) {
        return mimeType != null && mimeType.startsWith("image/");
    }

    public static boolean isVideo(@Nullable String mimeType) {
        return mimeType != null && mimeType.startsWith("video/");
    }

    public static boolean isGif(@Nullable String mimeType) {
        return "image/gif".equals(mimeType);
    }

    public static boolean isTextPlain(@Nullable String mimeType) {
        return mimeType != null && mimeType.startsWith("text/plain");
    }
}
