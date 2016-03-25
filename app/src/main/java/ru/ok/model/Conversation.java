package ru.ok.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;

public final class Conversation implements Parcelable, Comparable<Conversation> {
    public static final Creator<Conversation> CREATOR;
    public final ConversationCapabilities capabilities;
    public final String id;
    public final String lastAuthorId;
    public final String lastMessage;
    public final long lastMsgTime;
    public final long lastViewTime;
    public final int newMessagesCount;
    public final String ownerId;
    public final ArrayList<ConversationParticipant> participants;
    public final String topic;
    public final Type type;

    /* renamed from: ru.ok.model.Conversation.1 */
    static class C15081 implements Creator<Conversation> {
        C15081() {
        }

        public Conversation createFromParcel(Parcel parcel) {
            String id = parcel.readString();
            String title = parcel.readString();
            String type = parcel.readString();
            String ownerId = parcel.readString();
            long lastMsgTime = parcel.readLong();
            long lastViewTime = parcel.readLong();
            int newMessagesCount = parcel.readInt();
            String lastMessage = parcel.readString();
            String lastAuthorId = parcel.readString();
            ArrayList<ConversationParticipant> participants = new ArrayList();
            parcel.readTypedList(participants, ConversationParticipant.CREATOR);
            ConversationCapabilities capabilities = (ConversationCapabilities) parcel.readParcelable(ConversationCapabilities.class.getClassLoader());
            return new Conversation(id, title, Type.valueOf(type), ownerId, lastMsgTime, lastViewTime, newMessagesCount, lastMessage, lastAuthorId, participants, capabilities == null ? ConversationCapabilities.DEFAULT_CAPABILITIES : capabilities);
        }

        public Conversation[] newArray(int count) {
            return new Conversation[count];
        }
    }

    public enum Type {
        PRIVATE,
        CHAT
    }

    static {
        CREATOR = new C15081();
    }

    public Conversation(String id, String topic, Type type, String ownerId, long lastMsgTime, long lastViewTime, int newMessagesCount, String lastMessage, String lastAuthorId, ArrayList<ConversationParticipant> participants, ConversationCapabilities capabilities) {
        this.id = id;
        this.topic = topic;
        this.type = type;
        this.ownerId = ownerId;
        this.lastMsgTime = lastMsgTime;
        this.lastViewTime = lastViewTime;
        this.newMessagesCount = newMessagesCount;
        this.lastMessage = lastMessage;
        this.lastAuthorId = lastAuthorId;
        if (participants == null) {
            participants = new ArrayList();
        }
        this.participants = participants;
        this.capabilities = capabilities;
    }

    public void addParticipant(ConversationParticipant participant) {
        if (!this.participants.contains(participant)) {
            this.participants.add(participant);
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.topic);
        dest.writeString(this.type.name());
        dest.writeString(this.ownerId);
        dest.writeLong(this.lastMsgTime);
        dest.writeLong(this.lastViewTime);
        dest.writeInt(this.newMessagesCount);
        dest.writeString(this.lastMessage);
        dest.writeString(this.lastAuthorId);
        dest.writeTypedList(this.participants);
        dest.writeParcelable(this.capabilities, flags);
    }

    public int compareTo(Conversation another) {
        if (this.newMessagesCount > 0 && another.newMessagesCount <= 0) {
            return -1;
        }
        if (this.newMessagesCount <= 0 && another.newMessagesCount > 0) {
            return 1;
        }
        if (another.lastMsgTime >= this.lastMsgTime) {
            return another.lastMsgTime > this.lastMsgTime ? 1 : 0;
        } else {
            return -1;
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Conversation that = (Conversation) o;
        if (this.id != null) {
            return this.id.equals(that.id);
        }
        if (that.id != null) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.id != null ? this.id.hashCode() : 0;
    }

    public String toString() {
        return "Conversation{id='" + this.id + '\'' + ", topic='" + this.topic + '\'' + ", lastMessage='" + this.lastMessage + '\'' + '}';
    }
}
