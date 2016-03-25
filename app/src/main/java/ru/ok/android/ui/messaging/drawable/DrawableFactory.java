package ru.ok.android.ui.messaging.drawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import pl.droidsonroids.gif.GifDrawable;

public class DrawableFactory {
    private final Context context;

    public DrawableFactory(@NonNull Context context) {
        this.context = context;
    }

    @NonNull
    public Drawable createDrawableFromStream(@NonNull InputStream in) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(in);
        if (readStreamHeader(bis).equals("GIF")) {
            return createGifDrawable(bis);
        }
        return createBitmapDrawable(bis);
    }

    @NonNull
    private Drawable createGifDrawable(@NonNull InputStream in) throws IOException {
        return new GifDrawable(in);
    }

    @NonNull
    private Drawable createBitmapDrawable(@NonNull InputStream in) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeStream(in);
        if (bitmap != null) {
            return new BitmapDrawable(this.context.getResources(), bitmap);
        }
        throw new IOException("can't create bitmap from input stream");
    }

    @NonNull
    private static String readStreamHeader(@NonNull InputStream in) throws IOException {
        byte[] headerBytes = new byte[3];
        in.mark(headerBytes.length);
        in.read(headerBytes);
        in.reset();
        return new String(headerBytes);
    }
}
