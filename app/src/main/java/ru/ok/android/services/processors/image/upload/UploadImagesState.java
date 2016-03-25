package ru.ok.android.services.processors.image.upload;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.persistent.PersistentInstanceState;
import ru.ok.android.services.persistent.PersistentTask;
import ru.ok.android.services.persistent.PersistentTaskState;
import ru.ok.android.services.processors.photo.upload.ImageUploadException;

public final class UploadImagesState extends PersistentInstanceState<ImageUploadException> {
    public static final Creator<UploadImagesState> CREATOR;
    private static final long serialVersionUID = 1;
    private UploadImagesPhase phase;
    private final int photoCount;
    private int preparedPhotos;
    private long totalUploadSize;
    private int uploadedPhotos;
    private long uploadedSize;

    /* renamed from: ru.ok.android.services.processors.image.upload.UploadImagesState.1 */
    static class C04581 implements Creator<UploadImagesState> {
        C04581() {
        }

        public UploadImagesState createFromParcel(Parcel source) {
            return new UploadImagesState(source);
        }

        public UploadImagesState[] newArray(int size) {
            return new UploadImagesState[size];
        }
    }

    /* renamed from: ru.ok.android.services.processors.image.upload.UploadImagesState.2 */
    static /* synthetic */ class C04592 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState;

        static {
            $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState = new int[PersistentTaskState.values().length];
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.EXECUTING.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.SUBMITTED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.FAILED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.ERROR.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[PersistentTaskState.PAUSED.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    public enum UploadImagesPhase {
        STARTING,
        PREPARE,
        UPLOAD,
        COMPLETED
    }

    public UploadImagesState(int photoCount) {
        this.phase = UploadImagesPhase.STARTING;
        this.photoCount = photoCount;
    }

    public UploadImagesPhase getPhase() {
        return this.phase;
    }

    public long getTotalUploadSize() {
        return this.totalUploadSize;
    }

    public long getUploadedSize() {
        return this.uploadedSize;
    }

    public int getPhotoCount() {
        return this.photoCount;
    }

    public int getUploadedPhotos() {
        return this.uploadedPhotos;
    }

    public int getPreparedPhotos() {
        return this.preparedPhotos;
    }

    void preparing() {
        this.phase = UploadImagesPhase.PREPARE;
        setExecutionState(PersistentTaskState.EXECUTING);
    }

    void uploading(long totalSize) {
        this.phase = UploadImagesPhase.UPLOAD;
        setExecutionState(PersistentTaskState.EXECUTING);
        this.totalUploadSize = totalSize;
    }

    void complete() {
        setExecutionState(PersistentTaskState.COMPLETED);
        this.phase = UploadImagesPhase.COMPLETED;
    }

    void updateProgress(long uploadedSize, int preparedPhotos, int uploadedPhotos) {
        this.uploadedSize = uploadedSize;
        this.preparedPhotos = preparedPhotos;
        this.uploadedPhotos = uploadedPhotos;
    }

    void onStateChanged(PersistentTaskState newState) {
        setExecutionState(newState);
    }

    void onSubTaskStateChanged(PersistentTask subTask) {
        if (subTask instanceof PrepareImageTask) {
            this.phase = UploadImagesPhase.PREPARE;
        } else if (subTask instanceof UploadOneImageTask) {
            this.phase = UploadImagesPhase.UPLOAD;
        }
        PersistentTaskState subTaskState = subTask.getState();
        switch (C04592.$SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[subTaskState.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                setExecutionState(PersistentTaskState.EXECUTING);
                return;
            case Message.TYPE_FIELD_NUMBER /*3*/:
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                if (!(subTask instanceof PrepareImageTask)) {
                    if (subTask instanceof UploadOneImageTask) {
                        setError(subTask.getError(ImageUploadException.class));
                        break;
                    }
                }
                setError(subTask.getError(ImageUploadException.class));
                break;
                break;
            case Message.UUID_FIELD_NUMBER /*5*/:
                break;
            default:
                return;
        }
        setExecutionState(subTaskState);
    }

    protected void appendFieldsToString(StringBuilder out) {
        super.appendFieldsToString(out);
        out.append(" phase=").append(this.phase);
        out.append(" photoCount=").append(this.photoCount);
        out.append(" preparedPhotos=").append(this.preparedPhotos);
        out.append(" uploadedPhotos=").append(this.uploadedPhotos);
        out.append(" totalUploadSize=").append(this.totalUploadSize);
        out.append(" uploadedSize=").append(this.uploadedSize);
    }

    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        UploadImagesState other = (UploadImagesState) o;
        if (this.phase == other.phase && this.photoCount == other.photoCount && this.preparedPhotos == other.preparedPhotos && this.uploadedPhotos == other.uploadedPhotos && this.totalUploadSize == other.totalUploadSize && this.uploadedSize == other.uploadedSize) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (((((super.hashCode() + (this.phase.ordinal() * 434603879)) + (this.photoCount * 2080922759)) + (this.preparedPhotos * 1121930461)) + (this.uploadedPhotos * 732338429)) + (((int) this.totalUploadSize) * 1393237663)) + (((int) this.uploadedSize) * 124126987);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.phase.ordinal());
        dest.writeInt(this.photoCount);
        dest.writeInt(this.preparedPhotos);
        dest.writeInt(this.uploadedPhotos);
        dest.writeLong(this.totalUploadSize);
        dest.writeLong(this.uploadedSize);
    }

    protected UploadImagesState(Parcel src) {
        super(src);
        this.phase = UploadImagesPhase.values()[src.readInt()];
        this.photoCount = src.readInt();
        this.preparedPhotos = src.readInt();
        this.uploadedPhotos = src.readInt();
        this.totalUploadSize = src.readLong();
        this.uploadedSize = src.readLong();
    }

    static {
        CREATOR = new C04581();
    }
}
