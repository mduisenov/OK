package ru.ok.android.services.processors.messaging.attach;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import ru.ok.android.model.cache.ram.MessageModel;
import ru.ok.android.model.cache.ram.MessagesCache;
import ru.ok.android.proto.MessagesProto.Attach;
import ru.ok.android.proto.MessagesProto.Attach.Status;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.messages.MessagesService;
import ru.ok.android.services.persistent.PersistentTask;
import ru.ok.android.services.persistent.PersistentTaskContext;
import ru.ok.android.utils.Logger;

public final class PhotoSendAttachmentsTask extends SendAttachmentsTask {
    public static final Creator<PhotoSendAttachmentsTask> CREATOR;
    private static final long serialVersionUID = 1;
    private final long[] attachmentIds;

    /* renamed from: ru.ok.android.services.processors.messaging.attach.PhotoSendAttachmentsTask.1 */
    static class C04791 implements Creator<PhotoSendAttachmentsTask> {
        C04791() {
        }

        public PhotoSendAttachmentsTask createFromParcel(Parcel source) {
            return new PhotoSendAttachmentsTask(source);
        }

        public PhotoSendAttachmentsTask[] newArray(int size) {
            return new PhotoSendAttachmentsTask[size];
        }
    }

    public PhotoSendAttachmentsTask(String uid, int messageId, String conversationId, long[] attachmentIds) {
        super(uid, messageId, conversationId);
        this.attachmentIds = attachmentIds;
    }

    public PhotoSendAttachmentsTask(Parcel src) {
        super(src);
        this.attachmentIds = new long[src.readInt()];
        src.readLongArray(this.attachmentIds);
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.attachmentIds.length);
        dest.writeLongArray(this.attachmentIds);
    }

    protected PersistentTask createUploadAttachmentsTask() {
        return new UploadAttachmentsPhotosTask(getUid(), getId(), this.attachmentIds, this.messageId, this.conversationId, true);
    }

    protected void onSubTaskCompleted(PersistentTaskContext persistentContext, PersistentTask subTask) {
        super.onSubTaskCompleted(persistentContext, subTask);
        try {
            UploadAttachmentsPhotosTask photoSubTask = (UploadAttachmentsPhotosTask) subTask;
            int messageId = photoSubTask.getMessageId();
            long[] ids = photoSubTask.getAttachmentIds();
            MessageModel messageModel = MessagesCache.getInstance().getMessage(messageId);
            boolean hasErrors = false;
            boolean hasFailures = false;
            if (messageModel != null) {
                Message message = messageModel.message;
                for (long attachment : ids) {
                    for (Attach attach : message.getAttachesList()) {
                        if (attach.getUuid() == attachment) {
                            if (attach.getStatus() == Status.ERROR) {
                                hasErrors = true;
                                if (hasFailures) {
                                    break;
                                }
                            }
                            if (attach.getStatus() == Status.RECOVERABLE_ERROR) {
                                hasFailures = true;
                                if (hasErrors) {
                                    break;
                                }
                            } else {
                                continue;
                            }
                        }
                    }
                }
            }
            if (hasErrors) {
                updateMessageStatus(Message.Status.SERVER_ERROR);
            } else if (hasFailures) {
                updateMessageStatus(Message.Status.SERVER_ERROR);
            } else {
                updateMessageStatus(Message.Status.WAITING);
                Logger.m172d("Initiate sending undelivered messages due to photo attachment upload completion");
                MessagesService.sendActionSendAll(persistentContext.getContext());
            }
        } catch (Throwable exc) {
            Logger.m179e(exc, "Unable to update message state");
        }
    }

    public PersistentTask copy() {
        Parcel parcel = toParcel();
        PersistentTask copy = new PhotoSendAttachmentsTask(parcel);
        parcel.recycle();
        return copy;
    }

    static {
        CREATOR = new C04791();
    }
}
