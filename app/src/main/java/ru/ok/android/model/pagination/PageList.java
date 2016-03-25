package ru.ok.android.model.pagination;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.java.api.request.paging.PagingDirection;

public class PageList<T> implements Parcelable {
    public static final Creator<PageList> CREATOR;
    private boolean cacheValid;
    private int elementCount;
    protected final List<Page<T>> pages;

    /* renamed from: ru.ok.android.model.pagination.PageList.1 */
    static class C03721 implements Creator<PageList> {
        C03721() {
        }

        public PageList createFromParcel(Parcel source) {
            return new PageList(source);
        }

        public PageList[] newArray(int size) {
            return new PageList[size];
        }
    }

    /* renamed from: ru.ok.android.model.pagination.PageList.2 */
    static /* synthetic */ class C03732 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$java$api$request$paging$PagingDirection;

        static {
            $SwitchMap$ru$ok$java$api$request$paging$PagingDirection = new int[PagingDirection.values().length];
            try {
                $SwitchMap$ru$ok$java$api$request$paging$PagingDirection[PagingDirection.BACKWARD.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$java$api$request$paging$PagingDirection[PagingDirection.FORWARD.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public PageList() {
        this.pages = new ArrayList();
    }

    protected PageList(Parcel source) {
        this.pages = new ArrayList();
        int count = source.readInt();
        ClassLoader cl = PageList.class.getClassLoader();
        for (int i = 0; i < count; i++) {
            this.pages.add((Page) source.readParcelable(cl));
        }
    }

    public final void addPage(@NonNull Page<T> page, @Nullable String anchor, @NonNull PagingDirection direction) {
        int pageLocation = findPageInsertLocation(anchor, direction);
        if (pageLocation != -1) {
            doAddPage(pageLocation, page, direction);
        }
    }

    protected void doAddPage(int location, @NonNull Page<T> page, @NonNull PagingDirection direction) {
        addPage(location, page);
    }

    private int findPageInsertLocation(@Nullable String anchor, @NonNull PagingDirection direction) {
        if (getPageCount() == 0) {
            return 0;
        }
        switch (C03732.$SwitchMap$ru$ok$java$api$request$paging$PagingDirection[direction.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return findPageInsertLocationInBackwardDirection(anchor);
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return findPageInsertLocationInForwardDirection(anchor);
            default:
                return -1;
        }
    }

    protected int findPageInsertLocationInBackwardDirection(@Nullable String anchor) {
        return 0;
    }

    protected int findPageInsertLocationInForwardDirection(@Nullable String anchor) {
        return getPageCount();
    }

    public final void addPage(int location, @NonNull Page<T> page) {
        this.pages.add(location, page);
        invalidateCache();
    }

    @NonNull
    public Page<T> removePage(int location) {
        Page<T> page = (Page) this.pages.remove(location);
        invalidateCache();
        return page;
    }

    @NonNull
    public Page<T> getPage(int location) {
        return (Page) this.pages.get(location);
    }

    public void clear() {
        this.pages.clear();
        invalidateCache();
    }

    public int getPageCount() {
        return this.pages.size();
    }

    public boolean isEmpty() {
        return this.pages.isEmpty();
    }

    @Nullable
    public T getElement(int position) {
        int start = 0;
        for (Page<T> page : this.pages) {
            int end = start + page.getCount();
            if (position < start || position >= end) {
                start = end;
            } else {
                int offset = position - start;
                if (offset < 0) {
                    return null;
                }
                return page.getElements().get(offset);
            }
        }
        return null;
    }

    public void addFirstPage(@NonNull Page<T> page) {
        addPage(0, page);
    }

    public void addLastPage(@NonNull Page<T> page) {
        addPage(getPageCount(), page);
    }

    public int getOffsetInPage(int position) {
        int start = 0;
        for (Page<T> page : this.pages) {
            int end = start + page.getCount();
            if (position >= start && position < end) {
                return position - start;
            }
            start = end;
        }
        return -1;
    }

    public int getPageLocationForPosition(int position) {
        int start = 0;
        int n = this.pages.size();
        for (int i = 0; i < n; i++) {
            int end = start + ((Page) this.pages.get(i)).getCount();
            if (position >= start && position < end) {
                return i;
            }
            start = end;
        }
        return -1;
    }

    @Nullable
    public Page<T> getPageForPosition(int position) {
        int start = 0;
        int n = this.pages.size();
        for (int i = 0; i < n; i++) {
            Page<T> page = (Page) this.pages.get(i);
            int end = start + page.getCount();
            if (position >= start && position < end) {
                return page;
            }
            start = end;
        }
        return null;
    }

    public int getStartPositionForPage(int pageLocation) {
        int position = 0;
        for (int i = 0; i < pageLocation; i++) {
            position += ((Page) this.pages.get(i)).getCount();
        }
        return position;
    }

    @NonNull
    public List<T> getAllElements() {
        List<T> elements = new ArrayList();
        for (Page<T> page : this.pages) {
            elements.addAll(page.getElements());
        }
        return elements;
    }

    public int getElementCount() {
        ensureCacheValid();
        return this.elementCount;
    }

    @Nullable
    public Page<T> getPageForElement(@NonNull T element) {
        for (Page<T> page : this.pages) {
            if (page.contains(element)) {
                return page;
            }
        }
        return null;
    }

    protected void invalidateCache() {
        this.cacheValid = false;
    }

    protected void ensureCacheValid() {
        if (!this.cacheValid) {
            this.elementCount = 0;
            for (Page<T> page : this.pages) {
                this.elementCount += page.getCount();
            }
            this.cacheValid = true;
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.pages.size());
        for (Page<T> page : this.pages) {
            dest.writeParcelable(page, flags);
        }
    }

    static {
        CREATOR = new C03721();
    }
}
