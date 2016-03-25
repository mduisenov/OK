package ru.ok.android.services.processors.image.upload;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import java.io.File;
import ru.ok.android.http.client.methods.HttpUriRequest;
import ru.ok.android.model.image.ImageEditInfo;
import ru.ok.android.services.persistent.PersistentTask;
import ru.ok.android.services.persistent.PersistentTaskContext;
import ru.ok.android.services.persistent.PersistentTaskNotificationBuilder;
import ru.ok.android.services.persistent.PersistentTaskState;
import ru.ok.android.services.processors.image.upload.ImageUploadMethods.GetUrlResult;
import ru.ok.android.services.processors.photo.upload.ImageUploadException;
import ru.ok.android.services.processors.photo.upload.ImageUploader.PhotoCommitResponse;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.services.transport.JsonTransportProvider;
import ru.ok.android.utils.FileUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.Storage.External.Application;
import ru.ok.java.api.request.image.UploadSingleImageProgressRequest.UploadProgressListener;

public class UploadOneImageTask extends PersistentTask implements UploadProgressListener {
    public static final Creator<UploadOneImageTask> CREATOR;
    private static final long serialVersionUID = 2;
    private String assignedPhotoId;
    private boolean doCleanup;
    private final boolean doCommit;
    private long fileSize;
    private String filename;
    private final String groupId;
    private final ImageEditInfo imageEditInfo;
    private volatile transient boolean isAborting;
    private volatile transient PersistentTaskContext persistentContext;
    private String token;
    private volatile transient UploadImageHandle uploadHandle;
    private String uploadId;
    private String uploadUrl;
    private transient long uploadedSize;

    /* renamed from: ru.ok.android.services.processors.image.upload.UploadOneImageTask.1 */
    static class C04631 implements Creator<UploadOneImageTask> {
        C04631() {
        }

        public UploadOneImageTask createFromParcel(Parcel source) {
            return new UploadOneImageTask(source);
        }

        public UploadOneImageTask[] newArray(int size) {
            return new UploadOneImageTask[size];
        }
    }

    public UploadOneImageTask(String uid, ImageEditInfo imageEditInfo, String groupId, int parentTaskId, boolean doCommit, String filename, long fileSize) {
        super(uid, true, parentTaskId);
        this.doCleanup = true;
        this.imageEditInfo = imageEditInfo;
        this.groupId = groupId;
        this.doCommit = doCommit;
        this.filename = filename;
        this.fileSize = fileSize;
    }

    public String getToken() {
        return this.token;
    }

    public ImageEditInfo getImageEditInfo() {
        return this.imageEditInfo;
    }

    protected void onPausing(PersistentTaskContext persistentContext) {
        abortUpload(persistentContext);
    }

    protected void onCancel(PersistentTaskContext persistentContext) {
        abortUpload(persistentContext);
        cleanUp(persistentContext);
    }

    public PersistentTaskState execute(PersistentTaskContext persistentContext, Context context) throws ImageUploadException {
        Logger.m172d("execute >>>");
        this.persistentContext = persistentContext;
        this.isAborting = false;
        try {
            if (this.uploadUrl == null || this.uploadId == null) {
                Logger.m172d("execute: getting url...");
                getUrl(persistentContext);
            }
            checkThrowCanceled(3);
            if (this.filename == null) {
                Logger.m172d("execute: preparing...");
                prepareImage(persistentContext, context, this.imageEditInfo);
            }
            checkThrowCanceled(2);
            if (this.token == null) {
                Logger.m172d("execute: uploading...");
                uploadFile(persistentContext);
            }
            checkThrowCanceled(4);
            if (this.doCommit && this.assignedPhotoId == null) {
                Logger.m172d("execute: performing commit...");
                commit(persistentContext);
            }
            cleanUp(persistentContext);
            PersistentTaskState persistentTaskState = PersistentTaskState.COMPLETED;
            return persistentTaskState;
        } finally {
            this.persistentContext = null;
        }
    }

