package ru.ok.android.services.processors.messaging.attach;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Parcel;
import ru.ok.android.model.cache.ram.MessagesCache;
import ru.ok.android.proto.MessagesProto.Message.Status;
import ru.ok.android.services.persistent.BaseParentPersistentTask;
import ru.ok.android.services.persistent.PersistentTask;
import ru.ok.android.services.persistent.PersistentTaskContext;
import ru.ok.android.services.persistent.PersistentTaskNotificationBuilder;
import ru.ok.android.services.persistent.PersistentTaskState;
import ru.ok.android.services.persistent.TaskException;

public abstract class SendAttachmentsTask extends BaseParentPersistentTask {
    private static final long serialVersionUID = 6262507099183371026L;
    protected final String conversationId;
    protected final int messageId;
    protected int uploadPhotosSubtaskId;

    protected abstract PersistentTask createUploadAttachmentsTask();

    public SendAttachmentsTask(String uid, int messageId, String conversationId) {
        super(uid, true);
        this.messageId = messageId;
        this.conversationId = conversationId;
    }

    public SendAttachmentsTask(Parcel src) {
        super(src);
        this.messageId = src.readInt();
        this.conversationId = src.readString();
        this.uploadPhotosSubtaskId = src.readInt();
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.messageId);
        dest.writeString(this.conversationId);
        dest.writeInt(this.uploadPhotosSubtaskId);
    }

    public void createNotification(PersistentTaskContext persistentContext, PersistentTask activeSubTask, PersistentTaskNotificationBuilder notificationBuilder) {
    }

    public PersistentTaskState execute(PersistentTaskContext persistentContext, Context context) throws TaskException {
        PersistentTask uploadImagesTask = null;
        if (this.uploadPhotosSubtaskId != 0) {
            uploadImagesTask = getSubTask(persistentContext, this.uploadPhotosSubtaskId);
        }
        if (uploadImagesTask == null) {
            updateMessageStatus(Status.UPLOADING_ATTACHMENTS);
            this.uploadPhotosSubtaskId = submitSubTask(persistentContext, createUploadAttachmentsTask());
            persist(persistentContext);
        } else if (uploadImagesTask.getState() == PersistentTaskState.COMPLETED) {
            return PersistentTaskState.COMPLETED;
        }
        return PersistentTaskState.EXECUTING;
    }

    protected void onSubTaskStateChanged(PersistentTaskContext persistentContext, PersistentTask subTask) {
        super.onSubTaskStateChanged(persistentContext, subTask);
        if (subTask.getState() == PersistentTaskState.ERROR) {
            updateMessageStatus(Status.FAILED);
        }
        if (subTask.getState() == PersistentTaskState.FAILED) {
            updateMessageStatus(Status.FAILED);
        }
    }

    protected void onCancel(PersistentTaskContext persistentContext) {
        if (this.uploadPhotosSubtaskId != 0) {
            PersistentTask subTask = persistentContext.getTask(this.uploadPhotosSubtaskId);
            if (subTask != null) {
                persistentContext.cancelSubTask(subTask);
            }
        }
    }

    protected void updateMessageStatus(Status status) {
        MessagesCache.getInstance().updateStatus(this.messageId, status);
    }

    protected PendingIntent getTaskDetailsIntent(PersistentTaskContext persistentContext) {
        return null;
    }
}
