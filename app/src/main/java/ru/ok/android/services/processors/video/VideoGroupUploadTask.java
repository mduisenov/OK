package ru.ok.android.services.processors.video;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import ru.ok.android.services.persistent.PersistentTask;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.video.GetVideoUploadUrlRequest;

public class VideoGroupUploadTask extends VideoUploadTask {
    public static final Creator<VideoGroupUploadTask> CREATOR;
    private static final long serialVersionUID = 1;
    private final String groupId;

    /* renamed from: ru.ok.android.services.processors.video.VideoGroupUploadTask.1 */
    static class C05111 implements Creator<VideoGroupUploadTask> {
        C05111() {
        }

        public VideoGroupUploadTask createFromParcel(Parcel source) {
            return new VideoGroupUploadTask(source);
        }

        public VideoGroupUploadTask[] newArray(int size) {
            return new VideoGroupUploadTask[size];
        }
    }

    public VideoGroupUploadTask(String uid, MediaInfo mediaInfo, String groupId) {
        super(uid, mediaInfo);
        this.groupId = groupId;
    }

    public String getGroupId() {
        return this.groupId;
    }

    protected BaseRequest getUploadUrlRequest(long fileSize, String fileName) {
        return new GetVideoUploadUrlRequest(this.groupId, fileName, fileSize, null);
    }

    protected VideoGroupUploadTask(Parcel src) {
        super(src);
        this.groupId = src.readString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(getGroupId());
    }

    public PersistentTask copy() {
        Parcel parcel = toParcel();
        PersistentTask copy = new VideoGroupUploadTask(parcel);
        parcel.recycle();
        return copy;
    }

    public boolean isVideoMail() {
        return false;
    }

    static {
        CREATOR = new C05111();
    }
}
