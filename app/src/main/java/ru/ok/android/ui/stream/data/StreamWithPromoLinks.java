package ru.ok.android.ui.stream.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import ru.ok.model.stream.Holidays;
import ru.ok.model.stream.StreamPage;
import ru.ok.model.stream.banner.PromoLinkBuilder;

public class StreamWithPromoLinks {
    public final int benchmarkSeqId;
    public final boolean fromAPI;
    @Nullable
    public final Holidays holidays;
    @Nullable
    public final ArrayList<PromoLinkBuilder> promoLinks;
    @NonNull
    public final StreamPage streamPage;

    public StreamWithPromoLinks(@NonNull StreamPage streamPage, @Nullable ArrayList<PromoLinkBuilder> promoLinks, @Nullable Holidays holidays, boolean fromAPI) {
        this(streamPage, promoLinks, holidays, fromAPI, 0);
    }

    public StreamWithPromoLinks(@NonNull StreamPage streamPage, @Nullable ArrayList<PromoLinkBuilder> promoLinks, @Nullable Holidays holidays, boolean fromAPI, int benchmarkSeqId) {
        this.streamPage = streamPage;
        this.promoLinks = promoLinks;
        this.fromAPI = fromAPI;
        this.benchmarkSeqId = benchmarkSeqId;
        this.holidays = holidays;
    }

    public String toString() {
        return "{streamPage=" + this.streamPage + " promoLinks=" + this.promoLinks + ", holidays=" + this.holidays + "}";
    }
}
