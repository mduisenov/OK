package ru.ok.android.ui.stream.list;

import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import ru.ok.android.ui.custom.CompositePresentView;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.utils.PrefetchUtils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.stream.entities.FeedUserEntity;

public class StreamCardPresentItem extends StreamItem {
    private final PresentInfo presentInfo;
    private final FeedUserEntity receiver;
    private final boolean showMakePresentBtn;

    protected static class CardPresentViewHolder extends ViewHolder {
        final Button makePresentBtn;
        final CompositePresentView presentView;

        CardPresentViewHolder(View view) {
            super(view);
            this.presentView = (CompositePresentView) view.findViewById(2131625347);
            this.makePresentBtn = (Button) view.findViewById(2131625348);
        }
    }

    protected StreamCardPresentItem(FeedWithState feedWithState, FeedUserEntity receiver, PresentInfo presentInfo, boolean showMakePresentBtn) {
        super(46, 3, showMakePresentBtn ? 1 : 4, feedWithState);
        this.receiver = receiver;
        this.presentInfo = presentInfo;
        this.showMakePresentBtn = showMakePresentBtn;
    }

    public void prefetch() {
        PrefetchUtils.prefetchUrl(this.presentInfo.presentImageUrl);
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return LocalizationManager.inflate(parent.getContext(), 2130903473, parent, false);
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder instanceof CardPresentViewHolder) {
            CardPresentViewHolder presentViewHolder = (CardPresentViewHolder) holder;
            presentViewHolder.presentView.setPresentType(this.presentInfo.presentType, new Point());
            presentViewHolder.presentView.setTag(2131624336, this.presentInfo);
            presentViewHolder.presentView.setTag(2131624322, this.feedWithState);
            if (this.showMakePresentBtn) {
                presentViewHolder.makePresentBtn.setTag(2131624336, this.presentInfo);
                presentViewHolder.makePresentBtn.setTag(2131624354, this.receiver.getUserInfo());
                presentViewHolder.makePresentBtn.setTag(2131624322, this.feedWithState);
                presentViewHolder.makePresentBtn.setVisibility(0);
                return;
            }
            presentViewHolder.makePresentBtn.setVisibility(8);
        }
    }

    public static ViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        CardPresentViewHolder viewHolder = new CardPresentViewHolder(view);
        viewHolder.presentView.setOnClickListener(streamItemViewController.getPresentClickListener());
        viewHolder.makePresentBtn.setOnClickListener(streamItemViewController.getMakePresentClickListener());
        return viewHolder;
    }

    boolean sharePressedState() {
        return false;
    }
}
