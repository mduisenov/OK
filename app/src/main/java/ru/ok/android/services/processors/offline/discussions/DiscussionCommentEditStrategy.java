package ru.ok.android.services.processors.offline.discussions;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import java.util.Map;
import ru.ok.android.services.processors.offline.OfflineBaseSendProcessor.MessageProcessStrategy;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.json.JsonResultParser;
import ru.ok.java.api.json.messages.JsonMessageEditParser;
import ru.ok.java.api.request.discussions.DiscussionEditCommentRequest;

final class DiscussionCommentEditStrategy implements MessageProcessStrategy<DiscussionEditCommentRequest, String> {
    DiscussionCommentEditStrategy() {
    }

    public DiscussionEditCommentRequest createRequest(Cursor cursor) {
        return new DiscussionEditCommentRequest(cursor.getString(cursor.getColumnIndex("discussion_id")), cursor.getString(cursor.getColumnIndex("discussion_type")), cursor.getString(cursor.getColumnIndex("server_id")), cursor.getString(cursor.getColumnIndex("message_edited")));
    }

    public JsonResultParser<String> createParser(JsonHttpResult result) {
        return new JsonMessageEditParser(result);
    }

    public void onItemPostUpdate(Context context, Map<String, String> map, String response) {
    }

    public void fillValuesByResult(ContentValues cv, String parsed) {
        cv.put("server_id", parsed);
    }

    public void removeExistingDuplicates(Map<String, String> map, String response) {
    }
}
