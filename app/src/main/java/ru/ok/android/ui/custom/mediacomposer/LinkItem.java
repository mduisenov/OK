package ru.ok.android.ui.custom.mediacomposer;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import ru.ok.model.mediatopics.MediaItemType;

public class LinkItem extends MediaItem {
    public static final Creator<LinkItem> CREATOR;
    private static final long serialVersionUID = 1;
    private final String linkUrl;

    /* renamed from: ru.ok.android.ui.custom.mediacomposer.LinkItem.1 */
    static class C06701 implements Creator<LinkItem> {
        C06701() {
        }

        public LinkItem createFromParcel(Parcel source) {
            return new LinkItem(source);
        }

        public LinkItem[] newArray(int size) {
            return new LinkItem[size];
        }
    }

    public LinkItem(String linkUrl) {
        super(MediaItemType.LINK);
        this.linkUrl = linkUrl;
    }

    LinkItem(Parcel source) {
        super(MediaItemType.LINK, source);
        this.linkUrl = source.readString();
    }

    public String getLinkUrl() {
        return this.linkUrl;
    }

    public String getSampleText() {
        return this.linkUrl;
    }

    public boolean isEmpty() {
        return TextUtils.isEmpty(this.linkUrl);
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.linkUrl);
    }

    static {
        CREATOR = new C06701();
    }
}
