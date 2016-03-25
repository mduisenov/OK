package ru.ok.model.events;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import ru.ok.model.events.OdnkEvent.EventType;

public class DiscussionOdklEvent extends OdnkEvent {
    public static final Creator<DiscussionOdklEvent> CREATOR;
    public final String valueLike;
    public final String valueReplay;

    /* renamed from: ru.ok.model.events.DiscussionOdklEvent.1 */
    static class C15221 implements Creator<DiscussionOdklEvent> {
        C15221() {
        }

        public DiscussionOdklEvent createFromParcel(Parcel parcel) {
            return new DiscussionOdklEvent(parcel);
        }

        public DiscussionOdklEvent[] newArray(int count) {
            return new DiscussionOdklEvent[count];
        }
    }

    public DiscussionOdklEvent(String uid, String value, String valueLike, String valueReplay, long lastId, long requestTime) {
        super(uid, value, EventType.DISCUSSIONS, lastId, requestTime);
        this.valueLike = valueLike;
        this.valueReplay = valueReplay;
    }

    public DiscussionOdklEvent(Parcel parcel) {
        super(parcel);
        this.valueLike = parcel.readString();
        this.valueReplay = parcel.readString();
    }

    public int getIntValueLike() {
        return Integer.parseInt(this.valueLike);
    }

    public int getIntValueReply() {
        return Integer.parseInt(this.valueReplay);
    }

    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(this.valueLike);
        parcel.writeString(this.valueReplay);
    }

    static {
        CREATOR = new C15221();
    }

    public boolean equals(Object o) {
        if (!(o instanceof DiscussionOdklEvent)) {
            return false;
        }
        DiscussionOdklEvent event = (DiscussionOdklEvent) o;
        if (this.lastId == event.lastId && this.value.equals(event.value) && this.type == event.type && this.uid.equals(event.uid) && this.valueLike.equals(event.valueLike) && this.valueReplay.equals(event.valueReplay)) {
            return true;
        }
        return false;
    }
}
