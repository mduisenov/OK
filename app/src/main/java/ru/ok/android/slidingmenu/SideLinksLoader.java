package ru.ok.android.slidingmenu;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.Loader$android.support.v4.content.Loader.ForceLoadContentObserver;
import java.util.ArrayList;
import ru.ok.android.db.access.PromoLinkStorageFacade;
import ru.ok.android.db.provider.OdklContract.PromoLinks;
import ru.ok.model.stream.banner.PromoLink;
import ru.ok.model.stream.banner.PromoLinkBuilder;

public class SideLinksLoader extends AsyncTaskLoader<ArrayList<PromoLink>> {
    private ArrayList<PromoLink> lastDeliveredResult;
    private ForceLoadContentObserver observer;
    private final int[] types;

    public SideLinksLoader(Context context, int[] types) {
        super(context);
        this.types = types;
    }

    protected void onStartLoading() {
        if (this.lastDeliveredResult != null) {
            deliverResult(this.lastDeliveredResult);
        } else {
            forceLoad();
        }
        if (this.observer == null) {
            this.observer = new Loader.ForceLoadContentObserver(this);
            getContext().getContentResolver().registerContentObserver(PromoLinks.getContentUri(), true, this.observer);
        }
    }

    protected void onReset() {
        super.onReset();
        if (this.observer != null) {
            getContext().getContentResolver().unregisterContentObserver(this.observer);
        }
    }

    public ArrayList<PromoLink> loadInBackground() {
        return PromoLinkBuilder.build(PromoLinkStorageFacade.queryPromoLinks(getContext().getContentResolver(), this.types));
    }

    public void deliverResult(ArrayList<PromoLink> data) {
        this.lastDeliveredResult = data;
        super.deliverResult(data);
    }
}
