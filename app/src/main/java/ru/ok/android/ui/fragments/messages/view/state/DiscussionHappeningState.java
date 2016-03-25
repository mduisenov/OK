package ru.ok.android.ui.fragments.messages.view.state;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import ru.ok.java.api.response.discussion.info.DiscussionInfoResponse;
import ru.ok.java.api.response.discussion.info.HappeningInfo;

public final class DiscussionHappeningState extends DiscussionState {
    private final HappeningInfo happeningInfo;

    public DiscussionHappeningState(DiscussionInfoResponse infoResponse) {
        this.happeningInfo = infoResponse.happeningInfo;
    }

    public void configureView(View contentView, DiscussionInfoResponse discussion) {
        String url = this.happeningInfo.imageUrl;
        ((PhotoHolder) contentView.getTag()).image.setUri(!TextUtils.isEmpty(url) ? Uri.parse(url) : null);
    }

    public View createContentView(Context context) {
        return DiscussionInfoViewFactory.photoView(context);
    }

    public void onContentClicked() {
    }

    public boolean isDateVisible() {
        return false;
    }
}
