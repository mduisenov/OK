package ru.ok.android.services.processors.image.upload;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import ru.ok.android.C0206R;
import ru.ok.android.model.image.ImageEditInfo;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.persistent.BaseParentPersistentTask;
import ru.ok.android.services.persistent.PersistentTask;
import ru.ok.android.services.persistent.PersistentTaskContext;
import ru.ok.android.services.persistent.PersistentTaskNotificationBuilder;
import ru.ok.android.services.persistent.PersistentTaskState;
import ru.ok.android.services.processors.image.upload.UploadImagesState.UploadImagesPhase;
import ru.ok.android.services.processors.photo.upload.ImageUploadException;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NotificationsUtils;
import ru.ok.android.utils.localization.LocalizationManager;

public class UploadImagesTask extends BaseParentPersistentTask {
    public static final Creator<UploadImagesTask> CREATOR;
    private static final long serialVersionUID = 1;
    private int completedPrepareImageCount;
    private int completedUploadImageCount;
    private final boolean doCommit;
    private final String groupId;
    private boolean ignoreErrors;
    protected HashMap<ImageEditInfo, ImageState> imageStates;
    private final ArrayList<ImageEditInfo> images;
    private transient int totalUploadSizeBytes;
    private final UploadImagesState uploadState;
    private transient int uploadedSizeBytes;

    /* renamed from: ru.ok.android.services.processors.image.upload.UploadImagesTask.1 */
    static class C04601 implements Creator<UploadImagesTask> {
        C04601() {
        }

        public UploadImagesTask createFromParcel(Parcel source) {
            return new UploadImagesTask(source);
        }

        public UploadImagesTask[] newArray(int size) {
            return new UploadImagesTask[size];
        }
    }

    /* renamed from: ru.ok.android.services.processors.image.upload.UploadImagesTask.2 */
    static /* synthetic */ class C04612 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState;

        static {
            $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState = new int[PersistentTaskState.values().length];
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.SUBMITTED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.EXECUTING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.COMPLETED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.PAUSED.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.ERROR.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.FAILED.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.WAIT_INTERNET.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    public static class ImageState implements Parcelable, Serializable {
        public static final Creator<ImageState> CREATOR;
        private static final long serialVersionUID = 1;
        String filename;
        long filesize;
        int prepareSubTaskId;
        String token;
        int uploadSubTaskId;
        long uploadedSize;

        /* renamed from: ru.ok.android.services.processors.image.upload.UploadImagesTask.ImageState.1 */
        static class C04621 implements Creator<ImageState> {
            C04621() {
            }

            public ImageState createFromParcel(Parcel source) {
                return new ImageState(null);
            }

            public ImageState[] newArray(int size) {
                return new ImageState[size];
            }
        }

        ImageState() {
        }

        public String getToken() {
            return this.token;
        }

        public String toString() {
            return "ImageState[filename=" + this.filename + " filesize=" + this.filesize + " uploadedSize=" + this.uploadedSize + " token=" + this.token + " prepareSubTaskId=" + this.prepareSubTaskId + " uploadSubTaskId=" + this.uploadSubTaskId + "]";
        }

        public ImageState copy() {
            ImageState copy = new ImageState();
            copy.filename = this.filename;
            copy.filesize = this.filesize;
            copy.uploadedSize = this.uploadedSize;
            copy.token = this.token;
            return copy;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.filename);
            dest.writeLong(this.filesize);
            dest.writeLong(this.uploadedSize);
            dest.writeString(this.token);
            dest.writeInt(this.prepareSubTaskId);
            dest.writeInt(this.uploadSubTaskId);
        }

        private ImageState(Parcel src) {
            this.filename = src.readString();
            this.filesize = src.readLong();
            this.uploadedSize = src.readLong();
            this.token = src.readString();
            this.prepareSubTaskId = src.readInt();
            this.uploadSubTaskId = src.readInt();
        }

        static {
            CREATOR = new C04621();
        }
    }

