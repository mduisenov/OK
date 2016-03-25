package ru.ok.android.services.processors.video;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.app.NotificationCompat;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collections;
import org.json.JSONArray;
import org.json.JSONException;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.persistent.PersistentTask;
import ru.ok.android.services.persistent.PersistentTaskContext;
import ru.ok.android.services.persistent.PersistentTaskNotificationBuilder;
import ru.ok.android.services.persistent.PersistentTaskState;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.services.transport.exception.TransportLevelException;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NetUtils;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.exceptions.ResultParsingException;
import ru.ok.java.api.exceptions.ServerReturnErrorException;
import ru.ok.java.api.request.video.VideoGetRequest;
import ru.ok.java.api.utils.JsonUtil;

public final class ProcessVideoAttachTask extends PersistentTask {
    public static final Creator<ProcessVideoAttachTask> CREATOR;
    private static final long serialVersionUID = 1;
    private int attemptsCount;
    private long fileUploadCompletedTs;
    private long lastRequestTs;
    private volatile transient Object lock;
    private long requestIntervalMs;
    private final String videoId;

    /* renamed from: ru.ok.android.services.processors.video.ProcessVideoAttachTask.1 */
    static class C05071 implements Creator<ProcessVideoAttachTask> {
        C05071() {
        }

        public ProcessVideoAttachTask createFromParcel(Parcel source) {
            return new ProcessVideoAttachTask(source);
        }

        public ProcessVideoAttachTask[] newArray(int size) {
            return new ProcessVideoAttachTask[size];
        }
    }

    @Subscribe(on = 2131623944, to = 2131624085)
    public void onProcessVideo(BusEvent event) {
        if (this.videoId.equals(String.valueOf(event.bundleInput.getLong("VIDEO_ID", -1)))) {
            notifyLoc();
        }
    }

    public ProcessVideoAttachTask(String uid, int parentTaskId, String videoId, long fileUploadCompletedTs) {
        super(uid, true, parentTaskId);
        this.videoId = videoId;
        this.fileUploadCompletedTs = fileUploadCompletedTs;
        this.requestIntervalMs = 3000;
    }

