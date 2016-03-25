package ru.ok.java.api.request.search;

import android.text.TextUtils;
import ru.ok.java.api.request.BaseRequest;
import ru.ok.java.api.request.paging.PagingDirection;
import ru.ok.java.api.request.serializer.RequestSerializer;
import ru.ok.java.api.request.serializer.SerializeException;
import ru.ok.java.api.request.serializer.SerializeParamName;
import ru.ok.java.api.request.serializer.http.HttpPreamble;
import ru.ok.model.search.SearchResults.SearchContext;
import ru.ok.model.search.SearchType;

@HttpPreamble(hasFormat = true, hasSessionKey = true)
public class SearchQuickRequest extends BaseRequest {
    private final String anchor;
    private final SearchContext context;
    private final int count;
    private final PagingDirection direction;
    private String fields;
    private final String query;
    private final SearchType[] types;

    public SearchQuickRequest(String query, SearchType[] types, SearchContext context, String anchor, PagingDirection direction, int count) {
        this.query = query;
        this.types = types;
        this.context = context;
        this.anchor = anchor;
        this.direction = direction;
        this.count = count;
    }

    public String getMethodName() {
        return "search.quick";
    }

    public void serializeInternal(RequestSerializer<?> serializer) throws SerializeException {
        serializer.add(SerializeParamName.QUERY, this.query);
        if (!(this.types == null || this.types.length <= 0 || (this.types.length == 1 && this.types[0] == SearchType.ALL))) {
            serializer.add(SerializeParamName.TYPES, TextUtils.join(",", this.types));
        }
        if (!(this.context == null || this.context == SearchContext.ALL)) {
            serializer.add(SerializeParamName.CONTEXT, this.context.toString());
        }
        if (!TextUtils.isEmpty(this.anchor)) {
            serializer.add(SerializeParamName.ANCHOR, this.anchor);
        }
        if (!(this.direction == null || this.direction == PagingDirection.FORWARD)) {
            serializer.add(SerializeParamName.DIRECTION, this.direction.getValue());
        }
        if (this.count > 0) {
            serializer.add(SerializeParamName.COUNT, this.count);
        }
        if (!TextUtils.isEmpty(this.fields)) {
            serializer.add(SerializeParamName.FIELDS, this.fields);
        }
    }

    public void setFields(String fields) {
        this.fields = fields;
    }
}
