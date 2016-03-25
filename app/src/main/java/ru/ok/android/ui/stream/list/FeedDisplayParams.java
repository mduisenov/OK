package ru.ok.android.ui.stream.list;

import android.text.TextUtils;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.ui.stream.data.StreamContext;

public class FeedDisplayParams {
    public final boolean doAuthorInHeader;
    public final boolean doCollapseVideos;
    public final boolean doDisplayJoinGroupButton;

    public FeedDisplayParams(boolean doCollapseVideos, boolean doDisplayJoinGroupButton, boolean doAuthorInHeader) {
        this.doCollapseVideos = doCollapseVideos;
        this.doDisplayJoinGroupButton = doDisplayJoinGroupButton;
        this.doAuthorInHeader = doAuthorInHeader;
    }

    public FeedDisplayParams() {
        this.doCollapseVideos = false;
        this.doDisplayJoinGroupButton = true;
        this.doAuthorInHeader = false;
    }

    public static FeedDisplayParams fromStreamContext(StreamContext streamContext) {
        boolean doAuthorInHeader = true;
        if (streamContext == null) {
            return new FeedDisplayParams();
        }
        boolean doCollapseVideos;
        boolean doDisplayJoinGroupButton;
        int type = streamContext.type;
        String streamId = streamContext.id;
        String currentUserId = OdnoklassnikiApplication.getCurrentUser().uid;
        if (type == 1) {
            doCollapseVideos = true;
        } else {
            doCollapseVideos = false;
        }
        if (type == 2 && TextUtils.equals(streamId, currentUserId)) {
            doDisplayJoinGroupButton = false;
        } else {
            doDisplayJoinGroupButton = true;
        }
        if (type != 3) {
            doAuthorInHeader = false;
        }
        return new FeedDisplayParams(doCollapseVideos, doDisplayJoinGroupButton, doAuthorInHeader);
    }
}