    private void checkThrowCanceled(int phase) throws ImageUploadException {
        if (this.isAborting) {
            throw new ImageUploadException(phase, 3);
        }
    }

    private void prepareImage(PersistentTaskContext persistentTaskContext, Context context, ImageEditInfo imageEditInfo) throws ImageUploadException {
        File destFile = getTempFile(context, imageEditInfo);
        Logger.m173d("encode image to temp file: %s", destFile);
        if (destFile == null || !"mounted".equals(Environment.getExternalStorageState())) {
            throw new ImageUploadException(1, 2);
        }
        ImageUploadMethods.prepareImageToFile(context, imageEditInfo, destFile);
        this.fileSize = destFile.length();
        if (this.fileSize <= 0) {
            throw new ImageUploadException(1, 15);
        }
        this.filename = destFile.getAbsolutePath();
        persist(persistentTaskContext);
    }

    private void getUrl(PersistentTaskContext persistentContext) throws ImageUploadException {
        GetUrlResult result;
        JsonSessionTransportProvider jsonSessionTransportProvider = JsonSessionTransportProvider.getInstance();
        if (this.groupId == null) {
            result = ImageUploadMethods.getUrlWithMaxQualitySettings(this.imageEditInfo.getAlbumInfo(), jsonSessionTransportProvider);
        } else {
            result = ImageUploadMethods.getUrlWithMaxQualitySettings(this.groupId, jsonSessionTransportProvider);
        }
        this.uploadUrl = result.uploadUrl;
        this.uploadId = result.uploadId;
        persist(persistentContext);
    }

    private void uploadFile(PersistentTaskContext persistentContext) throws ImageUploadException {
        File imageFile = new File(this.filename);
        try {
            this.uploadHandle = ImageUploadMethods.createUploadImageRequest(this.uploadUrl, imageFile, this.uploadId, (UploadProgressListener) this);
            this.token = ImageUploadMethods.uploadImage(this.uploadHandle, JsonTransportProvider.getInstance(persistentContext.getContext()));
            persist(persistentContext);
        } catch (Throwable e) {
            if (this.isAborting) {
                Logger.m185w("Upload was intentionally aborted: %s", e);
                throw new ImageUploadException(3, 3);
            }
            Throwable iue;
            if (e instanceof ImageUploadException) {
                iue = (ImageUploadException) e;
            } else {
                iue = ImageUploadMethods.convertException(e, 3, null, imageFile, null);
            }
            Logger.m177e("image upload failed: %s", iue);
            Logger.m178e(iue);
            throw iue;
        }
    }

    private void commit(PersistentTaskContext persistentContext) throws ImageUploadException {
        PhotoCommitResponse response = ImageUploadMethods.commit(this.uploadId, this.token, this.imageEditInfo.getComment(), JsonSessionTransportProvider.getInstance());
        this.assignedPhotoId = response == null ? null : response.assignedPhotoId;
        persist(persistentContext);
    }

    protected PendingIntent getTaskDetailsIntent(PersistentTaskContext persistentContext) {
        return null;
    }

    public void createNotification(PersistentTaskContext persistentContext, PersistentTaskNotificationBuilder notificationBulder) {
    }

    public long getUploadedSize() {
        return this.uploadedSize;
    }

    public void onUploadProgress(long uploadedSize, long totalSize) {
        Logger.m172d("onUploadProgrees: " + uploadedSize + " / " + totalSize + " bytes");
        long oldProgressPercents5 = (this.uploadedSize * 20) / totalSize;
        long newProgressPercents5 = (uploadedSize * 20) / totalSize;
        this.uploadedSize = uploadedSize;
        if (this.persistentContext != null && oldProgressPercents5 != newProgressPercents5) {
            this.persistentContext.notifyOnChanged(this);
        }
    }

