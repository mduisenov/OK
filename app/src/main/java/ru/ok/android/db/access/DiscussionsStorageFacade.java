package ru.ok.android.db.access;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import java.util.Collection;
import java.util.UUID;
import org.jivesoftware.smack.packet.Message;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.db.base.OfflineTable.Status;
import ru.ok.android.db.provider.OdklProvider;
import ru.ok.android.emoji.smiles.SmileTextProcessor;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.ui.fragments.messages.loaders.data.OfflineData;
import ru.ok.android.ui.fragments.messages.loaders.data.OfflineMessage;
import ru.ok.model.Discussion;
import ru.ok.model.messages.MessageAuthor;
import ru.ok.model.messages.MessageBase.Flags;
import ru.ok.model.messages.MessageBase.RepliedTo;
import ru.ok.model.messages.MessageComment;
import ru.ok.model.messages.MessageComment.MessageCommentBuilder;
import ru.ok.model.stream.LikeInfo;

public final class DiscussionsStorageFacade {
    public static Uri insertComment(String discussionId, String discussionType, MessageAuthor author, long date, String comment, RepliedTo repliedTo, String serverId, boolean silent) {
        String str;
        String str2 = null;
        ContentValues cv = new ContentValues();
        cv.put(Message.ELEMENT, comment);
        cv.put("discussion_id", discussionId);
        cv.put("discussion_type", discussionType);
        String str3 = "reply_to_comment_id";
        if (repliedTo != null) {
            str = repliedTo.messageId;
        } else {
            str = null;
        }
        cv.put(str3, str);
        str3 = "reply_to_id";
        if (repliedTo != null) {
            str = repliedTo.authorId;
        } else {
            str = null;
        }
        cv.put(str3, str);
        str = "reply_to_type";
        if (repliedTo != null) {
            str2 = repliedTo.authorType;
        }
        cv.put(str, str2);
        cv.put("deletion_allowed", Boolean.valueOf(true));
        cv.put("author_id", author.getId());
        cv.put("author_type", author.getType());
        cv.put("_date", Long.valueOf(date));
        cv.put(NotificationCompat.CATEGORY_STATUS, Status.WAITING.name());
        cv.put("server_id", serverId);
        cv.put("uuid", UUID.randomUUID().toString());
        return OdnoklassnikiApplication.getContext().getContentResolver().insert(silent ? OdklProvider.commentsSilentUri() : OdklProvider.commentsUri(), cv);
    }

    public static Cursor queryComment(Context context, int commentId) {
        return context.getContentResolver().query(OdklProvider.commentUri((long) commentId), new String[]{"_id", NotificationCompat.CATEGORY_STATUS, Message.ELEMENT}, null, null, null);
    }

    public static void updateComment(Context context, int commentId, ContentValues cv) {
        context.getContentResolver().update(OdklProvider.commentUri((long) commentId), cv, null, null);
    }

    public static Cursor queryComments(Discussion info) {
        return OdnoklassnikiApplication.getContext().getContentResolver().query(OdklProvider.commentsUri(), null, "discussion_id = ? AND discussion_type = ?", new String[]{info.id, info.type}, "_date ASC");
    }

    public static void deleteComments(Collection<Integer> commentIds) {
        if (!commentIds.isEmpty()) {
            OdnoklassnikiApplication.getContext().getContentResolver().delete(OdklProvider.commentsUri(), "_id IN (" + TextUtils.join(", ", commentIds) + ")", null);
        }
    }

    public static OfflineMessage<MessageComment> cursor2Comment(Cursor cursor) {
        MessageCommentBuilder builder = new MessageCommentBuilder();
        String id = cursor.getString(cursor.getColumnIndex("server_id"));
        String text = cursor.getString(cursor.getColumnIndex(Message.ELEMENT));
        String textEdited = cursor.getString(cursor.getColumnIndex("message_edited"));
        String authorId = cursor.getString(cursor.getColumnIndex("author_id"));
        String authorType = cursor.getString(cursor.getColumnIndex("author_type"));
        long date = cursor.getLong(cursor.getColumnIndex("_date"));
        int likesCount = cursor.getInt(cursor.getColumnIndex("likes_count"));
        boolean isLiked = cursor.getInt(cursor.getColumnIndex("is_liked")) > 0;
        String likeId = cursor.getString(cursor.getColumnIndex("like_id"));
        long lastLikeDate = cursor.getLong(cursor.getColumnIndex("like_last_date"));
        boolean likeAllowed = cursor.getInt(cursor.getColumnIndex("like_allowed")) > 0;
        builder.setId(id).setText(text).setTextEdited(textEdited).setAuthorId(authorId).setAuthorType(authorType).setDate(date).setLikeInfo(new LikeInfo(likesCount, isLiked, lastLikeDate, likeId, likeAllowed, likeAllowed)).setFlags(new Flags(likeAllowed, cursor.getInt(cursor.getColumnIndex("mark_as_spam_allowed")) > 0, cursor.getInt(cursor.getColumnIndex("deletion_allowed")) > 0, cursor.getInt(cursor.getColumnIndex("block_allowed")) > 0, false, false, SmileTextProcessor.isSticker(text))).setRepliedTo(new RepliedTo(cursor.getString(cursor.getColumnIndex("reply_to_comment_id")), cursor.getString(cursor.getColumnIndex("reply_to_id")), cursor.getString(cursor.getColumnIndex("reply_to_type"))));
        return new OfflineMessage(builder.build(), new OfflineData(cursor.getInt(cursor.getColumnIndex("_id")), Status.valueOf(cursor.getString(cursor.getColumnIndex(NotificationCompat.CATEGORY_STATUS))), ErrorType.safeValueOf(cursor.getString(cursor.getColumnIndex("failure_reason")))));
    }
}
