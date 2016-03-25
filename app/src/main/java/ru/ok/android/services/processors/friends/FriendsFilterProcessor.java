package ru.ok.android.services.processors.friends;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.db.provider.OdklContract.UserPrivacySettings;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.json.JsonFriendsFilterParser;
import ru.ok.java.api.request.friends.FriendsFilter;
import ru.ok.java.api.request.friends.FriendsFilterRequest;
import ru.ok.java.api.request.serializer.SerializeParamName;

public final class FriendsFilterProcessor {
    private final ContentResolver contentResolver;

    /* renamed from: ru.ok.android.services.processors.friends.FriendsFilterProcessor.1 */
    static /* synthetic */ class C04541 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$java$api$request$friends$FriendsFilter;

        static {
            $SwitchMap$ru$ok$java$api$request$friends$FriendsFilter = new int[FriendsFilter.values().length];
            try {
                $SwitchMap$ru$ok$java$api$request$friends$FriendsFilter[FriendsFilter.MARK_IN_TOPICS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$java$api$request$friends$FriendsFilter[FriendsFilter.GROUPS_INVITE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public static void fillInputBundle(Bundle inBundle, FriendsFilter filter) {
        if (filter != null) {
            inBundle.putString(SerializeParamName.FILTER.getName(), filter.name());
        }
    }

    public FriendsFilterProcessor(Context context) {
        this.contentResolver = context.getContentResolver();
    }

    @Subscribe(on = 2131623944, to = 2131623981)
    public void requestFriendsFilter(BusEvent event) {
        int resultCode;
        Logger.m172d(">>>");
        FriendsFilter filter = null;
        Bundle input = event.bundleInput;
        Set<String> uids = null;
        try {
            filter = getFilter(input);
        } catch (Throwable e) {
            Logger.m177e("Failed to create request: %s", e);
            Logger.m178e(e);
        }
        if (filter == null) {
            resultCode = 3;
        } else {
            try {
                uids = (Set) new JsonFriendsFilterParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new FriendsFilterRequest(filter))).parse();
                storeResult(this.contentResolver, filter, uids);
                resultCode = 1;
            } catch (Throwable e2) {
                Logger.m177e("Failed to execute request: %s", e2);
                Logger.m178e(e2);
                resultCode = 2;
            }
        }
        Logger.m173d("<<< resultCode=%d uids=%s", Integer.valueOf(resultCode), uids);
        sendResult(resultCode, input, uids);
    }

    private static FriendsFilter getFilter(Bundle bundleInput) {
        String filterStr = bundleInput.getString(SerializeParamName.FILTER.getName());
        if (!TextUtils.isEmpty(filterStr)) {
            return FriendsFilter.valueOf(filterStr);
        }
        throw new IllegalArgumentException("Filter not specified");
    }

    private void sendResult(int resultCode, Bundle input, Collection<String> uids) {
        Bundle output = new Bundle();
        if (uids != null) {
            output.putStringArrayList("friends_filtered_uids", new ArrayList(uids));
        }
        GlobalBus.send(2131624161, new BusEvent(input, output, resultCode));
    }

    public static void storeResult(ContentResolver contentResolver, FriendsFilter filter, Collection<String> uids) {
        int settingId = filterToPrivacySettingId(filter);
        if (settingId != 0) {
            ContentValues[] values = new ContentValues[uids.size()];
            int i = 0;
            for (String uid : uids) {
                ContentValues row = new ContentValues();
                row.put("uid", uid);
                row.put("privacy_mode", Integer.valueOf(1));
                int i2 = i + 1;
                values[i] = row;
                i = i2;
            }
            try {
                Uri privacySettingsUri = UserPrivacySettings.getUri(settingId);
                int rowCount = contentResolver.bulkInsert(UserPrivacySettings.getUri(settingId), values);
                int deletedRows = contentResolver.delete(privacySettingsUri, createSelectionUidNotIn(uids), null);
                Logger.m173d("Stored %d rows to user_privacy_settings, %d rows were deleted", Integer.valueOf(rowCount), Integer.valueOf(deletedRows));
            } catch (Throwable e) {
                Logger.m177e("Failed to store privacy settings: %s", e);
                Logger.m178e(e);
            }
        }
    }

    private static String createSelectionUidNotIn(Collection<String> uids) {
        StringBuilder sb = new StringBuilder((uids.size() + 1) * 15);
        sb.append("uid").append(" NOT IN (");
        boolean addComma = false;
        for (String uid : uids) {
            if (addComma) {
                sb.append(',');
            } else {
                addComma = true;
            }
            sb.append(uid);
        }
        sb.append(')');
        return sb.toString();
    }

    private static int filterToPrivacySettingId(FriendsFilter filter) {
        switch (C04541.$SwitchMap$ru$ok$java$api$request$friends$FriendsFilter[filter.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return 1;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return 4;
            default:
                Logger.m184w("Unsupported filter: " + filter);
                return 0;
        }
    }
}
