package ru.ok.android.services.processors.mediatopic;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.persistent.PersistentInstanceState;
import ru.ok.android.services.persistent.PersistentTask;
import ru.ok.android.services.persistent.PersistentTaskState;
import ru.ok.android.services.processors.image.upload.UploadImagesState;
import ru.ok.android.services.processors.image.upload.UploadImagesTask;

public final class MediaTopicPostState extends PersistentInstanceState<MediaTopicPostException> {
    public static final Creator<MediaTopicPostState> CREATOR;
    private static final long serialVersionUID = 1;
    private boolean hasPhotos;
    private transient MediaTopicErrorListener listener;
    private MediaTopicPostPhase phase;
    private UploadImagesState uploadImagesState;

    /* renamed from: ru.ok.android.services.processors.mediatopic.MediaTopicPostState.1 */
    static class C04661 implements Creator<MediaTopicPostState> {
        C04661() {
        }

        public MediaTopicPostState createFromParcel(Parcel source) {
            return new MediaTopicPostState(source);
        }

        public MediaTopicPostState[] newArray(int size) {
            return new MediaTopicPostState[size];
        }
    }

    /* renamed from: ru.ok.android.services.processors.mediatopic.MediaTopicPostState.2 */
    static /* synthetic */ class C04672 {
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

    interface MediaTopicErrorListener {
        void onMediaTopicError();
    }

    public enum MediaTopicPostPhase {
        STARTING,
        UPLOADING_IMAGES,
        UPLOADING_MEDIA_TOPIC,
        COMPLETED
    }

    public MediaTopicPostState(boolean hasPhotos) {
        this.phase = MediaTopicPostPhase.STARTING;
        this.hasPhotos = hasPhotos;
    }

    public MediaTopicPostPhase getPhase() {
        return this.phase;
    }

    public boolean hasPhotos() {
        return this.hasPhotos;
    }

    void setErrorListener(MediaTopicErrorListener listener) {
        this.listener = listener;
    }

    public UploadImagesState getUploadImagesState() {
        return this.uploadImagesState;
    }

    protected void appendFieldsToString(StringBuilder out) {
        super.appendFieldsToString(out);
        out.append(" phase=").append(this.phase);
        out.append(" hasPhotos=").append(this.hasPhotos);
        out.append(" uploadImagesState=").append(this.uploadImagesState);
    }

    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        MediaTopicPostState other = (MediaTopicPostState) o;
        if (this.phase != other.phase || this.hasPhotos != other.hasPhotos) {
            return false;
        }
        if ((this.uploadImagesState != null || other.uploadImagesState != null) && (this.uploadImagesState == null || !this.uploadImagesState.equals(other.uploadImagesState))) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int i = 0;
        int ordinal = (this.hasPhotos ? 1123447513 : 0) + ((this.phase.ordinal() * 1502369503) + super.hashCode());
        if (this.uploadImagesState != null) {
            i = 502377011 * this.uploadImagesState.hashCode();
        }
        return ordinal + i;
    }

    void executing() {
        setExecutionState(PersistentTaskState.EXECUTING);
        setIsPausing(false);
    }

    void pausing() {
        setIsPausing(true);
    }

    void posting() {
        setExecutionState(PersistentTaskState.EXECUTING);
        this.phase = MediaTopicPostPhase.UPLOADING_MEDIA_TOPIC;
    }

    void failed(MediaTopicPostException e) {
        this.phase = MediaTopicPostPhase.UPLOADING_MEDIA_TOPIC;
        setExecutionState(PersistentTaskState.FAILED);
        setError(e);
    }

    void complete() {
        this.phase = MediaTopicPostPhase.COMPLETED;
        setExecutionState(PersistentTaskState.COMPLETED);
    }

    void onStateChanged(PersistentTaskState newState) {
        setExecutionState(newState);
        if (newState == PersistentTaskState.PAUSED) {
            setIsPausing(false);
        }
    }

    void onSubTaskStateChanged(PersistentTask subTask) {
        if (subTask instanceof UploadImagesTask) {
            UploadImagesTask uploadImagesTask = (UploadImagesTask) subTask;
            this.phase = MediaTopicPostPhase.UPLOADING_IMAGES;
            this.uploadImagesState = uploadImagesTask.getUploadState();
            switch (C04672.$SwitchMap$ru$ok$android$services$persistent$PersistentTaskState[this.uploadImagesState.getExecutionState().ordinal()]) {
                case Message.TEXT_FIELD_NUMBER /*1*/:
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    setExecutionState(PersistentTaskState.EXECUTING);
                    setIsPausing(subTask.isPausing());
                    return;
                case Message.TYPE_FIELD_NUMBER /*3*/:
                case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                    setError(new MediaTopicPostException(11, this.uploadImagesState.getError()));
                    break;
                case Message.UUID_FIELD_NUMBER /*5*/:
                    break;
                default:
                    return;
            }
            setExecutionState(this.uploadImagesState.getExecutionState());
            setIsPausing(false);
        }
    }

    protected void setError(MediaTopicPostException e) {
        super.setError(e);
        if (this.listener != null) {
            this.listener.onMediaTopicError();
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.phase.ordinal());
        dest.writeInt(this.hasPhotos ? 1 : 0);
        dest.writeParcelable(this.uploadImagesState, flags);
    }

    protected MediaTopicPostState(Parcel src) {
        super(src);
        ClassLoader cl = MediaTopicPostState.class.getClassLoader();
        this.phase = MediaTopicPostPhase.values()[src.readInt()];
        this.hasPhotos = src.readInt() != 0;
        this.uploadImagesState = (UploadImagesState) src.readParcelable(cl);
    }

    static {
        CREATOR = new C04661();
    }
}
