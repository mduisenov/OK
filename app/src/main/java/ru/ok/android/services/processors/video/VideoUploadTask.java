package ru.ok.android.services.processors.video;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.TextUtils;
import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jivesoftware.smack.util.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import ru.ok.android.http.HttpResponse;
import ru.ok.android.http.client.methods.HttpPost;
import ru.ok.android.http.client.methods.HttpUriRequest;
import ru.ok.android.http.support.v1.AndroidHttpClients;
import ru.ok.android.onelog.AppLaunchLog;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.persistent.PersistentTask;
import ru.ok.android.services.persistent.PersistentTaskContext;
import ru.ok.android.services.persistent.PersistentTaskNotificationBuilder;
import ru.ok.android.services.persistent.PersistentTaskState;
import ru.ok.android.services.persistent.PersistentTaskUtils;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.services.transport.TransportUtils;
import ru.ok.android.services.transport.exception.TransportLevelException;
import ru.ok.android.ui.activity.VideoUploadStatusActivity;
import ru.ok.android.ui.activity.main.OdklActivity;
import ru.ok.android.utils.IOUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NetUtils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.exceptions.ServerReturnErrorException;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.image.ObservableInputStream.InputStreamObserver;
import ru.ok.java.api.request.video.VideoUpdateRequest;

public abstract class VideoUploadTask extends PersistentTask {
    private static final Pattern rangePattern;
    private static final long serialVersionUID = 2;
    private boolean fileUploadCompleted;
    private boolean isPrivate;
    private long lastProgressTime;
    private transient long lastReportedProgressTs;
    private final MediaInfo mediaInfo;
    private volatile transient HttpUriRequest ongoingRequest;
    private int progress;
    private long retryDelay;
    private transient long startPosition;
    private transient Bitmap thumbnail;
    private transient boolean thumbnailLoaded;
    private String title;
    private String uploadUrl;
    private int uploadUrlCount;
    private long videoId;

    /* renamed from: ru.ok.android.services.processors.video.VideoUploadTask.1 */
    class C05131 implements InputStreamObserver {
        final /* synthetic */ long val$fileSize;
        final /* synthetic */ PersistentTaskContext val$persistentContext;
        final /* synthetic */ long val$startPosition;

        C05131(PersistentTaskContext persistentTaskContext, long j, long j2) {
            this.val$persistentContext = persistentTaskContext;
            this.val$startPosition = j;
            this.val$fileSize = j2;
        }

        public void onInputStreamProgress(long chunkSize, long totalReadSize) {
            VideoUploadTask.this.onPosition(this.val$persistentContext, this.val$startPosition + totalReadSize, this.val$fileSize);
        }
    }

    /* renamed from: ru.ok.android.services.processors.video.VideoUploadTask.2 */
    static /* synthetic */ class C05142 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState;

