package ru.ok.android.ui.quickactions;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class ActionItem implements Parcelable {
    public static final Creator<ActionItem> CREATOR;
    private final int actionId;
    private final int iconResourceId;
    private boolean selected;
    private final boolean sticky;
    private final int titleResourceId;

    /* renamed from: ru.ok.android.ui.quickactions.ActionItem.1 */
    static class C11761 implements Creator<ActionItem> {
        C11761() {
        }

        public ActionItem createFromParcel(Parcel source) {
            return new ActionItem(source);
        }

        public ActionItem[] newArray(int size) {
            return new ActionItem[size];
        }
    }

    public ActionItem(int actionId, int titleResourceId, int iconResourceId) {
        this.titleResourceId = titleResourceId;
        this.iconResourceId = iconResourceId;
        this.actionId = actionId;
        this.sticky = false;
    }

    public ActionItem(int actionId, int titleResourceId) {
        this(actionId, titleResourceId, 0);
    }

    public int getTitleResourceId() {
        return this.titleResourceId;
    }

    public int getIconResourceId() {
        return this.iconResourceId;
    }

    public int getActionId() {
        return this.actionId;
    }

    public boolean isSticky() {
        return this.sticky;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        int i;
        int i2 = 1;
        dest.writeInt(this.iconResourceId);
        dest.writeInt(this.titleResourceId);
        dest.writeInt(this.actionId);
        if (this.selected) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeInt(i);
        if (!this.sticky) {
            i2 = 0;
        }
        dest.writeInt(i2);
    }

    protected ActionItem(Parcel src) {
        boolean z;
        boolean z2 = true;
        this.iconResourceId = src.readInt();
        this.titleResourceId = src.readInt();
        this.actionId = src.readInt();
        if (src.readInt() != 0) {
            z = true;
        } else {
            z = false;
        }
        this.selected = z;
        if (src.readInt() == 0) {
            z2 = false;
        }
        this.sticky = z2;
    }

    static {
        CREATOR = new C11761();
    }
}
