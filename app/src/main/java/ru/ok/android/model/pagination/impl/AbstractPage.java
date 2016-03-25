package ru.ok.android.model.pagination.impl;

import android.os.Parcel;
import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.model.pagination.Page;
import ru.ok.android.model.pagination.Page.Id;
import ru.ok.android.model.pagination.PageAnchor;

public abstract class AbstractPage<T> implements Page<T> {
    protected final PageAnchor anchor;
    protected final List<T> elementList;
    protected final Id id;

    public AbstractPage(@NonNull List<T> elementList, @NonNull PageAnchor anchor) {
        this.elementList = new ArrayList(elementList);
        this.anchor = anchor;
        this.id = createId();
    }

    protected AbstractPage(Parcel source) {
        ClassLoader cl = AbstractPage.class.getClassLoader();
        this.elementList = source.readArrayList(cl);
        this.anchor = (PageAnchor) source.readParcelable(cl);
        this.id = new Id(source.readString());
    }

    @NonNull
    protected Id createId() {
        return new Id(String.format("%s/%s", new Object[]{this.anchor.getBackwardAnchor(), this.anchor.getForwardAnchor()}));
    }

    @NonNull
    public Id getId() {
        return this.id;
    }

    @NonNull
    public PageAnchor getAnchor() {
        return this.anchor;
    }

    public int getCount() {
        return this.elementList.size();
    }

    @NonNull
    public List<T> getElements() {
        return this.elementList;
    }

    public int getElementOffset(@NonNull T element) {
        return this.elementList.indexOf(element);
    }

    public boolean contains(@NonNull T element) {
        return this.elementList.contains(element);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.elementList);
        dest.writeParcelable(this.anchor, flags);
        dest.writeString(this.id.getId());
    }
}
