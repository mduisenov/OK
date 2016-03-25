package ru.ok.android.ui.image.view;

import android.content.Context;
import android.opengl.GLES10;
import android.util.DisplayMetrics;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.utils.DeviceUtils;

public class PhotoLayerHelper {
    private static int[] glSize;

    private static int getMaxGlTextureSize() {
        if (glSize == null) {
            glSize = new int[1];
            GLES10.glGetIntegerv(3379, glSize, 0);
        }
        return glSize[0];
    }

    public static void getSizesForPhotos(Context context, int[] holder) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        if (DeviceUtils.getMemoryClass(OdnoklassnikiApplication.getContext()) > 24) {
            width = (int) (((float) width) * 1.5f);
            height = (int) (((float) height) * 1.5f);
        }
        int widthToUse = Math.max(width, height);
        int heightToUse = Math.min(width, height);
        int maxGlTextureSize = getMaxGlTextureSize();
        if (maxGlTextureSize > 0) {
            widthToUse = Math.min(widthToUse, maxGlTextureSize);
            heightToUse = Math.min(heightToUse, maxGlTextureSize);
        }
        holder[0] = widthToUse;
        holder[1] = heightToUse;
    }
}