    public void setDoCleanup(boolean doCleanup) {
        this.doCleanup = doCleanup;
    }

    public String toString() {
        return "UploadOneImageTask[id=" + getId() + " state=" + getState() + " isCanceled=" + isCanceled() + " isPausing=" + isPausing() + " parentId=" + getParentId() + " filename=" + this.filename + " uploadUrl=" + this.uploadUrl + " uploadId=" + this.uploadId + " token=" + this.token + " assignedPhotoId=" + this.assignedPhotoId + " doCommit=" + this.doCommit + " groupId=" + this.groupId + "]";
    }

    private File getTempFile(Context context, ImageEditInfo imageEditInfo) {
        try {
            String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(imageEditInfo.getMimeType());
            return FileUtils.generateEmptyFile(Application.getCacheDir(context), ext != null ? "." + ext : "");
        } catch (Throwable e) {
            Logger.m184w("Failed to create empty file: " + e);
            Logger.m178e(e);
            return null;
        }
    }

    private void abortUpload(PersistentTaskContext persistentContext) {
        Logger.m172d("abortUpload");
        this.isAborting = true;
        UploadImageHandle handle = this.uploadHandle;
        HttpUriRequest httpRequest = handle == null ? null : handle.httpRequest;
        if (httpRequest != null) {
            Logger.m172d("Aborting upload httpRequest...");
            httpRequest.abort();
            return;
        }
        Logger.m172d("No ongoing upload is being executed, do nothing");
    }

    private void cleanUp(PersistentTaskContext persistentContext) {
        if (this.doCleanup) {
            Logger.m172d("performing cleanup");
            deleteTempEncodedFile(persistentContext);
            return;
        }
        Logger.m172d("skipping cleanup");
    }

    private void deleteTempEncodedFile(PersistentTaskContext persistentContext) {
        if (!TextUtils.isEmpty(this.filename)) {
            File tempFile = new File(this.filename);
            if (tempFile.exists()) {
                Logger.m173d("Deleting temp file: %s...", this.filename);
                try {
                    if (tempFile.delete()) {
                        Logger.m173d("Temp file deleted Ok: %s", this.filename);
                        return;
                    }
                    Logger.m185w("Failed to delete temp file: %s", this.filename);
                } catch (Throwable e) {
                    Logger.m177e("Error occurred while deleting temp file: %s", this.filename);
                    Logger.m178e(e);
                }
            }
        }
    }

    public void writeToParcel(Parcel dest, int flags) {
        int i;
        int i2 = 1;
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.imageEditInfo, flags);
        dest.writeString(this.filename);
        dest.writeString(this.uploadUrl);
        dest.writeString(this.uploadId);
        dest.writeString(this.token);
        dest.writeString(this.assignedPhotoId);
        if (this.doCommit) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeInt(i);
        dest.writeString(this.groupId);
        if (!this.doCleanup) {
            i2 = 0;
        }
        dest.writeInt(i2);
    }

    protected UploadOneImageTask(Parcel src) {
        boolean z;
        boolean z2 = true;
        super(src);
        this.doCleanup = true;
        this.imageEditInfo = (ImageEditInfo) src.readParcelable(UploadOneImageTask.class.getClassLoader());
        this.filename = src.readString();
        this.uploadUrl = src.readString();
        this.uploadId = src.readString();
        this.token = src.readString();
        this.assignedPhotoId = src.readString();
        if (src.readInt() != 0) {
            z = true;
        } else {
            z = false;
        }
        this.doCommit = z;
        this.groupId = src.readString();
        if (src.readInt() == 0) {
            z2 = false;
        }
        this.doCleanup = z2;
    }

    static {
        CREATOR = new C04631();
    }

    public PersistentTask copy() {
        Parcel parcel = toParcel();
        PersistentTask copy = new UploadOneImageTask(parcel);
        parcel.recycle();
        return copy;
    }
}
