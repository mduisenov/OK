package ru.ok.android.services.processors.video;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import ru.ok.android.services.persistent.PersistentTask;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.video.GetVideoUploadUrlRequest;

public class VideoUserUploadTask extends VideoUploadTask {
    public static final Creator<VideoUserUploadTask> CREATOR;
    private static final long serialVersionUID = 1;

    /* renamed from: ru.ok.android.services.processors.video.VideoUserUploadTask.1 */
    static class C05151 implements Creator<VideoUserUploadTask> {
        C05151() {
        }

        public VideoUserUploadTask createFromParcel(Parcel source) {
            return new VideoUserUploadTask(source);
        }

        public VideoUserUploadTask[] newArray(int size) {
            return new VideoUserUploadTask[size];
        }
    }

    public VideoUserUploadTask(String uid, MediaInfo mediaInfo, int parentTaskId) {
        super(uid, mediaInfo, parentTaskId);
    }

    protected VideoUserUploadTask(Parcel src) {
        super(src);
    }

    protected BaseRequest getUploadUrlRequest(long fileSize, String fileName) {
        return new GetVideoUploadUrlRequest(null, fileName, fileSize, null);
    }

    public boolean isVideoMail() {
        return false;
    }

    public PersistentTask copy() {
        Parcel parcel = toParcel();
        PersistentTask copy = new VideoUserUploadTask(parcel);
        parcel.recycle();
        return copy;
    }

    static {
        CREATOR = new C05151();
    }
}