    public UploadImagesTask(String uid, boolean isHidden, int parentTaskId, ArrayList<ImageEditInfo> images, boolean doCommit, String groupId, boolean ignoreErrors) {
        super(uid, isHidden, parentTaskId);
        this.images = new ArrayList(images);
        int size = images.size();
        this.doCommit = doCommit;
        this.groupId = groupId;
        this.uploadState = new UploadImagesState(size);
        this.imageStates = new HashMap();
        Iterator i$ = images.iterator();
        while (i$.hasNext()) {
            this.imageStates.put((ImageEditInfo) i$.next(), new ImageState());
        }
        this.ignoreErrors = ignoreErrors;
    }

    public UploadImagesTask(String uid, boolean isHidden, int parentTaskId, ArrayList<ImageEditInfo> images, boolean doCommit, String groupId) {
        super(uid, isHidden, parentTaskId);
        this.images = new ArrayList(images);
        int size = images.size();
        this.doCommit = doCommit;
        this.groupId = groupId;
        this.uploadState = new UploadImagesState(size);
        this.imageStates = new HashMap();
        Iterator i$ = images.iterator();
        while (i$.hasNext()) {
            this.imageStates.put((ImageEditInfo) i$.next(), new ImageState());
        }
    }

    public ArrayList<ImageEditInfo> getImages() {
        return this.images;
    }

    public ImageState getImageState(ImageEditInfo ImageEditInfo) {
        return (ImageState) this.imageStates.get(ImageEditInfo);
    }

    public void presetImageState(ImageEditInfo ImageEditInfo, ImageState imageState) {
        if (getState() == PersistentTaskState.SUBMITTED) {
            if (imageState == null) {
                imageState = new ImageState();
            }
            this.imageStates.put(ImageEditInfo, imageState);
        }
    }

    public void takeoverImage(PersistentTaskContext persistentContext, ImageEditInfo image) {
        if (image != null) {
            Logger.m173d(">>> image=%s", image);
            for (Integer intValue : getSubTaskIds()) {
                PersistentTask subTask = persistentContext.getTask(intValue.intValue());
                if (subTask instanceof UploadOneImageTask) {
                    UploadOneImageTask uploadOneImageTask = (UploadOneImageTask) subTask;
                    if (image.equals(uploadOneImageTask.getImageEditInfo())) {
                        Logger.m173d("<<< cancelling cleanup for image: %s", image);
                        uploadOneImageTask.setDoCleanup(false);
                        persistentContext.save(uploadOneImageTask);
                        return;
                    }
                }
            }
            Logger.m173d("<<< did not cancel cleanup for image: %s", image);
        }
    }

    public UploadImagesState getUploadState() {
        return this.uploadState;
    }

    public PersistentTaskState execute(PersistentTaskContext persistentContext, Context context) throws ImageUploadException {
        this.completedPrepareImageCount = 0;
        long totalSize = 0;
        Iterator i$ = this.images.iterator();
        while (i$.hasNext()) {
            ImageEditInfo image = (ImageEditInfo) i$.next();
            ImageState imageState = (ImageState) this.imageStates.get(image);
            boolean prepared = (imageState.filename == null || imageState.filesize == 0) ? false : true;
            boolean taskFinishedWithError = isInErrorState(persistentContext, imageState);
            if (prepared || taskFinishedWithError) {
                if (!taskFinishedWithError || (taskFinishedWithError && this.ignoreErrors)) {
                    totalSize += imageState.filesize;
                    this.completedPrepareImageCount++;
                }
            } else if (imageState.prepareSubTaskId == 0) {
                imageState.prepareSubTaskId = submitSubTask(persistentContext, new PrepareImageTask(getUid(), getId(), image));
            }
        }
        boolean isPreparing = this.completedPrepareImageCount != this.images.size();
        if (isPreparing) {
            this.uploadState.preparing();
        }
        persist(persistentContext);
        if (isPreparing) {
            return PersistentTaskState.EXECUTING;
        }
        this.completedUploadImageCount = 0;
        i$ = this.images.iterator();
        while (i$.hasNext()) {
            image = (ImageEditInfo) i$.next();
            imageState = (ImageState) this.imageStates.get(image);
            boolean isInErrorState = isInErrorState(persistentContext, imageState);
            if (imageState.token != null || isInErrorState) {
                this.completedUploadImageCount++;
            } else if (imageState.uploadSubTaskId == 0) {
                imageState.uploadSubTaskId = submitSubTask(persistentContext, new UploadOneImageTask(getUid(), image, this.groupId, getId(), this.doCommit, imageState.filename, imageState.filesize));
            }
        }
        boolean isUploading = this.completedUploadImageCount < this.images.size();
        if (isUploading) {
            this.uploadState.uploading(totalSize);
        }
        persist(persistentContext);
        if (isUploading) {
            return PersistentTaskState.EXECUTING;
        }
        this.uploadState.complete();
        return PersistentTaskState.COMPLETED;
    }

