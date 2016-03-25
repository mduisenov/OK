package ru.ok.android.services.processors.messaging.attach;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.TextUtils;
import java.util.ArrayList;
import ru.ok.android.C0206R;
import ru.ok.android.fragments.image.PhotoAlbumsHelper;
import ru.ok.android.model.cache.ram.MessageModel;
import ru.ok.android.model.cache.ram.MessagesCache;
import ru.ok.android.model.image.ImageEditInfo;
import ru.ok.android.proto.MessagesProto.Attach;
import ru.ok.android.proto.MessagesProto.Attach.Photo;
import ru.ok.android.proto.MessagesProto.Attach.Status;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.AttachmentUtils;
import ru.ok.android.services.persistent.PersistentTask;
import ru.ok.android.services.persistent.PersistentTaskContext;
import ru.ok.android.services.persistent.PersistentTaskState;
import ru.ok.android.services.processors.image.upload.PrepareImageTask;
import ru.ok.android.services.processors.image.upload.UploadImagesTask;
import ru.ok.android.services.processors.image.upload.UploadOneImageTask;
import ru.ok.android.services.processors.photo.upload.ImageUploadException;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.bus.BusMessagingHelper;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.photo.PhotoAlbumInfo;

public final class UploadAttachmentsPhotosTask extends UploadImagesTask {
    public static final Creator<UploadAttachmentsPhotosTask> CREATOR;
    private static final long serialVersionUID = 1;
    private final long[] attachmentIds;
    private final String conversationId;
    private final Integer messageId;

    /* renamed from: ru.ok.android.services.processors.messaging.attach.UploadAttachmentsPhotosTask.1 */
    static class C04801 implements Creator<UploadAttachmentsPhotosTask> {
        C04801() {
        }

        public UploadAttachmentsPhotosTask createFromParcel(Parcel source) {
            return new UploadAttachmentsPhotosTask(source);
        }

        public UploadAttachmentsPhotosTask[] newArray(int size) {
            return new UploadAttachmentsPhotosTask[size];
        }
    }

    public UploadAttachmentsPhotosTask(String uid, int parentTaskId, long[] attachmentIds, int messageId, String conversationId, boolean ignoreErrors) {
        super(uid, true, parentTaskId, getAttachmentsAsImages(messageId, attachmentIds), false, null, ignoreErrors);
        this.attachmentIds = attachmentIds;
        this.messageId = Integer.valueOf(messageId);
        this.conversationId = conversationId;
    }

    public UploadAttachmentsPhotosTask(Parcel src) {
        super(src);
        this.attachmentIds = new long[src.readInt()];
        src.readLongArray(this.attachmentIds);
        this.messageId = Integer.valueOf(src.readInt());
        this.conversationId = src.readString();
    }

    protected void onSubTaskStateChanged(PersistentTaskContext persistentContext, PersistentTask subTask) {
        super.onSubTaskStateChanged(persistentContext, subTask);
        if (subTask.getParentId() != getId()) {
            throw new AssertionError("Parent ID of sub-task doesn't match my ID: subtask=" + subTask.getClass().getSimpleName() + "[id=" + subTask.getId() + " parentId=" + subTask.getParentId() + "], my id=" + getId() + ", my sub-tasks: " + getSubTaskIds());
        }
        ImageUploadException error = null;
        ImageEditInfo info = null;
        if (subTask instanceof PrepareImageTask) {
            info = ((PrepareImageTask) subTask).getImageEditInfo();
            error = (ImageUploadException) subTask.getError(ImageUploadException.class);
        } else if (subTask instanceof UploadOneImageTask) {
            info = ((UploadOneImageTask) subTask).getImageEditInfo();
            error = (ImageUploadException) subTask.getError(ImageUploadException.class);
        }
        Attach attach = MessagesCache.getInstance().findAttachmentByLocalId(this.messageId.intValue(), info.getId());
        if (attach == null) {
            Logger.m185w("Can't find attachments for localId: %s", info.getId());
        } else if ((subTask instanceof UploadOneImageTask) && subTask.getState() == PersistentTaskState.EXECUTING) {
            Logger.m184w("Image upload started: " + info.getId());
            AttachmentUtils.updateAttachmentState(this.messageId.intValue(), attach.getUuid(), Status.UPLOADING);
        } else if (subTask.getState() == PersistentTaskState.FAILED) {
            switch (error.getErrorCode()) {
                case Message.EDITINFO_FIELD_NUMBER /*11*/:
                    setState(persistentContext, PersistentTaskState.WAIT_INTERNET);
                    AttachmentUtils.updateAttachmentState(this.messageId.intValue(), attach.getUuid(), Status.WAITING);
                case C0206R.styleable.Toolbar_titleMarginEnd /*14*/:
                    setState(persistentContext, PersistentTaskState.FAILED);
                    AttachmentUtils.updateAttachmentState(this.messageId.intValue(), attach.getUuid(), Status.WAITING);
                default:
                    setState(persistentContext, PersistentTaskState.FAILED);
                    AttachmentUtils.updateAttachmentState(this.messageId.intValue(), attach.getUuid(), Status.ERROR);
                    sendErrorNotification(persistentContext);
                    updateMessageStatus(Message.Status.SERVER_ERROR);
            }
        }
    }

