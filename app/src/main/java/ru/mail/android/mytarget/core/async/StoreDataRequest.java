package ru.mail.android.mytarget.core.async;

import android.content.Context;
import ru.mail.android.mytarget.Tracer;
import ru.mail.android.mytarget.core.utils.DiskFileCache;

public class StoreDataRequest extends AbstractRequest {
    private long cachePeriod;
    private String data;
    private int slotId;

    public StoreDataRequest(long cachePeriod, int slotId, String data) {
        this.slotId = slotId;
        this.data = data;
        this.cachePeriod = cachePeriod;
    }

    public void execute(Context context) {
        super.execute(context);
        DiskFileCache cache = DiskFileCache.openCache(context);
        if (cache == null) {
            onExecute(false);
            return;
        }
        boolean result = cache.put(Integer.toString(this.slotId), this.data, this.cachePeriod);
        Tracer.m35d("StoreDataRequest complete with status: " + result);
        onExecute(result);
    }
}
