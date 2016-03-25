package ru.ok.android.app.helper;

final class RequestStatusInfo {
    long lastResultTime;
    long startTime;
    RequestStatus status;

    RequestStatusInfo() {
        this.status = RequestStatus.UNKNOWN;
    }
}
