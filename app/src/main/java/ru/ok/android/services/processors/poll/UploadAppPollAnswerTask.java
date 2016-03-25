package ru.ok.android.services.processors.poll;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import ru.ok.android.services.persistent.PersistentTask;
import ru.ok.android.services.persistent.PersistentTaskContext;
import ru.ok.android.services.persistent.PersistentTaskNotificationBuilder;
import ru.ok.android.services.persistent.PersistentTaskState;
import ru.ok.android.services.persistent.TaskException;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.services.transport.exception.TransportLevelException;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.polls.AppPollAnswerPostRequest;

public class UploadAppPollAnswerTask extends PersistentTask {
    public static final Creator<UploadAppPollAnswerTask> CREATOR;
    private static final long serialVersionUID = 1;
    private final String answersJson;
    private final boolean cancel;
    private final boolean intermediate;
    private final int step;
    private final int version;

    /* renamed from: ru.ok.android.services.processors.poll.UploadAppPollAnswerTask.1 */
    static class C04901 implements Creator<UploadAppPollAnswerTask> {
        C04901() {
        }

        public UploadAppPollAnswerTask createFromParcel(Parcel source) {
            return new UploadAppPollAnswerTask(source);
        }

        public UploadAppPollAnswerTask[] newArray(int size) {
            return new UploadAppPollAnswerTask[size];
        }
    }

    public UploadAppPollAnswerTask(String uid, int version, boolean cancel, boolean intermediate, int step, String answersJson) {
        super(uid, true);
        this.version = version;
        this.cancel = cancel;
        this.intermediate = intermediate;
        this.step = step;
        this.answersJson = answersJson;
    }

    public PersistentTaskState execute(PersistentTaskContext persistentContext, Context context) throws TaskException {
        BaseRequest request = new AppPollAnswerPostRequest(this.version, this.cancel, this.intermediate, this.step, this.answersJson);
        Logger.m172d("Attempt to upload app poll answer:" + request.toString());
        try {
            Logger.m172d("Completed:" + JsonSessionTransportProvider.getInstance().execJsonHttpMethod(request).getResultAsString());
        } catch (Throwable e) {
            if (e instanceof TransportLevelException) {
                Logger.m172d("Wait internet:");
                return PersistentTaskState.WAIT_INTERNET;
            }
            Logger.m178e(e);
        } catch (Throwable e2) {
            Logger.m178e(e2);
        }
        return PersistentTaskState.COMPLETED;
    }

    protected PendingIntent getTaskDetailsIntent(PersistentTaskContext persistentContext) {
        return null;
    }

    public void createNotification(PersistentTaskContext persistentContext, PersistentTaskNotificationBuilder notificationBulder) {
    }

    static {
        CREATOR = new C04901();
    }

    public PersistentTask copy() {
        Parcel parcel = toParcel();
        PersistentTask copy = new UploadAppPollAnswerTask(parcel);
        parcel.recycle();
        return copy;
    }

    protected UploadAppPollAnswerTask(Parcel src) {
        boolean z;
        boolean z2 = true;
        super(src);
        this.version = src.readInt();
        if (src.readByte() == (byte) 1) {
            z = true;
        } else {
            z = false;
        }
        this.cancel = z;
        this.answersJson = src.readString();
        this.step = src.readInt();
        if (src.readByte() != (byte) 1) {
            z2 = false;
        }
        this.intermediate = z2;
    }

    public void writeToParcel(Parcel dest, int flags) {
        int i;
        int i2 = 1;
        super.writeToParcel(dest, flags);
        dest.writeInt(this.version);
        if (this.cancel) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeByte((byte) i);
        dest.writeString(this.answersJson);
        dest.writeInt(this.step);
        if (!this.intermediate) {
            i2 = 0;
        }
        dest.writeByte((byte) i2);
    }
}
