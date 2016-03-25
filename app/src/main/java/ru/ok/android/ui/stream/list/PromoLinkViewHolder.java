package ru.ok.android.ui.stream.list;

import android.app.Activity;
import android.support.annotation.NonNull;
import ru.ok.android.model.cache.ImageLoader.HandleBlocker;
import ru.ok.android.services.processors.banners.BannerLinksUtils;
import ru.ok.android.statistics.stream.StreamStats;
import ru.ok.android.ui.groups.list.StreamItemRecyclerAdapter;
import ru.ok.android.ui.stream.list.StreamItemAdapter.ViewHolder;
import ru.ok.android.ui.stream.view.PromoLinkAndHolidayView;
import ru.ok.android.ui.stream.view.PromoLinkView.PromoLinkViewListener;
import ru.ok.android.utils.Utils;
import ru.ok.model.stream.banner.PromoLink;

public class PromoLinkViewHolder extends ViewHolder implements PromoLinkViewListener {
    private Activity activity;
    private StreamItemRecyclerAdapter adapter;
    private PromoLinkAndHolidayView view;

    public PromoLinkViewHolder(@NonNull PromoLinkAndHolidayView view) {
        super(view);
        this.view = view;
    }

    public void bind(@NonNull PromoLink promoLink, @NonNull Activity activity, @NonNull StreamItemRecyclerAdapter adapter) {
        this.activity = activity;
        this.adapter = adapter;
        this.view.setHandleBlocker((HandleBlocker) (adapter == null ? null : adapter.getScrollBlocker()));
        this.view.setPromoLink(promoLink);
        this.view.promoLinkView.setListener(this);
    }

    public void updateForLayoutSize(@NonNull StreamLayoutConfig layoutConfig) {
        int extraMargin = layoutConfig.getExtraMarginForLandscapeAsInPortrait(true);
        StreamItem.applyExtraMarginsToBg(this.itemView, extraMargin, extraMargin);
        this.itemView.setPadding(this.originalLeftPadding + extraMargin, this.originalTopPadding, this.originalRightPadding + extraMargin, this.originalBottomPadding);
    }

    public void onPromoLinkClicked(@NonNull PromoLink promoLink) {
        if (this.adapter != null) {
            BannerLinksUtils.processBannerClick(promoLink.banner, this.activity, this.adapter.getStreamItemViewController().getWebLinksProcessor());
        }
        Utils.sendPixels(promoLink, 2, this.activity);
        StreamStats.clickPromoLink();
    }
}
