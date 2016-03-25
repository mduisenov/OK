package ru.ok.android.ui.stream.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import ru.ok.android.ui.custom.BannerAppRatingDrawer;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;

public class StreamBannerCardBottomAppItem extends AbsStreamClickableItem {
    private final float rating;
    private CharSequence text;
    private CharSequence voteText;

    public static class ViewHolder extends ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder {
        private TextView bannerOutLink;
        private BannerAppRatingDrawer bannerRating;
        private TextView bannerVoters;

        public ViewHolder(View view) {
            super(view);
            this.bannerRating = new BannerAppRatingDrawer((LinearLayout) view.findViewById(2131625340));
            this.bannerOutLink = (TextView) view.findViewById(2131625339);
            this.bannerVoters = (TextView) view.findViewById(2131625341);
            view.setTag(2131624314, this);
        }

        void setOutLinkText(CharSequence text) {
            if (this.bannerOutLink != null) {
                this.bannerOutLink.setText(text);
            }
        }

        void setVotersText(CharSequence text) {
            if (this.bannerVoters != null) {
                this.bannerVoters.setText(text);
            }
        }

        void setRating(float rating) {
            if (this.bannerRating != null) {
                this.bannerRating.setRating(rating);
            }
        }
    }

    protected StreamBannerCardBottomAppItem(FeedWithState feed, CharSequence text, CharSequence voteText, BannerClickAction clickAction, float rating) {
        super(26, 1, 4, feed, clickAction);
        this.voteText = voteText;
        this.text = text;
        this.rating = rating;
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903466, parent, false);
    }

    public static ViewHolder newViewHolder(View view) {
        return new ViewHolder(view);
    }

    public void bindView(ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        ViewHolder viewHolder = (ViewHolder) holder.itemView.getTag(2131624314);
        if (viewHolder != null) {
            viewHolder.setRating(this.rating);
            viewHolder.setOutLinkText(this.text);
            viewHolder.setVotersText(this.voteText);
        }
        super.bindView(holder, streamItemViewController, layoutConfig);
    }
}
