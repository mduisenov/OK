package ru.ok.android.ui.fragments.messages.view.state;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import ru.ok.android.utils.Utils;
import ru.ok.java.api.response.discussion.info.DiscussionInfoResponse;
import ru.ok.java.api.response.discussion.info.ShareInfo;

public final class DiscussionShareState extends DiscussionState implements OnClickListener {
    private Context context;
    private final ShareInfo shareInfo;
    private String url;

    public DiscussionShareState(DiscussionInfoResponse infoResponse) {
        this.shareInfo = infoResponse.shareInfo;
    }

    public void configureView(View contentView, DiscussionInfoResponse discussion) {
        this.url = this.shareInfo.url;
        ShareLinkHolder holder = (ShareLinkHolder) contentView.getTag();
        Utils.setImageViewUrlWithVisibility(holder.icon, this.shareInfo.imageUrl, 0);
        Utils.setTextViewTextWithVisibility(holder.comment, this.shareInfo.comment);
        Utils.setTextViewTextWithVisibility(holder.description, this.shareInfo.description);
        this.context = contentView.getContext();
    }

    public View createContentView(Context context) {
        View view = DiscussionInfoViewFactory.shareView(context);
        view.setOnClickListener(this);
        return view;
    }

    public void onContentClicked() {
        onClick(null);
    }

    public void onClick(View v) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse(this.url));
        this.context.startActivity(intent);
    }
}
