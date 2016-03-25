package ru.ok.model.messages;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import ru.ok.android.utils.Logger;
import ru.ok.model.stickers.Sticker;
import ru.ok.model.stream.LikeInfo;

public abstract class MessageBase implements Parcelable {
    public static final Comparator<? super MessageBase> COMPARATOR_DATE;
    public Attachment[] attachments;
    public final String authorId;
    public final String authorType;
    public final long date;
    public final long dateEdited;
    public final Flags flags;
    public final String id;
    public final LikeInfo likeInfo;
    public final String mediaMetadata;
    public final RepliedTo repliedToInfo;
    public final List<Sticker> replyStickers;
    public final Integer taskId;
    public final String text;
    public final String textEdited;

    /* renamed from: ru.ok.model.messages.MessageBase.1 */
    static class C15421 implements Comparator<MessageBase> {
        C15421() {
        }

        public int compare(MessageBase a, MessageBase b) {
            if (a.date < b.date) {
                return -1;
            }
            return a.date > b.date ? 1 : 0;
        }
    }

    public static final class Flags implements Parcelable, Serializable {
        public static final Creator<Flags> CREATOR;
        private static final long serialVersionUID = 1;
        public final boolean blockAllowed;
        public final boolean deletionAllowed;
        public final boolean editDisabled;
        public final boolean likeAllowed;
        public final boolean likesUnread;
        public final boolean markAsSpamAllowed;
        public final boolean repliesUnread;

        /* renamed from: ru.ok.model.messages.MessageBase.Flags.1 */
        static class C15431 implements Creator<Flags> {
            C15431() {
            }

            public Flags createFromParcel(Parcel source) {
                boolean z = true;
                boolean z2 = source.readInt() > 0;
                boolean z3 = source.readInt() > 0;
                boolean z4 = source.readInt() > 0;
                boolean z5 = source.readInt() > 0;
                boolean z6 = source.readInt() > 0;
                boolean z7 = source.readInt() > 0;
                if (source.readInt() <= 0) {
                    z = false;
                }
                return new Flags(z2, z3, z4, z5, z6, z7, z);
            }

            public Flags[] newArray(int size) {
                return new Flags[size];
            }
        }

        public Flags(boolean likeAllowed, boolean markAsSpamAllowed, boolean deletionAllowed, boolean blockAllowed, boolean likesUnread, boolean repliesUnread, boolean editDisabled) {
            this.likeAllowed = likeAllowed;
            this.markAsSpamAllowed = markAsSpamAllowed;
            this.deletionAllowed = deletionAllowed;
            this.blockAllowed = blockAllowed;
            this.likesUnread = likesUnread;
            this.repliesUnread = repliesUnread;
            this.editDisabled = editDisabled;
        }

        public static Flags create(String[] chunks) {
            boolean z = true;
            boolean z2 = Arrays.binarySearch(chunks, "l") >= 0;
            boolean z3 = Arrays.binarySearch(chunks, "s") >= 0;
            boolean z4 = Arrays.binarySearch(chunks, Logger.METHOD_D) >= 0;
            boolean z5 = Arrays.binarySearch(chunks, "b") >= 0;
            boolean z6 = Arrays.binarySearch(chunks, "lu") >= 0;
            boolean z7 = Arrays.binarySearch(chunks, "ru") >= 0;
            if (Arrays.binarySearch(chunks, "ed") < 0) {
                z = false;
            }
            return new Flags(z2, z3, z4, z5, z6, z7, z);
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            int i;
            int i2 = 1;
            dest.writeInt(this.likeAllowed ? 1 : 0);
            if (this.markAsSpamAllowed) {
                i = 1;
            } else {
                i = 0;
            }
            dest.writeInt(i);
            if (this.deletionAllowed) {
                i = 1;
            } else {
                i = 0;
            }
            dest.writeInt(i);
            if (this.blockAllowed) {
                i = 1;
            } else {
                i = 0;
            }
            dest.writeInt(i);
            if (this.likesUnread) {
                i = 1;
            } else {
                i = 0;
            }
            dest.writeInt(i);
            if (this.repliesUnread) {
                i = 1;
            } else {
                i = 0;
            }
            dest.writeInt(i);
            if (!this.editDisabled) {
                i2 = 0;
            }
            dest.writeInt(i2);
        }

        static {
            CREATOR = new C15431();
        }
    }

    public static abstract class MessageBaseBuilder<T extends MessageBase> {
        protected Attachment[] attachments;
        protected String authorId;
        protected String authorType;
        protected long date;
        protected long dateEdited;
        protected Flags flags;
        protected String id;
        protected LikeInfo likeInfo;
        protected String mediaMetadata;
        protected RepliedTo repliedToInfo;
        protected List<Sticker> replyStickers;
        protected String text;
        protected String textEdited;

        public abstract T build();

        public MessageBaseBuilder setAttachments(Attachment[] attachments) {
            this.attachments = attachments;
            return this;
        }

        public MessageBaseBuilder setId(String id) {
            this.id = id;
            return this;
        }

        public MessageBaseBuilder setText(String text) {
            this.text = text;
            return this;
        }

        public MessageBaseBuilder setTextEdited(String textEdited) {
            this.textEdited = textEdited;
            return this;
        }

        public MessageBaseBuilder setAuthorId(String authorId) {
            this.authorId = authorId;
            return this;
        }

        public MessageBaseBuilder setAuthorType(String authorType) {
            this.authorType = authorType;
            return this;
        }

        public MessageBaseBuilder setDate(long date) {
            this.date = date;
            return this;
        }

        public MessageBaseBuilder setDateEdited(long dateEdited) {
            this.dateEdited = dateEdited;
            return this;
        }

