package ru.ok.android.utils.indexing;

import android.net.Uri;

public class Action {
    final String actionType;
    final Uri appUri;
    final String title;
    final Uri webUri;

    public static Action newAction(String actionType, String objectName, Uri webUri, Uri appUri) {
        return new Action(actionType, objectName, webUri, appUri);
    }

    private Action(String actionType, String title, Uri webUri, Uri appUri) {
        this.actionType = actionType;
        this.title = title;
        this.webUri = webUri;
        this.appUri = appUri;
    }

    public String getTitle() {
        return this.title;
    }

    public Uri getWebUri() {
        return this.webUri;
    }

    public Uri getAppUri() {
        return this.appUri;
    }
}
