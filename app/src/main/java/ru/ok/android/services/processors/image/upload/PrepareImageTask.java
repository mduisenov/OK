package ru.ok.android.services.processors.image.upload;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.webkit.MimeTypeMap;
import java.io.File;
import ru.ok.android.model.image.ImageEditInfo;
import ru.ok.android.services.persistent.PersistentTask;
import ru.ok.android.services.persistent.PersistentTaskContext;
import ru.ok.android.services.persistent.PersistentTaskNotificationBuilder;
import ru.ok.android.services.persistent.PersistentTaskState;
import ru.ok.android.services.persistent.PersistentTaskUtils;
import ru.ok.android.services.processors.photo.upload.ImageUploadException;
import ru.ok.android.utils.FileUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.Storage.External.Application;

public class PrepareImageTask extends PersistentTask {
    public static final Creator<PrepareImageTask> CREATOR;
    private static final long serialVersionUID = 1;
    private long fileSize;
    private String filename;
    private final ImageEditInfo imageEditInfo;
    private boolean isTempFile;
    private transient File tmpFile;

    /* renamed from: ru.ok.android.services.processors.image.upload.PrepareImageTask.1 */
    static class C04571 implements Creator<PrepareImageTask> {
        C04571() {
        }

        public PrepareImageTask createFromParcel(Parcel source) {
            return new PrepareImageTask(source);
        }

        public PrepareImageTask[] newArray(int size) {
            return new PrepareImageTask[size];
        }
    }

    public PrepareImageTask(String uid, int parentTaskId, ImageEditInfo imageEditInfo) {
        super(uid, true, parentTaskId);
        this.filename = null;
        this.isTempFile = false;
        this.imageEditInfo = imageEditInfo;
    }

    private void initTmpFileOrThrow(Context context) throws ImageUploadException {
        boolean checkCreateFile;
        boolean hasExternalStorage = PersistentTaskUtils.checkHasExternalStorage(context);
        if (hasExternalStorage) {
            File tempFile = getTempFile(context);
            this.tmpFile = tempFile;
            if (tempFile != null) {
                checkCreateFile = true;
                if (!hasExternalStorage) {
                    throw new ImageUploadException(1, 2);
                } else if (!checkCreateFile) {
                    throw new ImageUploadException(1, 15);
                }
            }
        }
        checkCreateFile = false;
        if (!hasExternalStorage) {
            throw new ImageUploadException(1, 2);
        } else if (!checkCreateFile) {
            throw new ImageUploadException(1, 15);
        }
    }

    public PersistentTaskState execute(PersistentTaskContext persistentContext, Context context) throws ImageUploadException {
        try {
            initTmpFileOrThrow(context);
            ImageUploadMethods.prepareImageToFile(context, this.imageEditInfo, this.tmpFile);
            this.isTempFile = true;
            this.filename = this.tmpFile.getAbsolutePath();
            this.fileSize = this.tmpFile.length();
            if (this.fileSize > 0) {
                return PersistentTaskState.COMPLETED;
            }
            throw new ImageUploadException(1, 15);
        } catch (Throwable exc) {
            Logger.m176e("Failed to save image: " + exc);
            Logger.m178e(exc);
            throw exc;
        }
    }

    public ImageEditInfo getImageEditInfo() {
        return this.imageEditInfo;
    }

    public String getFilename() {
        return this.filename;
    }

    public long getFileSize() {
        return this.fileSize;
    }

    protected PendingIntent getTaskDetailsIntent(PersistentTaskContext persistentContext) {
        return null;
    }

    public void createNotification(PersistentTaskContext persistentContext, PersistentTaskNotificationBuilder notificationBulder) {
    }

    private File getTempFile(Context context) {
        try {
            String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(this.imageEditInfo.getMimeType());
            return FileUtils.generateEmptyFile(Application.getCacheDir(context), ext != null ? "." + ext : "");
        } catch (Throwable e) {
            Logger.m184w("Failed to create empty file: " + e);
            Logger.m178e(e);
            return null;
        }
    }

    public String toString() {
        return "PrepareImageTask[id=" + getId() + " state=" + getState() + " isCanceled=" + isCanceled() + " isPausing=" + isPausing() + " parentId=" + getParentId() + " uri=" + (this.imageEditInfo == null ? null : this.imageEditInfo.getUri()) + " filename=" + this.filename + " isTempFile=" + this.isTempFile + " fileSize=" + this.fileSize + " tmpFile=" + this.tmpFile + "]";
    }

    PrepareImageTask(Parcel src) {
        boolean z;
        super(src);
        this.filename = null;
        this.isTempFile = false;
        this.imageEditInfo = (ImageEditInfo) src.readParcelable(PrepareImageTask.class.getClassLoader());
        this.filename = src.readString();
        if (src.readInt() != 0) {
            z = true;
        } else {
            z = false;
        }
        this.isTempFile = z;
        this.fileSize = src.readLong();
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.imageEditInfo, flags);
        dest.writeString(this.filename);
        dest.writeInt(this.isTempFile ? 1 : 0);
        dest.writeLong(this.fileSize);
    }

    static {
        CREATOR = new C04571();
    }

    public PersistentTask copy() {
        Parcel parcel = toParcel();
        PersistentTask copy = new PrepareImageTask(parcel);
        parcel.recycle();
        return copy;
    }
}
