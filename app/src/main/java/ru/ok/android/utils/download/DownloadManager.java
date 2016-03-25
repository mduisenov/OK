package ru.ok.android.utils.download;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public abstract class DownloadManager {

    public static final class Request {
        private boolean allowMediaRescan;
        private Uri destinationUri;
        private Intent intent;
        private int notificationVisibility;
        private final Uri uri;

        public Request(Uri uri) {
            this.uri = uri;
        }

        public Uri getUri() {
            return this.uri;
        }

        public Uri getDestinationUri() {
            return this.destinationUri;
        }

        public void setDestinationUri(Uri destinationUri) {
            this.destinationUri = destinationUri;
        }

        public void allowScanningByMediaScanner() {
            this.allowMediaRescan = true;
        }

        boolean isAllowMediaRescan() {
            return this.allowMediaRescan;
        }

        public void setNotificationVisibility(int notificationVisibility) {
            this.notificationVisibility = notificationVisibility;
        }

        public void setIntent(Intent intent) {
            this.intent = intent;
        }

        public Intent getIntent() {
            return this.intent;
        }
    }

    public abstract long enqueue(Context context, Request request);

    public DownloadManager(Context context) {
    }
}
