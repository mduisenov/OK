package ru.ok.android.services.processors.audio;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import ru.ok.android.services.persistent.PersistentTaskState;
import ru.ok.android.services.processors.video.MediaInfo;
import ru.ok.android.services.processors.video.VideoAttachmentUploadTask;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.video.GetVideoUploadUrlRequest;
import ru.ok.model.messages.Attachment.AttachmentType;

public final class AudioAttachmentUploadTask extends VideoAttachmentUploadTask {
    public static final Creator<AudioAttachmentUploadTask> CREATOR;
    private static final long serialVersionUID = 1;

    /* renamed from: ru.ok.android.services.processors.audio.AudioAttachmentUploadTask.1 */
    static class C04491 implements Creator<AudioAttachmentUploadTask> {
        C04491() {
        }

        public AudioAttachmentUploadTask createFromParcel(Parcel source) {
            return new AudioAttachmentUploadTask(source);
        }

        public AudioAttachmentUploadTask[] newArray(int size) {
            return new AudioAttachmentUploadTask[size];
        }
    }

    public AudioAttachmentUploadTask(String uid, MediaInfo mediaInfo, String userId, long attachDatabaseId, String conversationId, int messageId, int parentId) {
        super(uid, mediaInfo, userId, attachDatabaseId, conversationId, messageId, parentId);
    }

    protected AudioAttachmentUploadTask(Parcel src) {
        super(src);
    }

    protected BaseRequest getUploadUrlRequest(long fileSize, String fileName) {
        return new GetVideoUploadUrlRequest(null, fileName, fileSize, AttachmentType.AUDIO_RECORDING);
    }

    protected Bitmap getThumbnail(Context context) {
        return createDefaultNotificationDrawable(context.getResources());
    }

    protected int getDefaultNotificationLargeIconId() {
        return 2130838250;
    }

    protected String getNotificationTitle(LocalizationManager localizationManager) {
        return localizationManager.getString(2131165417);
    }

    protected String getDefaultErrorMessage(LocalizationManager localizationManager) {
        return localizationManager.getString(2131165415);
    }

    protected String getStatusText(LocalizationManager localizationManager) {
        if (getState() == PersistentTaskState.SUBMITTED || getState() == PersistentTaskState.EXECUTING) {
            return localizationManager.getString(2131165416);
        }
        return super.getStatusText(localizationManager);
    }

    public boolean isVideoMail() {
        return true;
    }

    static {
        CREATOR = new C04491();
    }
}
