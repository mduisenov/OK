package ru.ok.android.ui.fragments.messages.view.state;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import ru.mail.libverify.C0176R;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fragments.web.WebBaseFragment;
import ru.ok.android.ui.custom.emptyview.SmartEmptyView;
import ru.ok.android.ui.custom.imageview.UrlImageView;
import ru.ok.android.ui.fragments.messages.view.DiscussionPhotoView;

public final class DiscussionInfoViewFactory {

    static class AlbumHolder {
        final UrlImageView image;
        final TextView name;
        final TextView photosCount;
        final View row;

        AlbumHolder(View view) {
            this.row = view;
            this.image = (UrlImageView) view.findViewById(C0263R.id.image);
            this.photosCount = (TextView) view.findViewById(2131624778);
            this.name = (TextView) view.findViewById(C0263R.id.name);
        }
    }

    static class PhotoHolder {
        final DiscussionPhotoView image;

        PhotoHolder(View view) {
            this.image = (DiscussionPhotoView) view.findViewById(C0263R.id.image);
        }
    }

    static class ShareLinkHolder {
        final TextView comment;
        final TextView description;
        final UrlImageView icon;

        ShareLinkHolder(View view) {
            this.icon = (UrlImageView) view.findViewById(C0176R.id.icon);
            this.description = (TextView) view.findViewById(2131624899);
            this.comment = (TextView) view.findViewById(2131624887);
        }
    }

    static class VideoHolder extends PhotoHolder {
        final View playButton;

        VideoHolder(View view) {
            super(view);
            this.playButton = view.findViewById(2131624795);
        }
    }

    static class WebHolder {
        final SmartEmptyView emptyView;
        final WebView webView;

        WebHolder(View view) {
            this.webView = (WebView) view.findViewById(2131624796);
            this.emptyView = (SmartEmptyView) view.findViewById(C0263R.id.empty_view);
        }
    }

    static View webView(Context context) {
        View result = LayoutInflater.from(context).inflate(2130903167, null, false);
        WebHolder holder = new WebHolder(result);
        result.setTag(holder);
        WebBaseFragment.syncSettings(context, holder.webView.getSettings());
        return result;
    }

    static View photoView(Context context) {
        View result = LayoutInflater.from(context).inflate(2130903166, null, false);
        result.setTag(new PhotoHolder(result));
        return result;
    }

    static View photoAlbumView(Context context) {
        View result = LayoutInflater.from(context).inflate(2130903162, null, false);
        result.setTag(new AlbumHolder(result));
        return result;
    }

    public static View movieView(Context context) {
        View result = LayoutInflater.from(context).inflate(2130903165, null, false);
        result.setTag(new VideoHolder(result));
        return result;
    }

    public static View shareView(Context context) {
        View result = LayoutInflater.from(context).inflate(2130903435, null, false);
        result.setTag(new ShareLinkHolder(result));
        return result;
    }
}
