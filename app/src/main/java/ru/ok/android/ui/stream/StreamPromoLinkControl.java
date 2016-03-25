package ru.ok.android.ui.stream;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.processors.banners.BannerLinksUtils;
import ru.ok.android.ui.groups.list.StreamItemRecyclerAdapter;
import ru.ok.android.ui.stream.list.PromoLinkViewHolder;
import ru.ok.android.ui.stream.list.StreamLayoutConfig;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.Utils;
import ru.ok.model.stream.banner.PromoLink;
import ru.ok.model.stream.banner.PromoLinkBuilder;

class StreamPromoLinkControl {
    private static int MSG_EXPIRE_PROMO_LINK;
    private Activity activity;
    private final StreamItemRecyclerAdapter adapter;
    private ArrayList<PromoLink> newestPromoLinks;
    private final PromoLinkExpireHandler promoLinkExpireHandler;
    private final PromoLinkViewHolder promoLinkViewHolder;

    static class PromoLinkExpireHandler extends Handler {
        final WeakReference<StreamPromoLinkControl> controlRef;

        PromoLinkExpireHandler(StreamPromoLinkControl control) {
            this.controlRef = new WeakReference(control);
        }

        public void handleMessage(Message msg) {
            StreamPromoLinkControl control = (StreamPromoLinkControl) this.controlRef.get();
            if (control != null && msg.what == StreamPromoLinkControl.MSG_EXPIRE_PROMO_LINK) {
                control.expirePromoLink();
            }
        }
    }

    StreamPromoLinkControl(PromoLinkViewHolder promoLinkViewHolder, StreamItemRecyclerAdapter adapter) {
        this.newestPromoLinks = new ArrayList();
        this.promoLinkExpireHandler = new PromoLinkExpireHandler(this);
        this.promoLinkViewHolder = promoLinkViewHolder;
        this.adapter = adapter;
    }

    void onAttach(Activity activity) {
        this.activity = activity;
        GlobalBus.register(this);
    }

    void onDetach() {
        this.activity = null;
        GlobalBus.unregister(this);
    }

    void updateLayout(StreamLayoutConfig layoutConfig) {
        this.promoLinkViewHolder.updateForLayoutSize(layoutConfig);
    }

    void updatePromoLinks(List<PromoLink> banners) {
        if (this.activity != null) {
            long now = System.currentTimeMillis();
            PromoLink promoLink = BannerLinksUtils.getLastPromoLinkByType(banners, 1, now);
            if (promoLink == null) {
                promoLink = BannerLinksUtils.getLastPromoLinkByType(this.newestPromoLinks, 1, now);
            }
            this.promoLinkViewHolder.bind(promoLink, this.activity, this.adapter);
            this.promoLinkExpireHandler.removeMessages(MSG_EXPIRE_PROMO_LINK);
            if (promoLink != null) {
                Utils.sendPixels(promoLink, 0, this.activity);
                Utils.sendPixels(promoLink, 1, this.activity);
                this.promoLinkExpireHandler.sendEmptyMessageDelayed(MSG_EXPIRE_PROMO_LINK, (promoLink.fetchedTime + 600000) - now);
            }
        }
    }

    void expirePromoLink() {
        if (this.activity != null) {
            this.promoLinkViewHolder.bind(null, this.activity, this.adapter);
        }
    }

    @Subscribe(on = 2131623946, to = 2131624235)
    public void onFetchedPromoLinks(BusEvent event) {
        ArrayList<PromoLink> promoLinks = PromoLinkBuilder.build(event.bundleOutput.getParcelableArrayList("EXTRA_PROMO_LINKS"));
        this.newestPromoLinks = promoLinks;
        Logger.m173d("%s", promoLinks);
        updatePromoLinks(promoLinks);
    }

    static {
        MSG_EXPIRE_PROMO_LINK = 1;
    }
}