    protected boolean isInErrorState(PersistentTaskContext context, ImageState imageState) {
        boolean prepared;
        PrepareImageTask prepareTask = null;
        if (imageState.prepareSubTaskId != 0) {
            prepareTask = (PrepareImageTask) getSubTask(context, imageState.prepareSubTaskId);
        }
        if (imageState.filename == null || imageState.filesize == 0) {
            prepared = false;
        } else {
            prepared = true;
        }
        if (prepared || prepareTask == null || (prepareTask.getState() != PersistentTaskState.COMPLETED && prepareTask.getState() != PersistentTaskState.FAILED && prepareTask.getState() != PersistentTaskState.ERROR)) {
            return false;
        }
        return true;
    }

    protected PendingIntent getTaskDetailsIntent(PersistentTaskContext persistentContext) {
        return null;
    }

    protected void onSubTaskCompleted(PersistentTaskContext persistentContext, PersistentTask subTask) {
        Logger.m173d(" id=%d subTaskId=%d", Integer.valueOf(getId()), Integer.valueOf(subTask.getId()));
        ImageState imageState;
        if (subTask instanceof PrepareImageTask) {
            PrepareImageTask prepareImageTask = (PrepareImageTask) subTask;
            imageState = (ImageState) this.imageStates.get(prepareImageTask.getImageEditInfo());
            if (imageState == null) {
                Logger.m176e("sub-task not found: " + subTask);
            } else if (imageState.filename == null || imageState.filesize == 0) {
                imageState.filename = prepareImageTask.getFilename();
                imageState.filesize = prepareImageTask.getFileSize();
                Logger.m173d("completedPrepareImageCount = %d (%d)", Integer.valueOf(this.completedPrepareImageCount), Integer.valueOf(this.images.size()));
            }
        } else if (subTask instanceof UploadOneImageTask) {
            UploadOneImageTask uploadTask = (UploadOneImageTask) subTask;
            imageState = (ImageState) this.imageStates.get(uploadTask.getImageEditInfo());
            if (imageState == null) {
                Logger.m176e("sub-task not found: " + subTask);
            } else if (imageState.token == null) {
                imageState.token = uploadTask.getToken();
                Logger.m173d("completedUploadImageCount = %d (%d)", Integer.valueOf(this.completedPrepareImageCount), Integer.valueOf(this.images.size()));
            }
        }
        recalculateProgress(persistentContext);
        this.uploadState.updateProgress((long) this.uploadedSizeBytes, this.completedPrepareImageCount, this.completedUploadImageCount);
        persist(persistentContext);
    }

    protected void persist(PersistentTaskContext persistentContext) {
        super.persist(persistentContext);
        Logger.m172d("" + this.uploadState);
    }

