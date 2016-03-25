package ru.ok.android.services.processors.messaging.attach;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.model.cache.ram.MessagesCache;
import ru.ok.android.proto.MessagesProto.Attach;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.proto.MessagesProto.Message.Status;
import ru.ok.android.services.messages.MessagesService;
import ru.ok.android.services.persistent.PersistentTask;
import ru.ok.android.services.persistent.PersistentTaskContext;
import ru.ok.android.services.persistent.PersistentTaskState;
import ru.ok.android.services.processors.video.MediaInfo;
import ru.ok.android.services.processors.video.VideoAttachmentUploadTask;
import ru.ok.android.utils.Logger;

public class VideoSendAttachmentsTask extends SendAttachmentsTask {
    public static final Creator<VideoSendAttachmentsTask> CREATOR;
    private static final long serialVersionUID = 1;
    protected final long attachmentDatabaseId;
    protected MediaInfo mediaInfo;

    /* renamed from: ru.ok.android.services.processors.messaging.attach.VideoSendAttachmentsTask.1 */
    static class C04811 implements Creator<VideoSendAttachmentsTask> {
        C04811() {
        }

        public VideoSendAttachmentsTask createFromParcel(Parcel source) {
            return new VideoSendAttachmentsTask(source);
        }

        public VideoSendAttachmentsTask[] newArray(int size) {
            return new VideoSendAttachmentsTask[size];
        }
    }

    /* renamed from: ru.ok.android.services.processors.messaging.attach.VideoSendAttachmentsTask.2 */
    static /* synthetic */ class C04822 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState;

        static {
            $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState = new int[PersistentTaskState.values().length];
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.FAILED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.ERROR.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.COMPLETED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public VideoSendAttachmentsTask(String uid, int messageId, String conversationId, long attachmentDatabaseId) {
        super(uid, messageId, conversationId);
        this.attachmentDatabaseId = attachmentDatabaseId;
    }

    public VideoSendAttachmentsTask(Parcel src) {
        super(src);
        this.attachmentDatabaseId = src.readLong();
        this.mediaInfo = (MediaInfo) src.readParcelable(MediaInfo.class.getClassLoader());
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(this.attachmentDatabaseId);
        dest.writeParcelable(this.mediaInfo, flags);
    }

    protected PersistentTask createUploadAttachmentsTask() {
        this.mediaInfo = createMediaInfo();
        return new VideoAttachmentUploadTask(OdnoklassnikiApplication.getCurrentUser().uid, this.mediaInfo, getUid(), this.attachmentDatabaseId, this.conversationId, this.messageId, getId());
    }

    protected MediaInfo createMediaInfo() {
        Attach attach = MessagesCache.getInstance().getAttachByUUID(this.messageId, this.attachmentDatabaseId);
        Uri uri = null;
        if (attach != null) {
            String path = attach.getVideo().getPath();
            if (!TextUtils.isEmpty(path)) {
                uri = Uri.parse(path);
            }
        }
        return MediaInfo.fromUri(OdnoklassnikiApplication.getContext(), uri, "video-" + System.currentTimeMillis());
    }

    protected void onSubTaskStateChanged(PersistentTaskContext persistentContext, PersistentTask subTask) {
        super.onSubTaskStateChanged(persistentContext, subTask);
        try {
            switch (C04822.$SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[subTask.getState().ordinal()]) {
                case Message.TEXT_FIELD_NUMBER /*1*/:
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    updateMessageStatus(Status.FAILED);
                    return;
                case Message.TYPE_FIELD_NUMBER /*3*/:
                    updateMessageStatus(Status.WAITING);
                    Logger.m172d("Initiate sending undelivered messages due to video attachment upload completion");
                    MessagesService.sendActionSendAll(persistentContext.getContext());
                    return;
                default:
                    return;
            }
        } catch (Throwable exc) {
            Logger.m179e(exc, "Unable to update message state");
        }
        Logger.m179e(exc, "Unable to update message state");
    }

    public PersistentTask copy() {
        Parcel parcel = toParcel();
        try {
            PersistentTask copy = new VideoSendAttachmentsTask(parcel);
            return copy;
        } finally {
            parcel.recycle();
        }
    }

    static {
        CREATOR = new C04811();
    }

    public String toString() {
        return getClass().getSimpleName() + "[" + " taskId=" + getId() + " parentTaskId=" + getParentId() + " state=" + getState() + " attachmentDatabaseId=" + this.attachmentDatabaseId + " mediaInfo=" + this.mediaInfo + "]";
    }
}
