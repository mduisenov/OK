package ru.ok.java.api.request.friends;

import android.text.TextUtils;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;

@HttpPreamble(hasUserId = false)
public class SuggestionsRequest extends BaseRequest {
    private final String anchor;
    private final int count;
    private final String direction;
    private final String uid;

    public SuggestionsRequest(String direction, String anchor, String uid, int count) {
        this.uid = uid;
        this.anchor = anchor;
        this.direction = direction;
        this.count = count;
    }

    public String getMethodName() {
        return "friends.getSuggestions";
    }

    public void serializeInternal(RequestSerializer<?> serializer) throws SerializeException {
        if (!TextUtils.isEmpty(this.uid)) {
            serializer.add(SerializeParamName.USER_ID, this.uid);
        }
        if (!TextUtils.isEmpty(this.anchor)) {
            serializer.add(SerializeParamName.ANCHOR, this.anchor);
        }
        if (!TextUtils.isEmpty(this.direction)) {
            serializer.add(SerializeParamName.PAGING_DIRECTION, this.direction);
        }
        if (this.count > 0) {
            serializer.add(SerializeParamName.COUNT, this.count);
        }
    }

    public String getUserIdsSupplier() {
        return "friends.getSuggestions.user_ids";
    }
}