        public MessageBaseBuilder setLikeInfo(LikeInfo likeInfo) {
            this.likeInfo = likeInfo;
            return this;
        }

        public MessageBaseBuilder setFlags(Flags flags) {
            this.flags = flags;
            return this;
        }

        public MessageBaseBuilder setRepliedTo(RepliedTo repliedTo) {
            this.repliedToInfo = repliedTo;
            return this;
        }

        public MessageBaseBuilder setMediaMetadata(String mediaMetadata) {
            this.mediaMetadata = mediaMetadata;
            return this;
        }

        public MessageBaseBuilder<T> setReplyStickers(List<Sticker> replyStickers) {
            this.replyStickers = replyStickers;
            return this;
        }
    }

    public static final class RepliedTo implements Parcelable {
        public static final Creator<RepliedTo> CREATOR;
        public final String authorId;
        public final String authorType;
        public final String messageId;

        /* renamed from: ru.ok.model.messages.MessageBase.RepliedTo.1 */
        static class C15441 implements Creator<RepliedTo> {
            C15441() {
            }

            public RepliedTo createFromParcel(Parcel source) {
                return new RepliedTo(source.readString(), source.readString(), source.readString());
            }

            public RepliedTo[] newArray(int size) {
                return new RepliedTo[size];
            }
        }

        public RepliedTo(String messageId, String authorId, String authorType) {
            this.messageId = messageId;
            this.authorId = authorId;
            this.authorType = authorType;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.messageId);
            dest.writeString(this.authorId);
            dest.writeString(this.authorType);
        }

        static {
            CREATOR = new C15441();
        }
    }

    static {
        COMPARATOR_DATE = new C15421();
    }

    public MessageBase(String id, String text, String textEdited, String authorId, String authorType, long date, long dateEdited, LikeInfo likeInfo, Flags flags, RepliedTo repliedToInfo, Attachment[] attachments, String mediaMetadata, Integer taskId, List<Sticker> replyStickers) {
        this.id = id;
        this.text = text;
        this.textEdited = textEdited;
        this.authorId = authorId;
        this.authorType = authorType;
        this.date = date;
        this.dateEdited = dateEdited;
        this.likeInfo = likeInfo;
        this.flags = flags;
        this.repliedToInfo = repliedToInfo;
        this.attachments = attachments;
        this.mediaMetadata = mediaMetadata;
        this.taskId = taskId;
        if (replyStickers == null) {
            replyStickers = Collections.emptyList();
        }
        this.replyStickers = replyStickers;
    }

    public void writeToParcel(Parcel dest, int flags) {
        int attachmentsCount;
        int stickersCount;
        dest.writeString(this.id);
        dest.writeString(this.text);
        dest.writeString(this.textEdited);
        dest.writeString(this.authorId);
        dest.writeString(this.authorType);
        dest.writeLong(this.date);
        dest.writeLong(this.dateEdited);
        dest.writeParcelable(this.likeInfo, flags);
        dest.writeParcelable(this.flags, flags);
        dest.writeParcelable(this.repliedToInfo, flags);
        if (this.attachments != null) {
            attachmentsCount = this.attachments.length;
        } else {
            attachmentsCount = 0;
        }
        dest.writeInt(attachmentsCount);
        if (attachmentsCount > 0) {
            dest.writeTypedArray(this.attachments, flags);
        }
        dest.writeString(this.mediaMetadata);
        dest.writeInt(this.taskId != null ? this.taskId.intValue() : -1);
        if (this.replyStickers != null) {
            stickersCount = this.replyStickers.size();
        } else {
            stickersCount = 0;
        }
        dest.writeInt(stickersCount);
        for (int i = 0; i < stickersCount; i++) {
            Sticker sticker = (Sticker) this.replyStickers.get(i);
            dest.writeString(sticker.code);
            dest.writeInt(sticker.price);
            dest.writeInt(sticker.width);
            dest.writeInt(sticker.height);
        }
    }

    protected MessageBase(Parcel src) {
        List list = null;
        ClassLoader cl = MessageBase.class.getClassLoader();
        this.id = src.readString();
        this.text = src.readString();
        this.textEdited = src.readString();
        this.authorId = src.readString();
        this.authorType = src.readString();
        this.date = src.readLong();
        this.dateEdited = src.readLong();
        this.likeInfo = (LikeInfo) src.readParcelable(cl);
        this.flags = (Flags) src.readParcelable(cl);
        this.repliedToInfo = (RepliedTo) src.readParcelable(cl);
        int attachmentsCount = src.readInt();
        if (attachmentsCount > 0) {
            this.attachments = new Attachment[attachmentsCount];
            src.readTypedArray(this.attachments, Attachment.CREATOR);
        }
        this.mediaMetadata = src.readString();
        int v = src.readInt();
        this.taskId = v == -1 ? null : Integer.valueOf(v);
        int stickersCount = src.readInt();
        if (stickersCount > 0) {
            list = new ArrayList();
        }
        this.replyStickers = list;
        for (int i = 0; i < stickersCount; i++) {
            this.replyStickers.add(new Sticker(src.readString(), src.readInt(), src.readInt(), src.readInt()));
        }
    }

    public boolean hasServerId() {
        return !TextUtils.isEmpty(this.id);
    }

    public boolean hasAttachments() {
        return this.attachments != null && this.attachments.length > 0;
    }

    public String getActualText() {
        return !TextUtils.isEmpty(this.textEdited) ? this.textEdited : this.text;
    }

    public String toString() {
        return getClass().getSimpleName() + " [" + getActualText() + "]";
    }
}
