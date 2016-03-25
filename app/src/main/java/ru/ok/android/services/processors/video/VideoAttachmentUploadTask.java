package ru.ok.android.services.processors.video;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import ru.ok.android.model.cache.ram.MessagesCache;
import ru.ok.android.proto.MessagesProto.Attach;
import ru.ok.android.proto.MessagesProto.Attach.Status;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.proto.ProtoProxy;
import ru.ok.android.services.AttachmentUtils;
import ru.ok.android.services.persistent.PersistentTask;
import ru.ok.android.services.persistent.PersistentTaskContext;
import ru.ok.android.services.persistent.PersistentTaskState;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.video.GetVideoUploadUrlRequest;
import ru.ok.model.messages.Attachment.AttachmentType;

public class VideoAttachmentUploadTask extends VideoUserUploadTask {
    public static final Creator<VideoAttachmentUploadTask> CREATOR;
    private static final long serialVersionUID = 1;
    private final long attachDatabaseId;
    private final String conversationId;
    private final int messageId;
    private int processVideoTaskId;
    private final String videoMailUserId;

    /* renamed from: ru.ok.android.services.processors.video.VideoAttachmentUploadTask.1 */
    static class C05091 implements Creator<VideoAttachmentUploadTask> {
        C05091() {
        }

        public VideoAttachmentUploadTask createFromParcel(Parcel source) {
            return new VideoAttachmentUploadTask(source);
        }

        public VideoAttachmentUploadTask[] newArray(int size) {
            return new VideoAttachmentUploadTask[size];
        }
    }

    /* renamed from: ru.ok.android.services.processors.video.VideoAttachmentUploadTask.2 */
    static /* synthetic */ class C05102 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState;

        static {
            $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState = new int[PersistentTaskState.values().length];
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.COMPLETED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.ERROR.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.FAILED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.PAUSED.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.SUBMITTED.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.EXECUTING.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.WAIT_INTERNET.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    public VideoAttachmentUploadTask(String uid, MediaInfo mediaInfo, String userId, long attachDatabaseId, String conversationId, int messageId, int parentTaskId) {
        super(uid, mediaInfo, parentTaskId);
        this.videoMailUserId = userId;
        this.messageId = messageId;
        this.attachDatabaseId = attachDatabaseId;
        this.conversationId = conversationId;
    }

    protected VideoAttachmentUploadTask(Parcel src) {
        super(src);
        this.videoMailUserId = src.readString();
        this.messageId = src.readInt();
        this.conversationId = src.readString();
        this.attachDatabaseId = src.readLong();
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.videoMailUserId);
        dest.writeInt(this.messageId);
        dest.writeString(this.conversationId);
        dest.writeLong(this.attachDatabaseId);
    }

    protected BaseRequest getUploadUrlRequest(long fileSize, String fileName) {
        if (isVideoMail()) {
            AttachmentType proto2Api;
            Attach attach = MessagesCache.getInstance().getAttachByUUID(this.messageId, this.attachDatabaseId);
            if (attach != null) {
                proto2Api = ProtoProxy.proto2Api(attach.getType());
            } else {
                proto2Api = null;
            }
            return new GetVideoUploadUrlRequest(null, fileName, fileSize, proto2Api);
        }
        return new GetVideoUploadUrlRequest(null, fileName, fileSize, AttachmentType.VIDEO);
    }

    protected PersistentTaskState onFileUploadCompleted(PersistentTaskContext persistentContext, long videoId) throws VideoUploadException {
        if (isVideoMail()) {
            MessagesCache.getInstance().updateAttachMediaServerId(this.messageId, this.attachDatabaseId, videoId);
            ProcessVideoAttachTask processVideoAttachTask = null;
            if (this.processVideoTaskId != 0) {
                processVideoAttachTask = (ProcessVideoAttachTask) getSubTask(persistentContext, this.processVideoTaskId);
                if (processVideoAttachTask == null) {
                    Logger.m185w("Sub-task not found: id=%d", Integer.valueOf(this.processVideoTaskId));
                }
            }
            if (processVideoAttachTask == null) {
                this.processVideoTaskId = submitSubTask(persistentContext, new ProcessVideoAttachTask(getUid(), getId(), Long.toString(videoId), System.currentTimeMillis()));
            } else {
                switch (C05102.$SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[processVideoAttachTask.getState().ordinal()]) {
                    case Message.TEXT_FIELD_NUMBER /*1*/:
                        finish(persistentContext);
                        return PersistentTaskState.COMPLETED;
                    case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    case Message.TYPE_FIELD_NUMBER /*3*/:
                        return PersistentTaskState.FAILED;
                    default:
                        Logger.m172d("Waiting for process video task to complete...");
                        return PersistentTaskState.WAIT;
                }
            }
        }
        return PersistentTaskState.EXECUTING;
    }

