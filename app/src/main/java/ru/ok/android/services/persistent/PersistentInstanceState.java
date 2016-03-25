package ru.ok.android.services.persistent;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

public abstract class PersistentInstanceState<TError extends TaskException> implements Parcelable, Serializable {
    private static final long serialVersionUID = 1;
    private TError error;
    private PersistentTaskState executionState;
    private boolean isPausing;

    public PersistentInstanceState() {
        this.executionState = PersistentTaskState.SUBMITTED;
        this.isPausing = false;
        this.error = null;
    }

    public PersistentTaskState getExecutionState() {
        return this.executionState;
    }

    public boolean isPausing() {
        return this.isPausing;
    }

    public TError getError() {
        return this.error;
    }

    protected void setExecutionState(PersistentTaskState executionState) {
        this.executionState = executionState;
    }

    protected void setIsPausing(boolean isPausing) {
        this.isPausing = isPausing;
    }

    protected void setError(TError error) {
        this.error = error;
    }

    public final String toString() {
        StringBuilder out = new StringBuilder();
        out.append(getClass().getSimpleName()).append("[");
        appendFieldsToString(out);
        out.append("]");
        return out.toString();
    }

    protected void appendFieldsToString(StringBuilder out) {
        out.append(this.executionState);
        out.append(" isPausing=").append(this.isPausing);
        out.append(" error=").append(this.error);
    }

    public boolean equals(Object o) {
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        PersistentInstanceState other = (PersistentInstanceState) o;
        if (this.executionState != other.executionState || this.isPausing != other.isPausing) {
            return false;
        }
        if ((this.error != null || other.error != null) && (this.error == null || !this.error.equals(other.error))) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int i = 0;
        int ordinal = (this.isPausing ? 1589402933 : 0) + (927302413 * this.executionState.ordinal());
        if (this.error != null) {
            i = this.error.hashCode() * 456302207;
        }
        return ordinal + i;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.executionState.ordinal());
        dest.writeInt(this.isPausing ? 1 : 0);
        dest.writeParcelable(this.error, flags);
    }

    protected PersistentInstanceState(Parcel src) {
        this.executionState = PersistentTaskState.values()[src.readInt()];
        this.isPausing = src.readInt() != 0;
        this.error = (TaskException) src.readParcelable(getClass().getClassLoader());
    }
}
