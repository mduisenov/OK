package ru.ok.android.services.processors.photo.upload;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import ru.ok.android.services.persistent.TaskServerErrorException;

public class ImageUploadException extends TaskServerErrorException implements Parcelable {
    public static final Creator<ImageUploadException> CREATOR;
    private static final long serialVersionUID = -3036533548438679955L;
    private final int phase;

    /* renamed from: ru.ok.android.services.processors.photo.upload.ImageUploadException.1 */
    static class C04841 implements Creator<ImageUploadException> {
        C04841() {
        }

        public ImageUploadException createFromParcel(Parcel source) {
            return new ImageUploadException(source);
        }

        public ImageUploadException[] newArray(int size) {
            return new ImageUploadException[size];
        }
    }

    public ImageUploadException(int phase, int errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
        this.phase = phase;
    }

    public ImageUploadException(int phase, int errorCode, Throwable cause) {
        this(phase, errorCode, null, cause);
    }

    public ImageUploadException(int phase, int errorCode) {
        this(phase, errorCode, null, null);
    }

    public ImageUploadException(int phase, int errorCode, String message) {
        this(phase, errorCode, message, null);
    }

    public int getPhase() {
        return this.phase;
    }

    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        if (this.phase == ((ImageUploadException) o).phase) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return super.hashCode() + (1410884393 * this.phase);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.phase);
    }

    protected ImageUploadException(Parcel src) {
        super(src);
        this.phase = src.readInt();
    }

    static {
        CREATOR = new C04841();
    }
}
