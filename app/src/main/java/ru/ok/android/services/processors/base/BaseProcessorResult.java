package ru.ok.android.services.processors.base;

import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;

public class BaseProcessorResult {
    public final ErrorType errorType;
    public final boolean isSuccess;

    public BaseProcessorResult(boolean isSuccess, ErrorType errorType) {
        this.isSuccess = isSuccess;
        this.errorType = errorType;
    }
}
