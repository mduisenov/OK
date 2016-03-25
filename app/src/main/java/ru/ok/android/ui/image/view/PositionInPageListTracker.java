package ru.ok.android.ui.image.view;

import android.support.annotation.NonNull;
import ru.ok.android.model.pagination.Page;
import ru.ok.android.model.pagination.Page.Id;
import ru.ok.android.model.pagination.PageList;

public class PositionInPageListTracker {
    private final Callbacks callbacks;
    private int offsetInPagePosition;
    private Id pageId;
    private int position;

    public interface Callbacks<T> {
        @NonNull
        Id getPageIdForPosition(int i);

        @NonNull
        PageList<T> getPageList();

        int getTrackPositionOffsetAfterRemovingPageItem(int i);
    }

    public PositionInPageListTracker(@NonNull Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    public void initialize(int position) {
        this.position = position;
        this.pageId = this.callbacks.getPageIdForPosition(position);
        this.offsetInPagePosition = this.callbacks.getPageList().getOffsetInPage(position);
    }

    public int getNewPositionAfterAddingPage() {
        PageList<?> pageList = this.callbacks.getPageList();
        int start = 0;
        int n = pageList.getPageCount();
        for (int i = 0; i < n; i++) {
            Page<?> page = pageList.getPage(i);
            if (page.getId().equals(this.pageId)) {
                return this.offsetInPagePosition + start;
            }
            start += page.getCount();
        }
        return -1;
    }

    public int getNewPositionAfterRemovingPageItem(boolean itemRemoved) {
        if (!itemRemoved) {
            return this.position;
        }
        int itemCount = this.callbacks.getPageList().getElementCount();
        if (itemCount == 0) {
            return -1;
        }
        int trackPosition;
        if (itemCount == this.position) {
            trackPosition = this.position - 1;
        } else {
            trackPosition = this.position;
        }
        return trackPosition + this.callbacks.getTrackPositionOffsetAfterRemovingPageItem(trackPosition);
    }
}