    protected void onSubTaskCompleted(PersistentTaskContext persistentContext, PersistentTask subTask) {
        Logger.m173d("subTask=%s", subTask);
        super.onSubTaskCompleted(persistentContext, subTask);
    }

    protected void onCancel(PersistentTaskContext persistentContext) {
        super.onCancel(persistentContext);
        if (this.processVideoTaskId != 0) {
            PersistentTask subTask = persistentContext.getTask(this.processVideoTaskId);
            if (subTask != null) {
                persistentContext.cancelSubTask(subTask);
            }
        }
    }

    public boolean isVideoMail() {
        return this.videoMailUserId != null;
    }

    protected boolean canCancelFromNotification() {
        return false;
    }

    protected String getNotificationTitle(LocalizationManager localizationManager) {
        return localizationManager.getString(2131166813);
    }

    protected String getStatusText(LocalizationManager localizationManager) {
        int statusText;
        switch (C05102.$SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[getState().ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                statusText = 2131165412;
                break;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return getErrorMessage(localizationManager);
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                statusText = 2131165413;
                break;
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
                statusText = 2131166851;
                break;
            default:
                statusText = 2131166812;
                break;
        }
        return localizationManager.getString(statusText);
    }

    protected String getDefaultErrorMessage(LocalizationManager localizationManager) {
        return localizationManager.getString(2131166811);
    }

    public PersistentTask copy() {
        Parcel parcel = toParcel();
        PersistentTask copy = new VideoAttachmentUploadTask(parcel);
        parcel.recycle();
        return copy;
    }

    protected void onUploadUrlAndVideoIdReceived(PersistentTaskContext persistentContext) {
        super.onUploadUrlAndVideoIdReceived(persistentContext);
        MessagesCache.getInstance().updateAttachMediaServerId(this.messageId, this.attachDatabaseId, getVideoId());
    }

    protected void onStateChanged(PersistentTaskContext persistentContext) {
        super.onStateChanged(persistentContext);
        Status newAttachmentState = null;
        switch (C05102.$SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[getState().ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                newAttachmentState = Status.UPLOADED;
                break;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                newAttachmentState = Status.ERROR;
                break;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                newAttachmentState = Status.RECOVERABLE_ERROR;
                break;
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                newAttachmentState = Status.UPLOADING;
                break;
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
                newAttachmentState = Status.WAITING;
                break;
        }
        if (newAttachmentState != null) {
            AttachmentUtils.updateAttachmentState(this.messageId, this.attachDatabaseId, newAttachmentState);
        }
    }

    protected void onSubTaskStateChanged(PersistentTaskContext persistentContext, PersistentTask subTask) {
        Logger.m173d("subTask=%s", subTask);
        if (subTask instanceof ProcessVideoAttachTask) {
            PersistentTaskState processingState = subTask.getState();
            if (processingState == PersistentTaskState.ERROR || processingState == PersistentTaskState.FAILED) {
                AttachmentUtils.updateAttachmentState(this.messageId, this.attachDatabaseId, Status.ERROR);
            }
            setState(persistentContext, processingState);
        }
    }

    protected void cleanup(PersistentTaskContext persistentTaskContext) {
    }

    protected PendingIntent getTaskDetailsIntent(PersistentTaskContext persistentContext) {
        return PendingIntent.getActivity(persistentContext.getContext(), 0, NavigationHelper.createIntentForShowMessagesForConversation(persistentContext.getContext(), this.conversationId), 268435456);
    }

    protected void showCompletedNotification(Context context) {
    }

    static {
        CREATOR = new C05091();
    }
}
