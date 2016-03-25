package ru.ok.android.services.processors.mediatopic;

import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.ok.android.services.processors.base.BaseProcessorResult;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.java.api.json.mediatopics.JsonArrayGroupTagsParser;
import ru.ok.java.api.request.mediatopic.MediaTopicGetGroupTagsRequest;
import ru.ok.model.groups.GroupTag;

public class MediaTopicGetGroupTagsProcessor {

    public static class Result extends BaseProcessorResult {
        public final String anchor;
        public final List<GroupTag> tags;

        public Result(boolean isSuccess, ErrorType errorType, List<GroupTag> tags, String anchor) {
            super(isSuccess, errorType);
            this.tags = tags;
            this.anchor = anchor;
        }
    }

    public static Result getGroupTags(String gid, String anchor, Integer count) {
        try {
            JSONObject resultObject = JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new MediaTopicGetGroupTagsRequest(gid, anchor, count)).getResultAsObject();
            JSONArray tagsArray = resultObject.optJSONArray("tags");
            List<GroupTag> tags = null;
            if (tagsArray != null) {
                tags = new JsonArrayGroupTagsParser(tagsArray).parse();
            }
            return new Result(true, null, tags, resultObject.getString("anchor"));
        } catch (Exception e) {
            return new Result(false, ErrorType.fromException(e), null, null);
        }
    }
}
