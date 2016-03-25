package ru.ok.model.events;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class OdnkEvent implements Parcelable {
    public static final Creator<OdnkEvent> CREATOR;
    private transient boolean hasParsedInt;
    private transient int intValue;
    private transient boolean isValueInt;
    public final long lastId;
    public final long requestTime;
    public final EventType type;
    public final String uid;
    public final String value;

    /* renamed from: ru.ok.model.events.OdnkEvent.1 */
    static class C15251 implements Creator<OdnkEvent> {
        C15251() {
        }

        public OdnkEvent createFromParcel(Parcel parcel) {
            return new OdnkEvent(parcel);
        }

        public OdnkEvent[] newArray(int count) {
            return new OdnkEvent[count];
        }
    }

    public enum EventType {
        MESSAGES,
        MARKS,
        ACTIVITIES,
        DISCUSSIONS,
        GUESTS,
        EVENTS,
        EVENTS_TOTAL,
        LOCALE,
        FRIENDS,
        FRIENDS_ONLINE,
        UPLOAD_PHOTO,
        GROUPS,
        HOLIDAYS
    }

    public OdnkEvent(String uid, String value, EventType type, long lastId, long requestTime) {
        this.uid = uid;
        this.value = value;
        this.type = type;
        this.lastId = lastId;
        this.requestTime = requestTime;
    }

    public OdnkEvent(Parcel parcel) {
        this.uid = parcel.readString();
        this.value = parcel.readString();
        this.type = EventType.valueOf(parcel.readString());
        this.lastId = parcel.readLong();
        this.requestTime = parcel.readLong();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.uid);
        parcel.writeString(this.value);
        parcel.writeString(this.type.name());
        parcel.writeLong(this.lastId);
        parcel.writeLong(this.requestTime);
    }

    static {
        CREATOR = new C15251();
    }

    public boolean isValueInt() {
        if (!this.hasParsedInt) {
            try {
                this.intValue = Integer.parseInt(this.value);
                this.isValueInt = true;
            } catch (Exception e) {
                this.isValueInt = false;
            }
            this.hasParsedInt = true;
        }
        return this.isValueInt;
    }

    public int getValueInt() {
        if (isValueInt()) {
            return this.intValue;
        }
        return 0;
    }

    public static EventType getTypeFromString(String typeString) {
        if (typeString.equals("conversations")) {
            return EventType.MESSAGES;
        }
        if (typeString.equals("guests")) {
            return EventType.GUESTS;
        }
        if (typeString.equals("activities")) {
            return EventType.ACTIVITIES;
        }
        if (typeString.equals("marks")) {
            return EventType.MARKS;
        }
        if (typeString.equals("discussions")) {
            return EventType.DISCUSSIONS;
        }
        if (typeString.equals("notifs_unread")) {
            return EventType.EVENTS;
        }
        if (typeString.equals("notifications")) {
            return EventType.EVENTS_TOTAL;
        }
        return EventType.DISCUSSIONS;
    }

    public boolean equals(Object o) {
        if (!(o instanceof OdnkEvent)) {
            return false;
        }
        OdnkEvent event = (OdnkEvent) o;
        if (this.lastId == event.lastId && this.value.equals(event.value) && this.type == event.type && this.uid.equals(event.uid)) {
            return true;
        }
        return false;
    }

    public String toString() {
        return "OdnkEvent{type=" + this.type + ", value='" + this.value + '\'' + '}';
    }
}
