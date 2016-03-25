package ru.ok.android.flurry;

import com.flurry.android.FlurryAgent;
import java.net.SocketException;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.stream.StreamLoadException;

public final class StreamErrors {
    public static void logAndFilterError(String id, String msg, Throwable throwable) {
        if (throwable instanceof StreamLoadException) {
            ErrorType errorType = ErrorType.from(((StreamLoadException) throwable).getErrorBundle());
            if (errorType == ErrorType.NO_INTERNET || errorType == ErrorType.NO_INTERNET_TOO_LONG) {
                return;
            }
        }
        if (!(throwable instanceof SocketException)) {
            FlurryAgent.onError(id, msg, throwable);
        }
    }
}
