package ru.ok.android.services.processors.messaging.attach;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.io.File;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.model.cache.ram.MessagesCache;
import ru.ok.android.proto.MessagesProto.Attach;
import ru.ok.android.services.persistent.PersistentTask;
import ru.ok.android.services.processors.audio.AudioAttachmentUploadTask;
import ru.ok.android.services.processors.video.FileLocation;
import ru.ok.android.services.processors.video.MediaInfo;
import ru.ok.android.services.processors.video.MediaInfoTempFile;

public final class AudioSendAttachmentTask extends VideoSendAttachmentsTask {
    public static final Creator<AudioSendAttachmentTask> CREATOR;
    private static final long serialVersionUID = 1;

    /* renamed from: ru.ok.android.services.processors.messaging.attach.AudioSendAttachmentTask.1 */
    static class C04781 implements Creator<AudioSendAttachmentTask> {
        C04781() {
        }

        public AudioSendAttachmentTask createFromParcel(Parcel source) {
            return new AudioSendAttachmentTask(source);
        }

        public AudioSendAttachmentTask[] newArray(int size) {
            return new AudioSendAttachmentTask[size];
        }
    }

    public AudioSendAttachmentTask(String uid, int messageId, String conversationId, long attachmentDatabaseId) {
        super(uid, messageId, conversationId, attachmentDatabaseId);
    }

    public AudioSendAttachmentTask(Parcel src) {
        super(src);
    }

    protected PersistentTask createUploadAttachmentsTask() {
        this.mediaInfo = createMediaInfo();
        return new AudioAttachmentUploadTask(OdnoklassnikiApplication.getCurrentUser().uid, this.mediaInfo, getUid(), this.attachmentDatabaseId, this.conversationId, this.messageId, getId());
    }

    protected MediaInfo createMediaInfo() {
        Attach attach = MessagesCache.getInstance().getAttachByUUID(this.messageId, this.attachmentDatabaseId);
        if (attach == null) {
            return null;
        }
        File audioFile = new File(attach.getAudio().getPath());
        return new MediaInfoTempFile(FileLocation.createFromExternalFile(audioFile), null, "recording.m4a", audioFile.length());
    }

    public PersistentTask copy() {
        Parcel parcel = toParcel();
        try {
            PersistentTask copy = new AudioSendAttachmentTask(parcel);
            return copy;
        } finally {
            parcel.recycle();
        }
    }

    static {
        CREATOR = new C04781();
    }
}
