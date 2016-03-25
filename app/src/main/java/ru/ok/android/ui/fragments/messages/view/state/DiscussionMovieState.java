package ru.ok.android.ui.fragments.messages.view.state;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import ru.ok.android.ui.fragments.messages.view.DiscussionInfoView.DiscussionInfoViewListener;
import ru.ok.java.api.response.discussion.info.DiscussionInfoResponse;
import ru.ok.java.api.response.video.VideoGetResponse;

public final class DiscussionMovieState extends DiscussionPhotoState implements OnClickListener {
    private final DiscussionInfoViewListener listener;
    private VideoGetResponse videoInfo;

    public DiscussionMovieState(DiscussionInfoResponse infoResponse, DiscussionInfoViewListener listener) {
        super(infoResponse, listener);
        this.listener = listener;
        this.videoInfo = infoResponse.videoInfo;
    }

    public View createContentView(Context context) {
        View view = DiscussionInfoViewFactory.movieView(context);
        VideoHolder holder = (VideoHolder) view.getTag();
        holder.image.setOnClickListener(null);
        holder.playButton.setOnClickListener(this);
        return view;
    }

    public void onClick(View v) {
        this.listener.onMovieClicked(this.videoInfo);
    }
}