    private void updateMessageStatus(Message.Status status) {
        MessagesCache.getInstance().updateStatus(this.messageId.intValue(), status);
    }

    private void sendErrorNotification(PersistentTaskContext context) {
        Builder builder = new Builder(context.getContext());
        String tickerText = LocalizationManager.getString(context.getContext(), 2131165992);
        long when = System.currentTimeMillis();
        builder.setSmallIcon(2130838516);
        builder.setTicker(tickerText);
        builder.setWhen(when);
        builder.setContentTitle(tickerText);
        builder.setContentIntent(getPendingIntent(context));
        builder.setAutoCancel(true);
        ((NotificationManager) context.getContext().getSystemService("notification")).notify(getClass().getSimpleName(), getClass().getSimpleName().hashCode(), builder.build());
    }

    private final PendingIntent getPendingIntent(PersistentTaskContext context) {
        return PendingIntent.getActivity(context.getContext(), (int) System.currentTimeMillis(), NavigationHelper.createIntentForShowMessagesForConversation(context.getContext(), this.conversationId), 134217728);
    }

    protected void onSubTaskCompleted(PersistentTaskContext persistentContext, PersistentTask subTask) {
        super.onSubTaskCompleted(persistentContext, subTask);
        if (subTask instanceof UploadOneImageTask) {
            UploadOneImageTask uploadTask = (UploadOneImageTask) subTask;
            MessagesCache.getInstance().updateAttachmentStatusTokenDate(this.messageId.intValue(), uploadTask.getImageEditInfo().getId(), Status.UPLOADED, uploadTask.getToken(), System.currentTimeMillis());
            BusMessagingHelper.messageUpdated(this.messageId.intValue());
        }
    }

    private static ArrayList<ImageEditInfo> getAttachmentsAsImages(int messageId, long[] attachmentIds) {
        ArrayList<ImageEditInfo> images = new ArrayList();
        PhotoAlbumInfo album = PhotoAlbumsHelper.createEmptyAlbum("application", "");
        MessageModel message = MessagesCache.getInstance().getMessage(messageId);
        if (message == null) {
            Logger.m185w("Can't find message with id: %d", Integer.valueOf(messageId));
        } else {
            for (long attachmentId : attachmentIds) {
                for (Attach attach : message.message.getAttachesList()) {
                    if (attach.getUuid() == attachmentId) {
                        ImageEditInfo info = new ImageEditInfo();
                        Photo photo = attach.getPhoto();
                        info.setId(photo.getLocalId());
                        String path = photo.getPath();
                        info.setUri(TextUtils.isEmpty(path) ? null : Uri.parse(path));
                        info.setRotation(photo.getRotation());
                        info.setUploadTarget(3);
                        info.setAlbumInfo(album);
                        info.setWasEdited(false);
                        info.setTemporary(false);
                        info.setMimeType(!TextUtils.isEmpty(photo.getGifUrl()) ? "image/gif" : "image/jpeg");
                        images.add(info);
                    }
                }
            }
        }
        return images;
    }

    public long[] getAttachmentIds() {
        return this.attachmentIds;
    }

    public int getMessageId() {
        return this.messageId.intValue();
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.attachmentIds.length);
        dest.writeLongArray(this.attachmentIds);
        dest.writeInt(this.messageId.intValue());
        dest.writeString(this.conversationId);
    }

    static {
        CREATOR = new C04801();
    }
}