        static {
            $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState = new int[PersistentTaskState.values().length];
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.PAUSED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.COMPLETED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.ERROR.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.WAIT_INTERNET.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.WAIT_EXTERNAL_STORAGE.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.FAILED.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.EXECUTING.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.SUBMITTED.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
        }
    }

    static class GetVideoUploadUrlResponse {
        final String uploadUrl;
        final long videoId;

        GetVideoUploadUrlResponse(String uploadUrl, long videoId) {
            this.uploadUrl = uploadUrl;
            this.videoId = videoId;
        }

        public String toString() {
            return "GetVideoUploadUrlResponse[uploadUrl=" + this.uploadUrl + " videoId=" + this.videoId + "]";
        }
    }

    protected abstract BaseRequest getUploadUrlRequest(long j, String str);

    public abstract boolean isVideoMail();

    static {
        rangePattern = Pattern.compile("^([0-9]+)-([0-9]+)/([0-9]+)");
    }

    public VideoUploadTask(String uid, MediaInfo mediaInfo, int parentId) {
        super(uid, false, parentId);
        this.fileUploadCompleted = false;
        this.retryDelay = 3000;
        this.mediaInfo = mediaInfo;
    }

    public VideoUploadTask(String uid, MediaInfo mediaInfo) {
        this(uid, mediaInfo, 0);
    }

    protected VideoUploadTask(Parcel src) {
        boolean z;
        boolean z2 = true;
        super(src);
        this.fileUploadCompleted = false;
        this.retryDelay = 3000;
        this.mediaInfo = (MediaInfo) src.readParcelable(VideoUploadTask.class.getClassLoader());
        this.uploadUrl = src.readString();
        this.progress = src.readInt();
        this.videoId = src.readLong();
        this.title = src.readString();
        if (src.readInt() != 0) {
            z = true;
        } else {
            z = false;
        }
        this.isPrivate = z;
        if (src.readInt() == 0) {
            z2 = false;
        }
        this.fileUploadCompleted = z2;
    }

    public void writeToParcel(Parcel dest, int flags) {
        int i;
        int i2 = 1;
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.mediaInfo, flags);
        dest.writeString(this.uploadUrl);
        dest.writeInt(this.progress);
        dest.writeLong(this.videoId);
        dest.writeString(this.title);
        if (this.isPrivate) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeInt(i);
        if (!this.fileUploadCompleted) {
            i2 = 0;
        }
        dest.writeInt(i2);
    }

    public PersistentTaskState execute(PersistentTaskContext persistentContext, Context context) throws VideoUploadException {
        Logger.m173d("mediaInfo=%s", this.mediaInfo);
        if (this.mediaInfo == null) {
            throw new VideoUploadException(21, "Video upload failed - failed to open video", null);
        }
        long fileSize = this.mediaInfo.getSizeBytes();
        String fileName = createFileName(this.mediaInfo.getDisplayName());
        if (fileSize > 0 || fileSize == -1) {
            if (fileSize == -1) {
                fileSize = 0;
            }
            boolean firstUpload = false;
            if (TextUtils.isEmpty(this.uploadUrl) || this.videoId == 0) {
                firstUpload = true;
                doGetUploadUrl(persistentContext, fileSize, fileName);
            }
            resetRetryStats();
            persist(persistentContext);
            if (isCanceled()) {
                throw new VideoUploadException(3);
            }
            URL url = null;
            if (!this.fileUploadCompleted) {
                this.startPosition = 0;
                try {
                    URL url2 = new URL(this.uploadUrl);
                    if (!firstUpload) {
                        try {
                            doGetCurrentPosition(persistentContext, url2);
                            onPosition(persistentContext, this.startPosition, fileSize);
                        } catch (VideoUploadException e) {
                            if (e.getErrorCode() == 26) {
                                this.uploadUrl = null;
                                this.videoId = 0;
                                this.progress = 0;
                                onUploadUrlAndVideoIdReceived(persistentContext);
                                if (this.uploadUrlCount < 3) {
                                    Logger.m173d("Upload URL has expired, will try again with new URL, attempts count: %d/%d", Integer.valueOf(this.uploadUrlCount), Integer.valueOf(3));
                                    return PersistentTaskState.EXECUTING;
                                }
                                Logger.m185w("Upload URL has expired, reached max attempts count (%d).", Integer.valueOf(this.uploadUrlCount));
                            }
                            throw e;
                        }
                    }
                    if (fileSize <= this.startPosition + 1) {
                        Logger.m172d("upload complete");
                        this.fileUploadCompleted = true;
                        persist(persistentContext);
                    }
                    if (isCanceled()) {
                        throw new VideoUploadException(3);
                    }
                } catch (MalformedURLException e2) {
                    Logger.m180e(e2, "Invalid upload URL: %s", this.uploadUrl);
                    throw new VideoUploadException(25, "Invalid upload URL", e2);
                }
            }
            if (!this.fileUploadCompleted) {
                try {
                    uploadMedia(persistentContext, this.mediaInfo, this.startPosition, url);
                    this.fileUploadCompleted = true;
                    persist(persistentContext);
                } catch (Throwable ex) {
                    Logger.m179e(ex, "Upload error");
                    throw ex;
                } catch (Throwable e3) {
                    Logger.m179e(e3, "Failed to upload video");
                    throw new VideoUploadException(999, null, e3);
                }
            }
            return onFileUploadCompleted(persistentContext, this.videoId);
        }
        throw new VideoUploadException(21, "Video upload failed - invalid file size: " + fileSize, null);
    }

    protected PersistentTaskState onFileUploadCompleted(PersistentTaskContext persistentContext, long videoId) throws VideoUploadException {
        if (TextUtils.isEmpty(this.title)) {
            Logger.m184w("title is empty. pausing until title is provided");
            return PersistentTaskState.PAUSED;
        }
        publishVideo(persistentContext);
        finish(persistentContext);
        return PersistentTaskState.COMPLETED;
    }

    private void doGetUploadUrl(PersistentTaskContext persistentContext, long fileSize, String fileName) throws VideoUploadException {
        GetVideoUploadUrlResponse resp = null;
        if (this.uploadUrlCount >= 3) {
            throw new VideoUploadException(26, "Reached max number of attempts (" + this.uploadUrlCount + ") after upload URL had expired", null);
        }
        try {
            resp = fetchUploadUrl(persistentContext, fileSize, fileName);
            if (resp == null) {
                persistentContext.scheduleRetry(this, getNextRetryDelay());
            }
            this.uploadUrl = resp.uploadUrl;
            this.videoId = resp.videoId;
            this.uploadUrlCount++;
            Logger.m173d("obtained upload URL: url=%s, id=%d, attempt count=%d", this.uploadUrl, Long.valueOf(this.videoId), Integer.valueOf(this.uploadUrlCount));
            onUploadUrlAndVideoIdReceived(persistentContext);
        } catch (VideoUploadException ex) {
            Logger.m185w("Attempt to get upload url failed: %s", ex);
            throw ex;
        } catch (Throwable ex2) {
            Logger.m179e(ex2, "Video upload failed - will retry");
            throw new VideoUploadException(999, null, ex2);
        } catch (Throwable th) {
            if (resp == null) {
                persistentContext.scheduleRetry(this, getNextRetryDelay());
            }
        }
    }

    protected void onUploadUrlAndVideoIdReceived(PersistentTaskContext persistentContext) {
    }

    private void doGetCurrentPosition(PersistentTaskContext persistentContext, URL url) throws VideoUploadException {
        try {
            this.startPosition = getCurrentPosition(persistentContext, url);
            if (null != null) {
                persistentContext.scheduleRetry(this, getNextRetryDelay());
            }
        } catch (VideoUploadException e) {
            Logger.m177e("video upload error: %s", e);
            throw e;
        } catch (Throwable e2) {
            Logger.m179e(e2, "Video upload failed - will retry");
            throw new VideoUploadException(999, null, e2);
        } catch (Throwable th) {
            if (null != null) {
                persistentContext.scheduleRetry(this, getNextRetryDelay());
            }
        }
    }

    private long getNextRetryDelay() {
        long j = 0;
        if (this.lastProgressTime <= 0 || System.currentTimeMillis() - this.lastProgressTime <= 600000) {
            j = this.retryDelay;
            this.retryDelay *= serialVersionUID;
            if (this.retryDelay > 30000) {
                this.retryDelay = 30000;
            }
        }
        return j;
    }

    protected void onPausing(PersistentTaskContext persistentContext) {
        Logger.m172d("");
        cancelUpload();
    }

    protected void onCancel(PersistentTaskContext persistentContext) {
        Logger.m172d("task cancelled");
        cancelUpload();
        cleanup(persistentContext);
    }

    private void cancelUpload() {
        HttpUriRequest ongoingRequest = this.ongoingRequest;
        if (ongoingRequest != null) {
            try {
                ongoingRequest.abort();
            } catch (Exception e) {
            }
        }
    }

    protected void cleanup(PersistentTaskContext persistentTaskContext) {
        if (this.mediaInfo != null) {
            this.mediaInfo.cleanUp();
        }
    }

    private void onPosition(PersistentTaskContext persistentContext, long position, long fileSize) {
        int progress = (int) ((((double) ((float) position)) * 100.0d) / ((double) ((float) fileSize)));
        if (progress > 100) {
            progress = 100;
        }
        if (progress < 0) {
            progress = 0;
        }
        if (progress > this.progress) {
            Logger.m173d("upload progress: %d", Integer.valueOf(progress));
            this.progress = progress;
            resetRetryStats();
            persist(persistentContext);
            long now = System.currentTimeMillis();
            if (this.lastReportedProgressTs == 0 || now - this.lastReportedProgressTs > 1000) {
                persistentContext.notifyOnChanged(this);
                this.lastReportedProgressTs = now;
            }
        }
    }

    private void resetRetryStats() {
        this.retryDelay = 3000;
        this.lastProgressTime = System.currentTimeMillis();
    }

    private void uploadMedia(PersistentTaskContext persistentContext, MediaInfo mediaInfo, long startPosition, URL uploadUrl) throws VideoUploadException {
        Logger.m173d(">>> mediaInfo=%s startPosition=%d uploadUrl=%s", mediaInfo, Long.valueOf(startPosition), uploadUrl);
        Context context = persistentContext.getContext();
        Closeable client = null;
        try {
            long fileSize = mediaInfo.getSizeBytes();
            String fileName = createFileName(mediaInfo.getDisplayName());
            HttpPost httpPost = new HttpPost(uploadUrl.toURI());
            httpPost.addHeader("Content-Type", "application/x-binary; charset=x-user-defined");
            httpPost.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            httpPost.addHeader("Content-Range", "bytes " + startPosition + "-/" + fileSize);
            httpPost = httpPost;
            httpPost.setEntity(new MediaInputEntity(context.getContentResolver(), mediaInfo, startPosition, new C05131(persistentContext, startPosition, fileSize)));
            client = AndroidHttpClients.create(TransportUtils.getAPIUserAgent());
            try {
                this.ongoingRequest = httpPost;
                HttpResponse response = client.execute(httpPost);
                this.ongoingRequest = null;
                int respCode = response.getStatusLine().getStatusCode();
                Logger.m173d("Upload response: code=%d message=%s", Integer.valueOf(respCode), response.getStatusLine().getReasonPhrase());
                if (200 == respCode) {
                    Logger.m172d("Upload complete");
                    IOUtils.closeSilently(client);
                    return;
                }
                Logger.m176e("Upload error");
                throw new VideoUploadException(4, "Error response code: " + respCode, new ServerReturnErrorException(1, "Upload error: code=" + respCode + "; response=" + respMessage));
            } catch (Throwable e) {
                Logger.m179e(e, "Failed to execute HTTP method");
                if (isCanceled()) {
                    throw new VideoUploadException(3);
                }
                throw convertNetworkIOError(context, e);
            } catch (RuntimeException e2) {
                Throwable cause = e2.getCause();
                if (cause instanceof VideoUploadException) {
                    throw ((VideoUploadException) cause);
                }
                throw e2;
            } catch (Throwable th) {
                this.ongoingRequest = null;
            }
        } catch (Throwable e3) {
            try {
                Throwable th2 = e3;
                Logger.m180e(th2, "Upload url format problem: %s", uploadUrl);
                throw new VideoUploadException(24, "Upload url format problem", e3);
            } catch (Throwable th3) {
                IOUtils.closeSilently(client);
            }
        }
    }

    private VideoUploadException convertNetworkIOError(Context context, Exception e) {
        if (PersistentTaskUtils.checkForInternetConnection(context)) {
            return new VideoUploadException(23, e.getMessage(), e);
        }
        return new VideoUploadException(1);
    }

    private long getCurrentPosition(PersistentTaskContext persistentContext, URL url) throws VideoUploadException {
        Throwable e;
        Throwable th;
        Logger.m173d(">>> url=%s", url);
        HttpURLConnection statusConnection = null;
        Closeable closeable = null;
        try {
            statusConnection = (HttpURLConnection) url.openConnection(NetUtils.getProxyForUrl(url));
            TransportUtils.addGeneralHeaders(statusConnection);
            int responseCode = statusConnection.getResponseCode();
            if (responseCode == 404) {
                Logger.m172d("<<< Not found, position=0");
                IOUtils.closeSilently(null);
                IOUtils.disconnectSilently(statusConnection);
                return 0;
            } else if (responseCode == 200) {
                Closeable in = new BufferedInputStream(statusConnection.getInputStream());
                try {
                    Matcher matcher = rangePattern.matcher(IOUtils.inputStreamToString(in));
                    if (matcher.find()) {
                        Logger.m173d("<<< position=%d", Integer.valueOf(Integer.parseInt(matcher.group(2))));
                        long parseInt = (long) Integer.parseInt(matcher.group(2));
                        IOUtils.closeSilently(in);
                        IOUtils.disconnectSilently(statusConnection);
                        closeable = in;
                        return parseInt;
                    }
                    Logger.m177e("<<< Unexpected response from server, range not found: %s", result);
                    throw new VideoUploadException(25, "Failed to get current position", null);
                } catch (IOException e2) {
                    e = e2;
                    closeable = in;
                    try {
                        Logger.m177e("Error occurred when getting current upload position: %s", e);
                        Logger.m178e(e);
                        throw convertNetworkIOError(persistentContext.getContext(), e);
                    } catch (Throwable th2) {
                        th = th2;
                        IOUtils.closeSilently(closeable);
                        IOUtils.disconnectSilently(statusConnection);
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    closeable = in;
                    IOUtils.closeSilently(closeable);
                    IOUtils.disconnectSilently(statusConnection);
                    throw th;
                }
            } else if (responseCode == 403 || responseCode == 410) {
                Logger.m184w("Upload URL has expired");
                throw new VideoUploadException(26, "Upload URL expired: " + url, null);
            } else {
                Logger.m177e("<<< upload status error: code=%d response=%s", Integer.valueOf(responseCode), statusConnection.getResponseMessage());
                if (Logger.isLoggingEnable()) {
                    try {
                        BufferedInputStream in2 = new BufferedInputStream(statusConnection.getErrorStream());
                        try {
                            Logger.m185w("Error stream from server: %s", IOUtils.inputStreamToString(in2).substring(0, 200));
                            closeable = in2;
                        } catch (Exception e3) {
                            e = e3;
                            Object obj = in2;
                            Logger.m179e(e, "Failed to read error from server");
                            throw new VideoUploadException(4, null, new ServerReturnErrorException(1, "Server returned error: " + responseCode + ", " + responseMessage));
                        }
                    } catch (Exception e4) {
                        e = e4;
                        Logger.m179e(e, "Failed to read error from server");
                        throw new VideoUploadException(4, null, new ServerReturnErrorException(1, "Server returned error: " + responseCode + ", " + responseMessage));
                    }
                }
                throw new VideoUploadException(4, null, new ServerReturnErrorException(1, "Server returned error: " + responseCode + ", " + responseMessage));
            }
        } catch (IOException e5) {
            e = e5;
            Logger.m177e("Error occurred when getting current upload position: %s", e);
            Logger.m178e(e);
            throw convertNetworkIOError(persistentContext.getContext(), e);
        }
    }

    private void publishVideo(PersistentTaskContext persistentContext) throws VideoUploadException {
        Logger.m172d(">>>");
        try {
            JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new VideoUpdateRequest(Long.valueOf(this.videoId), this.title, null, null, this.isPrivate ? "FRIENDS" : "PUBLIC"));
            Logger.m172d("<<< ok");
        } catch (ServerReturnErrorException e) {
            Logger.m180e(e, "Failed to update video: %s", e);
            throw new VideoUploadException(4, "Failed to get upload URL", e);
        } catch (TransportLevelException e2) {
            Logger.m180e(e2, "getUploadUrl method failed due to network problems: %s", e2);
            throw convertNetworkIOError(persistentContext.getContext(), e2);
        } catch (BaseApiException e3) {
            Logger.m180e(e3, "getUploadUrl method failed: %s", e3);
            throw new VideoUploadException(25);
        }
    }

    private GetVideoUploadUrlResponse fetchUploadUrl(PersistentTaskContext persistentContext, long fileSize, String fileName) throws VideoUploadException {
        try {
            JSONObject json = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(getUploadUrlRequest(fileSize, fileName)).getResultAsObject();
            long videoId = -1;
            if (json.has("video_id")) {
                videoId = json.getLong("video_id");
            }
            return new GetVideoUploadUrlResponse(json.getString("upload_url"), videoId);
        } catch (JSONException e) {
            Logger.m180e(e, "Failed to parse response from server: %s", e);
            throw new VideoUploadException(25, "Unexpected response from server", e);
        } catch (ServerReturnErrorException e2) {
            Logger.m180e(e2, "Failed to get video upload url: %s", e2);
            throw new VideoUploadException(4, "Failed to get upload URL", e2);
        } catch (TransportLevelException e3) {
            Logger.m180e(e3, "getUploadUrl method failed due to network problems: %s", e3);
            throw new VideoUploadException(1);
        } catch (BaseApiException e4) {
            Logger.m180e(e4, "getUploadUrl method failed: %s", e4);
            throw new VideoUploadException(25);
        }
    }

    protected PendingIntent getTaskDetailsIntent(PersistentTaskContext persistentContext) {
        Uri uri = Uri.parse("content://ru.ok.android/persistent_task/" + getId());
        Intent intent = new Intent(persistentContext.getContext(), VideoUploadStatusActivity.class);
        intent.setData(uri);
        intent.putExtra("video_upload_task", this);
        AppLaunchLog.fillLocalVideoUploadInProgress(intent);
        Logger.m172d("Setting task: " + this);
        return PendingIntent.getActivity(persistentContext.getContext(), 0, intent, 268435456);
    }

    public void createNotification(PersistentTaskContext persistentContext, PersistentTaskNotificationBuilder notificationBuilder) {
        int smallIconResId;
        switch (C05142.$SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[getState().ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                smallIconResId = 2130838515;
                break;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                smallIconResId = 2130838517;
                break;
            case Message.TYPE_FIELD_NUMBER /*3*/:
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
            case Message.UUID_FIELD_NUMBER /*5*/:
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                smallIconResId = 2130838516;
                break;
            default:
                smallIconResId = 2130838515;
                break;
        }
        Context context = persistentContext.getContext();
        LocalizationManager localizationManager = LocalizationManager.from(context);
        notificationBuilder.setSmallIcon(smallIconResId);
        notificationBuilder.setTitle(getNotificationTitle(localizationManager));
        notificationBuilder.setText(getStatusText(localizationManager));
        Bitmap thumb = getThumbnail(context);
        if (thumb != null) {
            notificationBuilder.setLargeIcon(thumb);
        }
        if (canCancelFromNotification()) {
            Intent cancel = new Intent(context, VideoUploadStatusActivity.class);
            cancel.putExtra("cancel", true);
            cancel.putExtra("video_upload_task", this);
            AppLaunchLog.fillLocalVideoUploadCancel(cancel);
            notificationBuilder.addCancelAction(context, localizationManager, PendingIntent.getActivity(context, getId(), cancel, 134217728));
        }
        notificationBuilder.setProgress(this.progress, 100);
    }

    protected String getNotificationTitle(LocalizationManager localizationManager) {
        return localizationManager.getString(2131166852);
    }

    protected boolean canCancelFromNotification() {
        return true;
    }

    protected Bitmap getThumbnail(Context context) {
        if (!this.thumbnailLoaded) {
            MediaInfo mediaInfo = getMediaInfo();
            if (mediaInfo != null) {
                Bitmap thumb = null;
                Resources res = context.getResources();
                try {
                    thumb = mediaInfo.getThumbnail(context.getContentResolver(), res.getDimensionPixelSize(17104901), res.getDimensionPixelSize(17104902));
                } catch (Exception ex) {
                    Logger.m184w("Failed to load thumbnail: " + ex.getMessage());
                }
                if (thumb == null) {
                    thumb = createDefaultNotificationDrawable(res);
                }
                this.thumbnail = thumb;
            }
            this.thumbnailLoaded = true;
        }
        return this.thumbnail;
    }

    protected Bitmap createDefaultNotificationDrawable(Resources res) {
        return ((BitmapDrawable) res.getDrawable(getDefaultNotificationLargeIconId())).getBitmap();
    }

    protected int getDefaultNotificationLargeIconId() {
        return 2130838251;
    }

    protected String getStatusText(LocalizationManager localizationManager) {
        int statusText = 2131166850;
        switch (C05142.$SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[getState().ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                if (!this.fileUploadCompleted) {
                    statusText = 2131166853;
                    break;
                }
                statusText = 2131166855;
                break;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                statusText = 2131166834;
                break;
            case Message.TYPE_FIELD_NUMBER /*3*/:
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                statusText = getErrorMessageResId();
                break;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                statusText = 2131166851;
                break;
        }
        return localizationManager.getString(statusText);
    }

    protected String getErrorMessage(LocalizationManager localizationManager) {
        VideoUploadException error = (VideoUploadException) getError(VideoUploadException.class);
        if (error != null) {
            String standardMessage = getStandardErrorMessage(error, localizationManager);
            if (standardMessage != null) {
                return standardMessage;
            }
        }
        return getDefaultErrorMessage(localizationManager);
    }

    protected String getStandardErrorMessage(VideoUploadException error, LocalizationManager localizationManager) {
        if (error.getErrorCode() == 1) {
            return localizationManager.getString(2131166851);
        }
        return null;
    }

    protected String getDefaultErrorMessage(LocalizationManager localizationManager) {
        return localizationManager.getString(2131166849);
    }

    private int getErrorMessageResId() {
        return 2131166849;
    }

    protected void onNewParams(PersistentTaskContext persistentContext, Bundle params) {
        Logger.m173d("params: %s", params);
        this.title = params.getString("task_param_title");
        this.isPrivate = params.getBoolean("task_param_privacy");
        if (params.getBoolean("task_param_retry")) {
            resetForRetry();
        }
        persist(persistentContext);
    }

    protected void resetForRetry() {
        this.uploadUrl = null;
        this.videoId = 0;
        this.progress = 0;
        this.uploadUrlCount = 0;
    }

    protected void finish(PersistentTaskContext persistentContext) {
        showCompletedNotification(persistentContext.getContext());
        cleanup(persistentContext);
    }

    protected void showCompletedNotification(Context context) {
        LocalizationManager localizationManager = LocalizationManager.from(context);
        String title = this.title;
        String text = localizationManager.getString(2131166814);
        Resources res = context.getResources();
        Bitmap thumbnail = this.mediaInfo.getThumbnail(context.getContentResolver(), res.getDimensionPixelSize(17104901), res.getDimensionPixelSize(17104902));
        Builder builder = new Builder(context);
        builder.setContentTitle(title);
        builder.setContentText(text);
        builder.setSmallIcon(2130838517);
        if (thumbnail != null) {
            builder.setLargeIcon(thumbnail);
        }
        builder.setContentIntent(createOpenMyVideosIntent(context));
        builder.setAutoCancel(true);
        ((NotificationManager) context.getSystemService("notification")).notify(2131624289, builder.build());
    }

    private static PendingIntent createOpenMyVideosIntent(Context context) {
        Intent openMyVideos = new Intent(context.getApplicationContext(), OdklActivity.class);
        openMyVideos.setAction("ru.ok.android.ui.OdklActivity.SHOW_MY_VIDEOS");
        openMyVideos.putExtra("FORCE_PROCESS_INTENT", true);
        openMyVideos.setFlags(67239936);
        return PendingIntent.getActivity(context.getApplicationContext(), 0, openMyVideos, 0);
    }

    public long getVideoId() {
        return this.videoId;
    }

    public String getTitle() {
        return this.title;
    }

    public MediaInfo getMediaInfo() {
        return this.mediaInfo;
    }

    private static String createFileName(String displayName) {
        if (displayName != null) {
            try {
                return URLEncoder.encode(displayName + ".mp4", StringUtils.UTF8);
            } catch (Exception e) {
            }
        }
        return "video.mp4";
    }

    public String toString() {
        return getClass().getSimpleName() + "[id=" + getId() + " state=" + getState() + " parentId=" + getParentId() + " isPausing=" + isPausing() + " fileUploadCompleted=" + this.fileUploadCompleted + " subTaskIds=" + getSubTaskIds() + " uploadUrl=" + this.uploadUrl + " videoId=" + this.videoId + " progress=" + this.progress + " title=" + this.title + "]";
    }
}