    protected void onSubTaskStateChanged(PersistentTaskContext persistentContext, PersistentTask subTask) {
        Logger.m173d(" id=%d subTaskId=%d", Integer.valueOf(getId()), Integer.valueOf(subTask.getId()));
        this.uploadState.onSubTaskStateChanged(subTask);
        if (subTask instanceof UploadOneImageTask) {
            UploadOneImageTask uploadTask = (UploadOneImageTask) subTask;
            ImageState imageState = (ImageState) this.imageStates.get(uploadTask.getImageEditInfo());
            if (imageState == null) {
                Logger.m177e("image not found: %s", ImageEditInfo);
            } else {
                imageState.uploadedSize = uploadTask.getUploadedSize();
            }
        }
        recalculateProgress(persistentContext);
        this.uploadState.updateProgress((long) this.uploadedSizeBytes, this.completedPrepareImageCount, this.completedUploadImageCount);
        persist(persistentContext);
        persistentContext.notifyOnChanged(this);
    }

    protected void onStateChanged(PersistentTaskContext persistentContext) {
        this.uploadState.onStateChanged(getState());
    }

    private void recalculateProgress(PersistentTaskContext persistentContext) {
        long totalFileSize = 0;
        long uploadedSize = 0;
        int preparedImages = 0;
        int uploadedImages = 0;
        Iterator i$ = this.images.iterator();
        while (i$.hasNext()) {
            ImageState imageState = (ImageState) this.imageStates.get((ImageEditInfo) i$.next());
            totalFileSize += imageState.filesize;
            uploadedSize += imageState.uploadedSize;
            if (imageState.token != null) {
                uploadedImages++;
            }
            if (!(imageState.filesize == 0 || imageState.filename == null)) {
                preparedImages++;
            }
        }
        this.totalUploadSizeBytes = (int) totalFileSize;
        this.uploadedSizeBytes = (int) uploadedSize;
        this.completedPrepareImageCount = preparedImages;
        this.completedUploadImageCount = uploadedImages;
    }

    public void createNotification(PersistentTaskContext persistentContext, PersistentTask activeSubTask, PersistentTaskNotificationBuilder notificationBuilder) {
        Context context = persistentContext.getContext();
        LocalizationManager localizationManager = LocalizationManager.from(context);
        if (activeSubTask == null) {
            switch (C04612.$SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[getState().ordinal()]) {
                case Message.TEXT_FIELD_NUMBER /*1*/:
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    createProgressNotification(context, localizationManager, null, notificationBuilder);
                case Message.TYPE_FIELD_NUMBER /*3*/:
                    createCompletedNotification(localizationManager, notificationBuilder);
                case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                    createPausedNotification(localizationManager, notificationBuilder);
                default:
                    createErrorNotification(localizationManager, notificationBuilder);
            }
        } else if (activeSubTask instanceof PrepareImageTask) {
            createPrepareNotification(context, localizationManager, notificationBuilder, (PrepareImageTask) activeSubTask);
        } else if (activeSubTask instanceof UploadOneImageTask) {
            createUploadNotification(context, localizationManager, notificationBuilder, (UploadOneImageTask) activeSubTask);
        }
    }

    private void createProgressNotification(Context context, LocalizationManager localizationManager, PersistentTask activeSubTask, PersistentTaskNotificationBuilder notificationBulder) {
        String title;
        String text;
        if (this.completedPrepareImageCount == this.images.size()) {
            title = localizationManager.getString(2131166762);
            text = String.format(localizationManager.getString(2131166780), new Object[]{Integer.valueOf(this.uploadState.getUploadedPhotos()), Integer.valueOf(this.uploadState.getPhotoCount())});
            notificationBulder.setProgress(this.uploadedSizeBytes, this.totalUploadSizeBytes);
            Logger.m173d("progress = %d/%d images, %d/%d bytes", Integer.valueOf(this.uploadState.getUploadedPhotos()), Integer.valueOf(this.uploadState.getPhotoCount()), Integer.valueOf(this.uploadedSizeBytes), Integer.valueOf(this.totalUploadSizeBytes));
            if (activeSubTask instanceof UploadOneImageTask) {
                ImageEditInfo imageEditInfo = ((UploadOneImageTask) activeSubTask).getImageEditInfo();
                Bitmap thumb = NotificationsUtils.createThumbnailForLargeIcon(context, imageEditInfo.getUri(), imageEditInfo.getRotation());
                if (thumb != null) {
                    notificationBulder.setLargeIcon(thumb);
                }
            }
        } else {
            title = localizationManager.getString(2131166394);
            text = String.format(localizationManager.getString(2131166395), new Object[]{Integer.valueOf(this.uploadState.getPreparedPhotos()), Integer.valueOf(this.uploadState.getPhotoCount())});
            notificationBulder.setIndeterminateProgress();
        }
        notificationBulder.setText(text);
        notificationBulder.setTitle(title);
        notificationBulder.setSmallIcon(2130838515);
    }

