package ru.ok.android.model.pagination.impl;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import ru.ok.android.model.pagination.Page;
import ru.ok.android.model.pagination.PageList;

public class CircularPageList<T> extends PageList<T> {
    public static final Creator<CircularPageList> CREATOR;

    /* renamed from: ru.ok.android.model.pagination.impl.CircularPageList.1 */
    static class C03741 implements Creator<CircularPageList> {
        C03741() {
        }

        public CircularPageList createFromParcel(Parcel source) {
            return new CircularPageList(source);
        }

        public CircularPageList[] newArray(int size) {
            return new CircularPageList[size];
        }
    }

    protected CircularPageList(Parcel source) {
        super(source);
    }

    protected int findPageInsertLocationInBackwardDirection(@Nullable String anchor) {
        return anchor == null ? getPageCount() : findPageInsertLocationByAnchorInBackwardDirection(anchor);
    }

    private int findPageInsertLocationByAnchorInBackwardDirection(@NonNull String anchor) {
        for (int i = getPageCount() - 1; i >= 0; i--) {
            if (compareToPageBackwardAnchor(anchor, (Page) this.pages.get(i))) {
                return i;
            }
        }
        return -1;
    }

    protected boolean compareToPageBackwardAnchor(@NonNull String anchor, @NonNull Page<T> page) {
        return page.getAnchor().getBackwardAnchor().equals(anchor);
    }

    protected int findPageInsertLocationInForwardDirection(@Nullable String anchor) {
        return anchor == null ? 0 : findPageInsertLocationByAnchorInForwardDirection(anchor);
    }

    protected int findPageInsertLocationByAnchorInForwardDirection(@NonNull String anchor) {
        int pageCount = getPageCount();
        for (int i = 0; i < pageCount; i++) {
            if (compareToPageForwardAnchor(anchor, (Page) this.pages.get(i))) {
                return i + 1;
            }
        }
        return -1;
    }

    protected boolean compareToPageForwardAnchor(@NonNull String anchor, @NonNull Page<T> page) {
        return page.getAnchor().getForwardAnchor().equals(anchor);
    }

    static {
        CREATOR = new C03741();
    }
}
