package ru.ok.model.stream.message;

import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import ru.ok.android.utils.Logger;
import ru.ok.model.GeneralUserInfo;
import ru.ok.model.GroupInfo;
import ru.ok.model.UserInfo;
import ru.ok.model.stream.entities.BaseEntity;
import ru.ok.model.stream.entities.FeedGroupEntity;
import ru.ok.model.stream.entities.FeedUserEntity;
import ru.ok.model.stream.message.FeedMessageParser.FeedMessageParserCallback;

public final class FeedMessageBuilder {

    /* renamed from: ru.ok.model.stream.message.FeedMessageBuilder.1 */
    static class C16331 implements FeedMessageParserCallback {
        final /* synthetic */ StringBuilder val$messageSb;
        final /* synthetic */ ArrayList val$spans;

        C16331(StringBuilder stringBuilder, ArrayList arrayList) {
            this.val$messageSb = stringBuilder;
            this.val$spans = arrayList;
        }

        public void addText(String text) {
            this.val$messageSb.append(text);
        }

        public void addUser(String text, String uid, String ref) {
            FeedMessageBuilder.addSpannedString(this.val$messageSb, text, this.val$spans, new FeedEntitySpan(7, uid, ref));
        }

        public void addGroup(String text, String gid, String ref) {
            FeedMessageBuilder.addSpannedString(this.val$messageSb, text, this.val$spans, new FeedEntitySpan(2, gid, ref));
        }

        public void addUserAlbum(String text, String albumId, String ref) {
            FeedMessageBuilder.addSpannedString(this.val$messageSb, text, this.val$spans, new FeedEntitySpan(8, albumId, ref));
        }

        public void addApp(String text, String appId, String ref) {
            FeedMessageBuilder.addSpannedString(this.val$messageSb, text, this.val$spans, new FeedEntitySpan(1, appId, ref));
        }

        public void addPlaylist(String text, String playlistId, String ref) {
            FeedMessageBuilder.addSpannedString(this.val$messageSb, text, this.val$spans, new FeedEntitySpan(18, playlistId, ref));
        }
    }

    public static void addSpannedString(StringBuilder sb, CharSequence text, List<FeedMessageSpan> spans, FeedMessageSpan span) {
        int startOffset = sb.length();
        sb.append(text);
        int endOffset = sb.length();
        span.setStartIndex(startOffset);
        span.setEndIndex(endOffset);
        spans.add(span);
    }

    @NonNull
    public static FeedMessage buildMessage(String message) {
        try {
            return buildMessage(new JSONArray(message));
        } catch (Exception e) {
            Logger.m180e(e, "Failed to parse message tokens: %s", message);
            return new FeedMessage("", new ArrayList());
        }
    }

    @NonNull
    public static FeedMessage buildMessage(JSONArray messageTokens) {
        StringBuilder messageSb = new StringBuilder();
        ArrayList<FeedMessageSpan> spans = new ArrayList();
        try {
            FeedMessageParser.parseFeedMessage(messageTokens, new C16331(messageSb, spans));
        } catch (Exception e) {
            Logger.m180e(e, "Failed to parse message: %s", messageTokens);
        }
        return new FeedMessage(messageSb.toString(), spans);
    }

    public static FeedMessage buildMessageFromAuthor(BaseEntity author, List<GeneralUserInfo> outUserInfos) {
        StringBuilder messageSb = new StringBuilder();
        ArrayList<FeedMessageSpan> spans = new ArrayList();
        if (author == null) {
            return null;
        }
        if (author instanceof FeedUserEntity) {
            UserInfo userInfo = ((FeedUserEntity) author).getUserInfo();
            if (outUserInfos != null) {
                outUserInfos.add(userInfo);
            }
            addSpannedString(messageSb, userInfo.getConcatName(), spans, new FeedEntitySpan(7, userInfo.getId(), null));
        } else if (author instanceof FeedGroupEntity) {
            GroupInfo groupInfo = ((FeedGroupEntity) author).getGroupInfo();
            if (outUserInfos != null) {
                outUserInfos.add(groupInfo);
            }
            addSpannedString(messageSb, groupInfo.getName(), spans, new FeedEntitySpan(2, groupInfo.getId(), null));
        }
        return new FeedMessage(messageSb.toString(), spans);
    }
}
