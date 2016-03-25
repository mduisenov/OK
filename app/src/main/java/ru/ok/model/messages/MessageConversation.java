package ru.ok.model.messages;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.util.List;
import ru.ok.model.messages.MessageBase.Flags;
import ru.ok.model.messages.MessageBase.MessageBaseBuilder;
import ru.ok.model.messages.MessageBase.RepliedTo;
import ru.ok.model.stickers.Sticker;
import ru.ok.model.stream.LikeInfo;

public final class MessageConversation extends MessageBase {
    public static final Creator<MessageConversation> CREATOR;
    public final String conversationId;
    public final Type type;

    /* renamed from: ru.ok.model.messages.MessageConversation.1 */
    static class C15461 implements Creator<MessageConversation> {
        C15461() {
        }

        public MessageConversation createFromParcel(Parcel source) {
            return new MessageConversation(source);
        }

        public MessageConversation[] newArray(int size) {
            return new MessageConversation[size];
        }
    }

    public static class MessageConversationBuilder extends MessageBaseBuilder<MessageConversation> {
        private String conversationId;
        private Integer taskId;
        private Type type;

        public MessageConversationBuilder setType(Type type) {
            this.type = type;
            return this;
        }

        public MessageConversationBuilder setConversationId(String conversationId) {
            this.conversationId = conversationId;
            return this;
        }

        public MessageConversation build() {
            return new MessageConversation(this.id, this.text, this.textEdited, this.type, this.authorId, this.authorType, this.date, this.dateEdited, this.likeInfo, this.flags, this.repliedToInfo, this.mediaMetadata, this.attachments, this.taskId, this.conversationId, this.replyStickers);
        }
    }

    public enum Type {
        SYSTEM,
        STICKER,
        USER;

        public static Type safeValueOf(String messageType) {
            for (Type type : values()) {
                if (TextUtils.equals(type.name(), messageType)) {
                    return type;
                }
            }
            return SYSTEM;
        }
    }

    public MessageConversation(String id, String text, String textEdited, Type messageType, String authorId, String authorType, long date, long dateEdited, LikeInfo likeInfo, Flags flags, RepliedTo repliedToInfo, String mediaMetadata, Attachment[] attachments, Integer taskId, String conversationId, List<Sticker> replyStickers) {
        super(id, text, textEdited, authorId, authorType, date, dateEdited, likeInfo, flags, repliedToInfo, attachments, mediaMetadata, taskId, replyStickers);
        this.type = messageType;
        this.conversationId = conversationId;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.type != null ? this.type.ordinal() : -1);
        dest.writeString(this.conversationId);
    }

    protected MessageConversation(Parcel src) {
        super(src);
        int ordinal = src.readInt();
        this.type = ordinal == -1 ? null : Type.values()[ordinal];
        this.conversationId = src.readString();
    }

    static {
        CREATOR = new C15461();
    }
}
