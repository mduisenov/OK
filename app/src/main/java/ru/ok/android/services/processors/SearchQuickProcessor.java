package ru.ok.android.services.processors;

import android.os.Bundle;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.services.transport.exception.NetworkException;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.json.search.SearchQuickParser;
import ru.ok.java.api.request.groups.GroupInfoRequest;
import ru.ok.java.api.request.paging.PagingDirection;
import ru.ok.java.api.request.search.SearchQuickRequest;
import ru.ok.java.api.request.users.UserInfoRequest.FIELDS;
import ru.ok.java.api.utils.fields.RequestFieldsBuilder;
import ru.ok.model.search.SearchResults;
import ru.ok.model.search.SearchResults.SearchContext;
import ru.ok.model.search.SearchType;

public final class SearchQuickProcessor {
    public static SearchResults performSearch(String query, SearchType[] types, SearchContext context, String anchor, PagingDirection direction, int count) throws Exception {
        SearchQuickRequest request = new SearchQuickRequest(query, types, context, anchor, direction, count);
        RequestFieldsBuilder builder = new RequestFieldsBuilder();
        StringBuilder fields = new StringBuilder();
        if (shouldRequestUserFields(types)) {
            builder.withPrefix("user.");
            builder.addFields(FIELDS.UID, FIELDS.FIRST_NAME, FIELDS.LAST_NAME, FIELDS.GENDER, FIELDS.ONLINE, FIELDS.CAN_VIDEO_CALL, FIELDS.CAN_VIDEO_MAIL, FIELDS.AGE, FIELDS.LOCATION, FIELDS.PRIVATE, FIELDS.PIC_HDPI, FIELDS.PRIVATE, FIELDS.PIC_MDPI, FIELDS.PIC_XHDPI, FIELDS.PIC_XXHDPI, FIELDS.AGE, FIELDS.LAST_ONLINE, FIELDS.SHOW_LOCK);
            fields.append(builder.build());
            builder.clear();
        }
        if (shouldRequestGroupFields(types)) {
            if (fields.length() > 0) {
                fields.append(",");
            }
            builder.withPrefix("group.");
            builder.addFields(GroupInfoRequest.FIELDS.GROUP_ID, GroupInfoRequest.FIELDS.GROUP_NAME, GroupInfoRequest.FIELDS.GROUP_DESCRIPTION, GroupInfoRequest.FIELDS.GROUP_PIC_AVATAR, GroupInfoRequest.FIELDS.GROUP_MEMBERS_COUNT, GroupInfoRequest.FIELDS.GROUP_PRIVATE, GroupInfoRequest.FIELDS.GROUP_PREMIUM);
            fields.append(builder.build());
        }
        request.setFields(fields.toString());
        return SearchQuickParser.parse(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(request).getResultAsObject());
    }

    @Subscribe(on = 2131623944, to = 2131624113)
    public void process(BusEvent event) {
        Bundle bundleInput = event.bundleInput;
        String query = bundleInput.getString("sqquery");
        SearchType[] types = (SearchType[]) bundleInput.getParcelableArray("sqtypes");
        SearchContext context = SearchContext.ALL;
        if (bundleInput.containsKey("sqcontext")) {
            context = (SearchContext) bundleInput.getSerializable("sqcontext");
        }
        String anchor = bundleInput.getString("sqanchor");
        int count = bundleInput.getInt("sqcount");
        PagingDirection direction = PagingDirection.values()[bundleInput.getInt("sqdirection")];
        int resultCode = -2;
        Bundle bundleOutput = new Bundle();
        try {
            bundleOutput.putParcelable("sqresult", performSearch(query, types, context, anchor, direction, count));
            resultCode = -1;
        } catch (NetworkException e) {
            resultCode = -3;
        } catch (Throwable exc) {
            Logger.m178e(exc);
        }
        GlobalBus.send(2131624257, new BusEvent(bundleInput, bundleOutput, resultCode));
    }

    private static boolean shouldRequestUserFields(SearchType[] types) {
        if (types == null) {
            return true;
        }
        for (SearchType type : types) {
            if (type == SearchType.USER || type == SearchType.ALL) {
                return true;
            }
        }
        return false;
    }

    private static boolean shouldRequestGroupFields(SearchType[] types) {
        if (types == null) {
            return true;
        }
        for (SearchType type : types) {
            if (type == SearchType.GROUP || type == SearchType.COMMUNITY || type == SearchType.ALL) {
                return true;
            }
        }
        return false;
    }
}
