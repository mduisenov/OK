package ru.ok.android.ui.stream.list;

import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest.ImageType;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import ru.mail.libverify.C0176R;
import ru.ok.android.ui.stream.data.FeedWithState;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.list.controller.StreamItemViewController;
import ru.ok.android.utils.PrefetchUtils;
import ru.ok.model.stream.banner.Banner;

public class StreamGiftsCampaignHeaderItem extends AbsStreamWithOptionsItem {
    private final BannerClickAction clickAction;
    private final String iconUrl;
    private final String text;
    private final int textColor;

    protected StreamGiftsCampaignHeaderItem(FeedWithState feedWithState, Banner banner, BannerClickAction clickAction) {
        super(43, 4, 1, feedWithState, true);
        this.clickAction = clickAction;
        this.iconUrl = TextUtils.isEmpty(banner.iconUrlHd) ? banner.iconUrl : banner.iconUrlHd;
        this.text = banner.header;
        this.textColor = banner.color;
    }

    public void bindView(ViewHolder holder, StreamItemViewController streamItemViewController, StreamLayoutConfig layoutConfig) {
        TextView textView = (TextView) holder.itemView.findViewById(2131624635);
        textView.setText(this.text);
        textView.setTextColor(this.textColor);
        SimpleDraweeView iconView = (SimpleDraweeView) holder.itemView.findViewById(C0176R.id.icon);
        iconView.setController(((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) Fresco.newDraweeControllerBuilder().setOldController(iconView.getController())).setImageRequest(ImageRequestBuilder.newBuilderWithSource(Uri.parse(this.iconUrl)).setImageType(ImageType.SMALL).build())).build());
        AbsStreamClickableItem.setupClick(holder.itemView, streamItemViewController, this.clickAction);
        super.bindView(holder, streamItemViewController, layoutConfig);
    }

    public void prefetch() {
        PrefetchUtils.prefetchUrl(this.iconUrl);
    }

    public static View newView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903477, parent, false);
    }
}