    private void createCompletedNotification(LocalizationManager localizationManager, PersistentTaskNotificationBuilder notificationBulder) {
        notificationBulder.setText(String.format(localizationManager.getString(2131166761), new Object[]{Integer.valueOf(this.images.size())}));
        notificationBulder.setTitle(localizationManager.getString(2131166764));
        notificationBulder.setSmallIcon(2130838517);
    }

    private void createErrorNotification(LocalizationManager localizationManager, PersistentTaskNotificationBuilder notificationBulder) {
        notificationBulder.setTitle(localizationManager.getString(2131166767));
        notificationBulder.setText(localizationManager.getString(2131166766));
        notificationBulder.setSmallIcon(2130838516);
    }

    private void createPrepareNotification(Context context, LocalizationManager localizationManager, PersistentTaskNotificationBuilder notificationBulder, PrepareImageTask activeSubTask) {
        String preparingPhotosTitle = localizationManager.getString(2131166394);
        switch (C04612.$SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[activeSubTask.getState().ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                createProgressNotification(context, localizationManager, activeSubTask, notificationBulder);
                if (!isPausing()) {
                    notificationBulder.addPauseAction(context, localizationManager, this);
                }
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                createPausedNotification(localizationManager, notificationBulder);
                notificationBulder.addResumeAction(context, localizationManager, this);
            case Message.UUID_FIELD_NUMBER /*5*/:
                createFailedNotification(localizationManager, notificationBulder, preparingPhotosTitle, null);
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                createFailedNotification(localizationManager, notificationBulder, preparingPhotosTitle, (ImageUploadException) activeSubTask.getError(ImageUploadException.class));
            default:
        }
    }

    private void createUploadNotification(Context context, LocalizationManager localizationManager, PersistentTaskNotificationBuilder notificationBulder, UploadOneImageTask activeSubTask) {
        String title = localizationManager.getString(2131166762);
        switch (C04612.$SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[activeSubTask.getState().ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                createProgressNotification(context, localizationManager, activeSubTask, notificationBulder);
                if (!isPausing()) {
                    notificationBulder.addPauseAction(context, localizationManager, this);
                }
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                createPausedNotification(localizationManager, notificationBulder);
                notificationBulder.addResumeAction(context, localizationManager, this);
            case Message.UUID_FIELD_NUMBER /*5*/:
                createFailedNotification(localizationManager, notificationBulder, title, null);
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                createFailedNotification(localizationManager, notificationBulder, title, (ImageUploadException) activeSubTask.getError(ImageUploadException.class));
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
                createWaitInternetNotification(localizationManager, notificationBulder, title);
            default:
        }
    }

    private void createWaitInternetNotification(LocalizationManager localizationManager, PersistentTaskNotificationBuilder notificationBuilder, String title) {
        notificationBuilder.setTitle(title);
        notificationBuilder.setText(localizationManager.getString(2131166773));
        notificationBuilder.setSmallIcon(2130838516);
    }

    private void createFailedNotification(LocalizationManager localizationManager, PersistentTaskNotificationBuilder notificationBuilder, String title, ImageUploadException reason) {
        notificationBuilder.setTitle(title);
        switch (reason == null ? 0 : reason.getErrorCode()) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                notificationBuilder.setText(localizationManager.getString(2131166773));
                break;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                notificationBuilder.setText(localizationManager.getString(2131166771));
                break;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                switch (reason.getServerErrorCode()) {
                    case Message.ATTACHES_FIELD_NUMBER /*7*/:
                        notificationBuilder.setText(localizationManager.getString(2131166763));
                        break;
                    case 500:
                        notificationBuilder.setText(localizationManager.getString(2131166770));
                        break;
                    case 501:
                        notificationBuilder.setText(localizationManager.getString(2131166779));
                        break;
                    case 502:
                        notificationBuilder.setText(localizationManager.getString(2131166778));
                        break;
                    case 503:
                        notificationBuilder.setText(localizationManager.getString(2131166769));
                        break;
                    case 504:
                        notificationBuilder.setText(localizationManager.getString(2131166765));
                        break;
                    case 505:
                        notificationBuilder.setText(localizationManager.getString(2131166772));
                        break;
                    default:
                        notificationBuilder.setText(localizationManager.getString(2131166776));
                        break;
                }
            case C0206R.styleable.Toolbar_titleMarginEnd /*14*/:
                notificationBuilder.setText(localizationManager.getString(2131166777));
                break;
            case C0206R.styleable.Toolbar_titleMarginTop /*15*/:
                notificationBuilder.setText(localizationManager.getString(2131166768));
                break;
            case C0206R.styleable.Toolbar_titleMarginBottom /*16*/:
                notificationBuilder.setText(localizationManager.getString(2131166774));
                break;
            default:
                notificationBuilder.setText(localizationManager.getString(2131166766));
                break;
        }
        notificationBuilder.setSmallIcon(2130838516);
    }

    private void createPausedNotification(LocalizationManager localizationManager, PersistentTaskNotificationBuilder notificationBulder) {
        int progress;
        notificationBulder.setTitle(localizationManager.getString(2131166762));
        UploadImagesState uploadState = getUploadState();
        int maxProgress = uploadState.getPhotoCount();
        if (uploadState.getPhase() == UploadImagesPhase.UPLOAD) {
            progress = uploadState.getUploadedPhotos();
        } else {
            progress = uploadState.getPreparedPhotos();
        }
        notificationBulder.setText(String.format(localizationManager.getString(2131166775), new Object[]{Integer.valueOf(progress), Integer.valueOf(maxProgress)}));
        notificationBulder.setSmallIcon(2130838511);
    }

    public String toString() {
        return "UploadImagesTask[id=" + getId() + " state=" + getState() + " isPausing=" + isPausing() + " parentId=" + getParentId() + " subTaskIds=" + getSubTaskIds() + " imagesCount=" + this.images.size() + " completedPrepareImageCount=" + this.completedPrepareImageCount + " completedUploadImageCount=" + this.completedUploadImageCount + " imageStates=" + this.imageStates + " doCommit=" + this.doCommit + " groupId=" + this.groupId + " uploadState=" + getUploadState() + "]";
    }

    protected UploadImagesTask(Parcel src) {
        boolean z = true;
        super(src);
        ClassLoader cl = UploadImagesTask.class.getClassLoader();
        this.images = src.readArrayList(cl);
        this.imageStates = src.readHashMap(cl);
        this.completedUploadImageCount = src.readInt();
        this.doCommit = src.readInt() != 0;
        this.groupId = src.readString();
        this.completedPrepareImageCount = src.readInt();
        this.uploadState = (UploadImagesState) src.readParcelable(cl);
        if (src.readInt() == 0) {
            z = false;
        }
        this.ignoreErrors = z;
    }

    public void writeToParcel(Parcel dest, int flags) {
        int i = 1;
        super.writeToParcel(dest, flags);
        dest.writeList(this.images);
        dest.writeMap(this.imageStates);
        dest.writeInt(this.completedUploadImageCount);
        dest.writeInt(this.doCommit ? 1 : 0);
        dest.writeString(this.groupId);
        dest.writeInt(this.completedPrepareImageCount);
        dest.writeParcelable(this.uploadState, flags);
        if (!this.ignoreErrors) {
            i = 0;
        }
        dest.writeInt(i);
    }

    static {
        CREATOR = new C04601();
    }

    public PersistentTask copy() {
        Parcel parcel = toParcel();
        PersistentTask copy = new UploadImagesTask(parcel);
        parcel.recycle();
        return copy;
    }
}
