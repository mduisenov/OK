package ru.ok.android.services.persistent;

import android.os.Parcel;
import ru.ok.java.api.exceptions.ServerReturnErrorException;

public class TaskServerErrorException extends TaskException {
    private static final long serialVersionUID = 1;

    public TaskServerErrorException(int errorCode, String detailMessage, Throwable cause) {
        if (errorCode == 4 && !(cause instanceof ServerReturnErrorException)) {
            errorCode = 999;
        }
        super(errorCode, detailMessage, cause);
    }

    protected TaskServerErrorException(Parcel src) {
        super(src);
    }

    public int getServerErrorCode() {
        Throwable cause = getCause();
        if (cause instanceof ServerReturnErrorException) {
            return ((ServerReturnErrorException) cause).getErrorCode();
        }
        return 0;
    }

    public String getServerErrorMessage() {
        Throwable cause = getCause();
        if (cause instanceof ServerReturnErrorException) {
            return ((ServerReturnErrorException) cause).getErrorMessage();
        }
        return null;
    }

    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        Throwable cause1 = getCause();
        if (!(cause1 instanceof ServerReturnErrorException) || cause1.equals(((TaskServerErrorException) o).getCause())) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int hashCode = super.hashCode();
        Throwable cause = getCause();
        if (cause instanceof ServerReturnErrorException) {
            return hashCode + (434508047 * cause.hashCode());
        }
        return hashCode;
    }
}
