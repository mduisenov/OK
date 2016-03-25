package ru.ok.android.services.processors.image.upload;

import ru.ok.android.utils.settings.ServicesSettingsHelper;
import ru.ok.java.api.response.ServicesSettings;

public class ResizeSettings {
    private final int desiredHeight;
    private final int desiredWidth;
    private final int rotation;
    private final int serverCompressQuality;

    public ResizeSettings() {
        this(0);
    }

    public ResizeSettings(int rotation) {
        this.rotation = rotation;
        ServicesSettings settings = ServicesSettingsHelper.getServicesSettings();
        this.serverCompressQuality = safeGetQuality(settings.getUploadPhotoMaxQuality());
        this.desiredWidth = settings.getUploadPhotoMaxWidth();
        this.desiredHeight = settings.getUploadPhotoMaxHeight();
    }

    private int safeGetQuality(int serverCompressQuality) {
        if (serverCompressQuality < 0) {
            return 0;
        }
        if (serverCompressQuality > 100) {
            return 100;
        }
        return serverCompressQuality;
    }

    public int getDesiredWidth() {
        return isSwapDimensions() ? this.desiredHeight : this.desiredWidth;
    }

    public int getDesiredHeight() {
        return isSwapDimensions() ? this.desiredWidth : this.desiredHeight;
    }

    private boolean isSwapDimensions() {
        return this.rotation == 90 || this.rotation == 270;
    }

    public int getScaleNumerator(int sourceWidth, int sourceHeight) {
        int scaleNumerator = (int) (0.6666667f + (Math.max(((float) this.desiredWidth) / ((float) sourceWidth), ((float) this.desiredHeight) / ((float) sourceHeight)) * 8.0f));
        if (scaleNumerator > 16) {
            scaleNumerator = 16;
        }
        if (scaleNumerator < 1) {
            return 1;
        }
        return scaleNumerator;
    }

    public int getRotation() {
        return this.rotation;
    }

    public int getServerCompressQuality() {
        return this.serverCompressQuality;
    }
}