    public ProcessVideoAttachTask(Parcel src) {
        super(src);
        this.videoId = src.readString();
        this.fileUploadCompletedTs = src.readLong();
        this.attemptsCount = src.readInt();
        this.requestIntervalMs = src.readLong();
        this.lastRequestTs = src.readLong();
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.videoId);
        dest.writeLong(this.fileUploadCompletedTs);
        dest.writeInt(this.attemptsCount);
        dest.writeLong(this.requestIntervalMs);
        dest.writeLong(this.lastRequestTs);
    }

    private boolean isValidProcessStatus(String status) {
        return "OK".equals(status) || "ON_MODERATION".equals(status);
    }

    public void notifyLoc() {
        if (this.lock != null) {
            synchronized (this.lock) {
                this.lock.notify();
            }
        }
    }

    private synchronized void waitLoc(long delay) throws InterruptedException {
        if (this.lock == null) {
            this.lock = new Object();
        }
        synchronized (this.lock) {
            this.lock.wait(delay);
        }
    }

    public PersistentTaskState execute(PersistentTaskContext persistentTaskContext, Context context) throws VideoUploadException {
        PersistentTaskState persistentTaskState;
        GlobalBus.register(this);
        VideoGetRequest request = createVideoGetRequest();
        while (!isCanceled()) {
            long nowTs = System.currentTimeMillis();
            long nextAttemptTs = this.lastRequestTs + this.requestIntervalMs;
            r9 = new Object[4];
            r9[2] = Long.valueOf(Math.max(0, 60000 - (nowTs - this.fileUploadCompletedTs)));
            r9[3] = Long.valueOf(Math.max(0, nextAttemptTs - nowTs));
            Logger.m173d("attempts count: %d, request interval: %d, time before timeout: %d, next attempt in: %d", r9);
            if (nextAttemptTs > nowTs) {
                if (nowTs - this.fileUploadCompletedTs > 60000 && this.attemptsCount > 0) {
                    Logger.m185w("Failed to get video status by timeout after %d attempts", Integer.valueOf(this.attemptsCount));
                    persistentTaskState = PersistentTaskState.FAILED;
                    GlobalBus.unregister(this);
                    break;
                }
                waitLoc(nextAttemptTs - nowTs);
            }
            try {
                if (doStatusRequest(request)) {
                    persistentTaskState = PersistentTaskState.COMPLETED;
                    this.lastRequestTs = System.currentTimeMillis();
                    this.requestIntervalMs += 1000;
                    this.attemptsCount++;
                    persist(persistentTaskContext);
                    GlobalBus.unregister(this);
                    break;
                }
                this.lastRequestTs = System.currentTimeMillis();
                this.requestIntervalMs += 1000;
                this.attemptsCount++;
                persist(persistentTaskContext);
            } catch (InterruptedException e) {
                Logger.m184w("get video status - interrupted waitLoc method");
                persistentTaskState = PersistentTaskState.CANCELED;
                GlobalBus.unregister(this);
            } catch (Throwable th) {
                GlobalBus.unregister(this);
            }
        }
        persistentTaskState = PersistentTaskState.CANCELED;
        GlobalBus.unregister(this);
        return persistentTaskState;
    }

    private boolean doStatusRequest(VideoGetRequest request) throws VideoUploadException {
        boolean z = false;
        try {
            Logger.m173d("Performing status request for videoId=%s...", this.videoId);
            String status = parseStatus(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(request));
            Logger.m173d("Received video status: %s", status);
            if ("ERROR".equals(status)) {
                throw new ServerReturnErrorException(1, "video.get returned status=ERROR");
            }
            z = isValidProcessStatus(status);
            return z;
        } catch (ServerReturnErrorException e) {
            Logger.m180e(e, "Failed to get status of video: %s", e);
            throw new VideoUploadException(4, "Failed to get video status", e);
        } catch (TransportLevelException e2) {
            if (!NetUtils.isConnectionAvailable(OdnoklassnikiApplication.getContext(), true)) {
                throw new VideoUploadException(1);
            }
        } catch (Exception e3) {
            Logger.m173d("error get status: %s", e3);
        }
    }

    private VideoGetRequest createVideoGetRequest() {
        return new VideoGetRequest(Collections.singletonList(this.videoId), "video.status");
    }

    private String parseStatus(JsonHttpResult response) throws ResultParsingException {
        try {
            JSONArray videos = response.getResultAsObject().optJSONArray("videos");
            if (videos == null) {
                throw new ResultParsingException("Missing videos array in response");
            } else if (videos.length() == 0) {
                throw new ResultParsingException("Empty videos array in response");
            } else {
                String status = JsonUtil.optStringOrNull(videos.getJSONObject(0), NotificationCompat.CATEGORY_STATUS);
                if (status != null) {
                    return status;
                }
                throw new ResultParsingException("Missing videos[0].status in response");
            }
        } catch (JSONException e) {
            throw new ResultParsingException(e);
        }
    }

    protected PendingIntent getTaskDetailsIntent(PersistentTaskContext persistentContext) {
        return null;
    }

    public void createNotification(PersistentTaskContext persistentContext, PersistentTaskNotificationBuilder notificationBulder) {
    }

    public PersistentTask copy() {
        Parcel parcel = toParcel();
        PersistentTask copy = new ProcessVideoAttachTask(parcel);
        parcel.recycle();
        return copy;
    }

    static {
        CREATOR = new C05071();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (this.fileUploadCompletedTs == 0) {
            this.fileUploadCompletedTs = System.currentTimeMillis();
        }
        if (this.requestIntervalMs == 0) {
            this.requestIntervalMs = 3000;
        }
    }

    public String toString() {
        return "ProcessVideoAttachTask[videoId=" + this.videoId + " fileUploadCompletedTs=" + this.fileUploadCompletedTs + " attemptsCount=" + this.attemptsCount + " requestIntervalTs=" + this.requestIntervalMs + " lastRequestTs=" + this.lastRequestTs + " taskId=" + getId() + " parentTaskId=" + getParentId() + " state=" + getState() + "]";
    }
}
