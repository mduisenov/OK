package ru.mail.android.mytarget.core;

import java.util.ArrayList;
import ru.mail.android.mytarget.ads.CustomParams;

public final class AdParams {
    private long cachePeriod;
    private boolean checkExclude;
    private CustomParams customParams;
    private ArrayList<String> formats;
    private int slotId;

    public int getSlotId() {
        return this.slotId;
    }

    public ArrayList<String> getFormats() {
        return this.formats;
    }

    public boolean hasFormat(String format) {
        return this.formats.contains(format);
    }

    public CustomParams getCustomParams() {
        return this.customParams;
    }

    public void setCustomParams(CustomParams customParams) {
        this.customParams = customParams;
    }

    public long getCachePeriod() {
        return this.cachePeriod;
    }

    public void setCachePeriod(long cachePeriod) {
        this.cachePeriod = cachePeriod;
    }

    public AdParams(int slotId) {
        this.slotId = 0;
        this.formats = new ArrayList();
        this.cachePeriod = 86400000;
        this.slotId = slotId;
    }

    public boolean addFormat(String format) {
        if (this.formats.contains(format)) {
            return false;
        }
        this.formats.add(format);
        return true;
    }

    public boolean isCheckExclude() {
        return this.checkExclude;
    }

    public void setCheckExclude(boolean checkExclude) {
        this.checkExclude = checkExclude;
    }
}
