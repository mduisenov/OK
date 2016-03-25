package ru.ok.android.ui.stream.list;

import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.app.SpritesHelper;
import ru.ok.android.ui.custom.CompositePresentView;
import ru.ok.android.ui.presents.helpers.PresentSettingsHelper;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.ui.stream.view.PresentTrackView;
import ru.ok.android.utils.PrefetchUtils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.UserInfo;
import ru.ok.model.stream.entities.FeedMusicTrackEntity;
import ru.ok.model.stream.entities.FeedPresentTypeEntity;
import ru.ok.model.stream.entities.FeedUserEntity;

public class StreamPresentItem extends AbsStreamClickableItem {
    private final PresentInfo presentInfo;
    private final int presentSizeBig;
    private final int presentSizeNormal;
    private final FeedUserEntity receiver;
    private final boolean showButton;

    protected static class PresentViewHolder extends ViewHolder {
        final Button makePresentButton;
        final PresentTrackView playButton;
        final CompositePresentView presentView;

        PresentViewHolder(View view) {
            super(view);
            this.makePresentButton = (Button) view.findViewById(2131625360);
            this.playButton = (PresentTrackView) view.findViewById(2131624795);
            this.presentView = (CompositePresentView) view.findViewById(2131625366);
        }

        public void setNeededSize(boolean isBig, int presentSizeBig, int presentSizeNormal) {
            int i;
            LayoutParams layoutParams = this.presentView.getLayoutParams();
            if (isBig) {
                i = presentSizeBig;
            } else {
                i = presentSizeNormal;
            }
            layoutParams.height = i;
            LayoutParams layoutParams2 = this.presentView.getLayoutParams();
            if (!isBig) {
                presentSizeBig = presentSizeNormal;
            }
            layoutParams2.width = presentSizeBig;
            this.presentView.requestLayout();
        }
    }

    protected StreamPresentItem(FeedWithState feed, FeedUserEntity receiver, PresentInfo presentInfo, boolean showButton, int presentSizeNormal, int presentSizeBig) {
        super(13, 3, 1, feed, new PresentClickAction(feed, presentInfo));
        this.receiver = receiver;
        this.showButton = showButton;
        this.presentSizeNormal = presentSizeNormal;
        this.presentSizeBig = presentSizeBig;
        this.presentInfo = presentInfo;
    }

    public void prefetch() {
        FeedPresentTypeEntity presentType = this.presentInfo.presentType;
        if (presentType.isAnimated() && PresentSettingsHelper.isAnimatedPresentsEnabled()) {
            SpritesHelper.prefetch(presentType, new Point(this.presentSizeBig, this.presentSizeBig));
        } else {
            PrefetchUtils.prefetchUrl(presentType.getLargestPicUrl());
        }
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return LocalizationManager.inflate(parent.getContext(), 2130903496, parent, false);
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder instanceof PresentViewHolder) {
            PresentViewHolder presentViewHolder = (PresentViewHolder) holder;
            UserInfo receiverUser = this.receiver.getUserInfo();
            presentViewHolder.setNeededSize(this.presentInfo.isBig, this.presentSizeBig, this.presentSizeNormal);
            int presentSize = OdnoklassnikiApplication.getContext().getResources().getDimensionPixelSize(2131230993);
            presentViewHolder.presentView.setPresentType(this.presentInfo.presentType, new Point(presentSize, presentSize));
            presentViewHolder.presentView.setTag(2131624336, this.presentInfo);
            presentViewHolder.presentView.setTag(2131624322, this.feedWithState);
            if (!this.showButton || this.presentInfo.presentType == null) {
                presentViewHolder.makePresentButton.setVisibility(4);
            } else {
                presentViewHolder.makePresentButton.setTag(2131624336, this.presentInfo);
                presentViewHolder.makePresentButton.setTag(2131624354, receiverUser);
                presentViewHolder.makePresentButton.setVisibility(0);
                if (this.presentInfo.isBadge) {
                    presentViewHolder.makePresentButton.setText(2131166519);
                } else {
                    presentViewHolder.makePresentButton.setText(2131166720);
                }
            }
            presentViewHolder.makePresentButton.setTag(2131624322, this.feedWithState);
            if (this.presentInfo.isMusic) {
                presentViewHolder.playButton.setTrack((FeedMusicTrackEntity) this.presentInfo.tracks.get(0));
                presentViewHolder.playButton.setTag(2131624322, this.feedWithState);
            }
            presentViewHolder.playButton.setVisibility(this.presentInfo.isMusic ? 0 : 8);
        }
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    public static ViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        PresentViewHolder viewHolder = new PresentViewHolder(view);
        viewHolder.makePresentButton.setOnClickListener(streamItemViewController.getMakePresentClickListener());
        viewHolder.presentView.setOnClickListener(streamItemViewController.getPresentClickListener());
        viewHolder.playButton.setPlayerStateHolder(streamItemViewController.getPlayerStateHolder());
        return viewHolder;
    }

    boolean sharePressedState() {
        return false;
    }
}
