package ru.ok.model.stream;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;

public final class EntityReference implements Parcelable {
    public static final Creator<EntityReference> CREATOR;
    public final String id;
    public final int type;
    public final String unknownType;

    /* renamed from: ru.ok.model.stream.EntityReference.1 */
    static class C15961 implements Creator<EntityReference> {
        C15961() {
        }

        public EntityReference createFromParcel(Parcel source) {
            try {
                return new EntityReference(null);
            } catch (UnParcelException e) {
                return null;
            }
        }

        public EntityReference[] newArray(int size) {
            return new EntityReference[size];
        }
    }

    public EntityReference(int type, String id) {
        this.type = type;
        this.id = id;
        this.unknownType = null;
    }

    public EntityReference(String unknownType, String id) {
        this.type = 999;
        this.id = id;
        this.unknownType = unknownType;
    }

    public String toString() {
        return "Ref{" + this.type + (this.type == 999 ? "(" + this.unknownType + ")" : "") + ":" + this.id + "}";
    }

    public boolean equals(Object o) {
        if (!(o instanceof EntityReference)) {
            return false;
        }
        EntityReference other = (EntityReference) o;
        if (this.type == other.type && TextUtils.equals(this.id, other.id) && TextUtils.equals(this.unknownType, other.unknownType)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int hashCode = 0 + (1403932609 * this.type);
        if (this.id != null) {
            hashCode += 888041617 * this.id.hashCode();
        }
        if (this.unknownType != null) {
            return hashCode + (721237157 * this.unknownType.hashCode());
        }
        return hashCode;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeInt(this.type);
        dest.writeString(this.unknownType);
    }

    private EntityReference(Parcel src) throws UnParcelException {
        String id = src.readString();
        this.type = src.readInt();
        this.unknownType = src.readString();
        if (id == null) {
            throw new UnParcelException("Incompatible change: read id=null from parcel");
        }
        this.id = id;
    }

    static {
        CREATOR = new C15961();
    }
}
