package ru.ok.android.services.processors.banners;

import android.content.ContentProviderOperation;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.db.access.AdStatsStorageFacade;
import ru.ok.android.db.access.PromoLinkStorageFacade;
import ru.ok.android.db.provider.OdklContract.PromoLinks;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.exceptions.ResultParsingException;
import ru.ok.model.stream.banner.BannerLinkType;
import ru.ok.model.stream.banner.PromoLinkBuilder;

public class BannerLinksProcessor {
    public static void processBannerLinksResponse(ArrayList<PromoLinkBuilder> links, String requestedId, BannerLinkType[] requestedTypes) throws ResultParsingException {
        Logger.m173d("requestedId=%s requestedTypes=%s", requestedId, requestedTypes);
        if (requestedTypes != null) {
            removeOldPromoLinks(requestedTypes, requestedId);
        }
        savePromoLinks(links);
        OdnoklassnikiApplication.getContext().getContentResolver().notifyChange(PromoLinks.getContentUri(), null);
    }

    private static void removeOldPromoLinks(BannerLinkType[] types, String id) {
        for (BannerLinkType type : types) {
            PromoLinkStorageFacade.remove(OdnoklassnikiApplication.getContext().getContentResolver(), PromoLinkBuilder.convertType(type), id);
        }
    }

    private static void savePromoLinks(List<PromoLinkBuilder> promoLinks) {
        ArrayList<ContentProviderOperation> operations = new ArrayList();
        for (PromoLinkBuilder link : promoLinks) {
            int feedBackRef = operations.size();
            PromoLinkStorageFacade.getInsertPromoLinkOps(link, operations);
            if (feedBackRef != operations.size()) {
                AdStatsStorageFacade.getCleanAdStatOps(2, feedBackRef, operations);
                for (int statType = 0; statType < 29; statType++) {
                    List<String> statUrls = link.getStatPixels(statType);
                    if (statUrls != null) {
                        AdStatsStorageFacade.getInsertAdStatOps(2, feedBackRef, statType, statUrls, operations);
                    }
                }
            }
        }
        try {
            OdnoklassnikiApplication.getContext().getContentResolver().applyBatch(OdklProvider.AUTHORITY, operations);
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to save promo links to cache");
        }
    }
}
