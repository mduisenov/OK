package ru.ok.android.widget.attach;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import bo.pic.android.media.util.ScaleMode;
import ru.ok.android.app.GifAsMp4ImageLoaderHelper;
import ru.ok.android.ui.custom.imageview.AspectRatioGifAsMp4ImageView;
import ru.ok.model.messages.Attachment;

public class GifAsMp4AttachAdapter extends BaseAttachAdapter {

    private static class MyViewHolder extends Holder {
        @NonNull
        public AspectRatioGifAsMp4ImageView gifAsMp4View;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.gifAsMp4View = (AspectRatioGifAsMp4ImageView) itemView.findViewById(2131624883);
        }
    }

    public GifAsMp4AttachAdapter() {
        super(2130903223);
    }

    @NonNull
    protected Holder createViewHolder(ViewGroup parent, int position) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(this.remoteItemLayoutResourceId, null));
    }

    protected void bindViewHolder(@NonNull Holder holder, int position) {
        MyViewHolder vh = (MyViewHolder) holder;
        Attachment attach = getItem(position);
        int width = getCount() == 1 ? this.twoColumnsSize : this.oneColumnSize;
        int height = this.twoColumnsSize;
        String mp4Url = attach.mp4Url;
        if (!TextUtils.equals(mp4Url, vh.gifAsMp4View.getEmbeddedAnimationUri())) {
            GifAsMp4ImageLoaderHelper.with(vh.itemView.getContext()).load(mp4Url, GifAsMp4ImageLoaderHelper.GIF).setDimensions(width, height).setScaleMode(ScaleMode.CROP).into(vh.gifAsMp4View, true);
        }
    }
}
