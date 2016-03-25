package ru.ok.android.ui.stream.list;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.ok.android.ui.custom.imageview.AsyncDraweeView;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.utils.PrefetchUtils;

public class StreamBannerHeaderItem extends AbsStreamClickableItem {
    private final Uri iconImage;
    private final CharSequence title;

    static class BannerHeaderViewHolder extends ViewHolder {
        final AsyncDraweeView iconView;
        final TextView textView;

        public BannerHeaderViewHolder(View view) {
            super(view);
            this.iconView = (AsyncDraweeView) view.findViewById(2131625343);
            this.textView = (TextView) view.findViewById(2131625344);
        }
    }

    protected StreamBannerHeaderItem(FeedWithState feed, Uri iconImage, CharSequence title, BannerClickAction clickAction) {
        super(27, 1, 3, feed, clickAction);
        this.iconImage = iconImage;
        this.title = title;
    }

    public void prefetch() {
        PrefetchUtils.prefetchUrl(this.iconImage);
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903469, parent, false);
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder instanceof BannerHeaderViewHolder) {
            BannerHeaderViewHolder viewHolder = (BannerHeaderViewHolder) holder;
            viewHolder.iconView.setUri(this.iconImage);
            viewHolder.textView.setText(this.title);
        }
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    public static ViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        return new BannerHeaderViewHolder(view);
    }
}
