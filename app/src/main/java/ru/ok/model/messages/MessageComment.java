package ru.ok.model.messages;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.List;
import ru.ok.model.messages.MessageBase.Flags;
import ru.ok.model.messages.MessageBase.MessageBaseBuilder;
import ru.ok.model.messages.MessageBase.RepliedTo;
import ru.ok.model.stickers.Sticker;
import ru.ok.model.stream.LikeInfo;

public final class MessageComment extends MessageBase {
    public static final Creator<MessageComment> CREATOR;
    public final String messageType;

    /* renamed from: ru.ok.model.messages.MessageComment.1 */
    static class C15451 implements Creator<MessageComment> {
        C15451() {
        }

        public MessageComment createFromParcel(Parcel source) {
            return new MessageComment(source);
        }

        public MessageComment[] newArray(int size) {
            return new MessageComment[size];
        }
    }

    public static class MessageCommentBuilder extends MessageBaseBuilder<MessageComment> {
        private String type;

        public MessageCommentBuilder setType(String type) {
            this.type = type;
            return this;
        }

        public MessageComment build() {
            return new MessageComment(this.id, this.text, this.textEdited, this.type, this.authorId, this.authorType, this.date, this.dateEdited, this.likeInfo, this.flags, this.repliedToInfo, this.attachments, this.mediaMetadata, this.replyStickers);
        }
    }

    public MessageComment(String id, String text, String textEdited, String messageType, String authorId, String authorType, long date, long dateEdited, LikeInfo likeInfo, Flags flags, RepliedTo repliedToInfo, Attachment[] attachments, String mediaMetadata, List<Sticker> replyStickers) {
        super(id, text, textEdited, authorId, authorType, date, dateEdited, likeInfo, flags, repliedToInfo, attachments, mediaMetadata, null, replyStickers);
        this.messageType = messageType;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.messageType);
    }

    protected MessageComment(Parcel src) {
        super(src);
        this.messageType = src.readString();
    }

    static {
        CREATOR = new C15451();
    }
}
