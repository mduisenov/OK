package ru.ok.android.ui.stream.list;

import android.app.Activity;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import ru.mail.libverify.C0176R;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fragments.web.hooks.WebLinksProcessor;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.PrefetchUtils;
import ru.ok.android.utils.URLUtil;
import ru.ok.model.mediatopics.MediaItemApp;
import ru.ok.model.stream.entities.FeedAppEntity;

public class StreamAppItem extends StreamItem implements OnClickListener {
    private final MediaItemApp appMedia;
    private final DiscussionClickAction imageClickAction;

    static class AppHolder extends ViewHolder {
        private final TextView actionView;
        private final SimpleDraweeView imageView;
        private final TextView titleView;

        public AppHolder(View view) {
            super(view);
            this.titleView = (TextView) view.findViewById(C0176R.id.title);
            this.actionView = (TextView) view.findViewById(2131624679);
            this.imageView = (SimpleDraweeView) view.findViewById(C0263R.id.image);
        }
    }

    public void prefetch() {
        PrefetchUtils.prefetchUrl(this.appMedia.getImage());
    }

    protected StreamAppItem(FeedWithState feed, MediaItemApp mediaItem, DiscussionClickAction imageClickAction) {
        super(44, 1, 1, feed);
        this.appMedia = mediaItem;
        this.imageClickAction = imageClickAction;
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903465, parent, false);
    }

    public static ViewHolder newViewHolder(View view, StreamItemViewController streamItemViewController) {
        return new AppHolder(view);
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        if (holder instanceof AppHolder) {
            AppHolder appHolder = (AppHolder) holder;
            if (TextUtils.isEmpty(this.appMedia.getImageTitle())) {
                appHolder.titleView.setVisibility(8);
            } else {
                appHolder.titleView.setText(this.appMedia.getImageTitle());
                appHolder.titleView.setVisibility(0);
            }
            if (TextUtils.isEmpty(this.appMedia.getImage())) {
                appHolder.imageView.setVisibility(4);
            } else {
                appHolder.imageView.setImageURI(URLUtil.isStubUrl(this.appMedia.getImage()) ? null : Uri.parse(this.appMedia.getImage()));
                appHolder.imageView.setVisibility(0);
                AbsStreamClickableItem.setupClick(appHolder.imageView, streamItemViewController, this.imageClickAction);
            }
            appHolder.actionView.setOnClickListener(this);
        }
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case 2131624679:
                handlePlayClick(v);
            default:
        }
    }

    private void handlePlayClick(View v) {
        FeedAppEntity game = this.appMedia.getApp();
        String storeId = DeviceUtils.isTablet(OdnoklassnikiApplication.getContext()) ? game.getTabStoreId() : game.getStoreId();
        if (TextUtils.isEmpty(storeId)) {
            new WebLinksProcessor((Activity) v.getContext(), false, true).processUrl(ConfigurationPreferences.getInstance().getWebServer() + "app/" + game.getId());
        } else {
            NavigationHelper.launchApplication(v.getContext(), storeId);
        }
    }
}
