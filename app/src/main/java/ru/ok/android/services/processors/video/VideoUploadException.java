package ru.ok.android.services.processors.video;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import ru.ok.android.services.persistent.TaskServerErrorException;

public class VideoUploadException extends TaskServerErrorException {
    public static final Creator<VideoUploadException> CREATOR;
    private static final long serialVersionUID = 1;

    /* renamed from: ru.ok.android.services.processors.video.VideoUploadException.1 */
    static class C05121 implements Creator<VideoUploadException> {
        C05121() {
        }

        public VideoUploadException createFromParcel(Parcel parcel) {
            return new VideoUploadException(parcel);
        }

        public VideoUploadException[] newArray(int size) {
            return new VideoUploadException[size];
        }
    }

    public VideoUploadException(int errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public VideoUploadException(int errorCode) {
        this(errorCode, null, null);
    }

    public String toString() {
        return "VideoUploadException[errorCode=" + getErrorCode() + " message=" + getMessage() + " cause=" + getCause() + "]";
    }

    protected VideoUploadException(Parcel src) {
        super(src);
    }

    static {
        CREATOR = new C05121();
    }
}
